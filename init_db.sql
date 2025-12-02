-- ===================================================================
-- ShopHub - Tech Gadgets Store Database Schema
-- SQL Server 2016+ 
-- Run in sqlcmd or SSMS as an admin user (sa or db_owner)
-- ===================================================================

-- Create database
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'shophub')
BEGIN
    CREATE DATABASE shophub;
END
GO

USE shophub;
GO

-- Optional: Create dedicated login/user for the application
-- Uncomment if you want a separate user instead of using 'sa'
/*
IF NOT EXISTS (SELECT name FROM sys.server_principals WHERE name = 'shophub_user')
BEGIN
    CREATE LOGIN shophub_user WITH PASSWORD = 'StrongPassword@2025!';
END

IF NOT EXISTS (SELECT name FROM sys.database_principals WHERE name = 'shophub_user')
BEGIN
    CREATE USER shophub_user FOR LOGIN shophub_user;
    ALTER ROLE db_datareader ADD MEMBER shophub_user;
    ALTER ROLE db_datawriter ADD MEMBER shophub_user;
    -- Grant execute permissions for stored procedures if needed
    GRANT EXECUTE TO shophub_user;
END
*/
GO

-- ===================================================================
-- Table: USERS
-- Stores customer and admin user accounts
-- ===================================================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[USERS]') AND type in (N'U'))
BEGIN
    CREATE TABLE USERS (
        ID INT IDENTITY(1,1) PRIMARY KEY,
        FULL_NAME NVARCHAR(100) NOT NULL,  -- increased from 30 for international names
        EMAIL VARCHAR(100) UNIQUE NOT NULL,  -- increased from 50
        PASSWORD_HASH VARCHAR(255) NOT NULL,  -- bcrypt/argon2 needs 60-255 chars, not 50!
        PHONE VARCHAR(20),  -- increased from 10 to support international formats (+84, etc)
        ROLE VARCHAR(20) CHECK (ROLE IN ('ADMIN','CUSTOMER')) NOT NULL DEFAULT 'CUSTOMER',
        IS_ACTIVE BIT NOT NULL DEFAULT 1,  -- soft delete / account suspension
        CREATED_AT DATETIME2 NOT NULL DEFAULT GETDATE(),  -- DATETIME2 more precise than DATETIME
        UPDATED_AT DATETIME2 NULL
    );

    CREATE INDEX IDX_USERS_EMAIL ON USERS(EMAIL);
    CREATE INDEX IDX_USERS_ROLE ON USERS(ROLE) WHERE IS_ACTIVE = 1;  -- filtered index for active users
END
GO

-- ===================================================================
-- Table: CATEGORIES
-- Product categories (e.g., Smartphones, Laptops, Accessories)
-- ===================================================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[CATEGORIES]') AND type in (N'U'))
BEGIN
    CREATE TABLE CATEGORIES (
        ID INT IDENTITY(1,1) PRIMARY KEY,
        NAME NVARCHAR(50) UNIQUE NOT NULL,  -- increased from 25
        DESCRIPTION NVARCHAR(500),  -- increased from 100
        IMAGE_URL VARCHAR(255) NULL,  -- category image for UI
        IS_ACTIVE BIT NOT NULL DEFAULT 1,
        CREATED_AT DATETIME2 NOT NULL DEFAULT GETDATE()
    );

    CREATE INDEX IDX_CATEGORIES_ACTIVE ON CATEGORIES(IS_ACTIVE);
END
GO

-- ===================================================================
-- Table: PRODUCTS
-- Tech gadgets inventory
-- ===================================================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[PRODUCTS]') AND type in (N'U'))
BEGIN
    CREATE TABLE PRODUCTS (
        ID INT IDENTITY(1,1) PRIMARY KEY,
        CATEGORY_ID INT NOT NULL,
        NAME NVARCHAR(200) NOT NULL,  -- increased from 50
        DESCRIPTION NVARCHAR(MAX),
        PRICE DECIMAL(18,2) NOT NULL CHECK (PRICE >= 0),  -- price validation
        STOCK INT NOT NULL DEFAULT 0 CHECK (STOCK >= 0),  -- stock validation
        IMAGE_URL VARCHAR(500),  -- increased from 255 for CDN URLs
        IS_ACTIVE BIT NOT NULL DEFAULT 1,  -- soft delete
        CREATED_AT DATETIME2 NOT NULL DEFAULT GETDATE(),
        UPDATED_AT DATETIME2 NULL,

        CONSTRAINT FK_PRODUCTS_CATEGORY FOREIGN KEY (CATEGORY_ID) 
            REFERENCES CATEGORIES(ID) ON DELETE CASCADE  -- cascade delete
    );

    CREATE INDEX IDX_PRODUCTS_CATEGORY ON PRODUCTS(CATEGORY_ID) WHERE IS_ACTIVE = 1;
    CREATE INDEX IDX_PRODUCTS_PRICE ON PRODUCTS(PRICE) WHERE IS_ACTIVE = 1;  -- for price filtering
    CREATE INDEX IDX_PRODUCTS_STOCK ON PRODUCTS(STOCK) WHERE IS_ACTIVE = 1;  -- for inventory queries
END
GO

-- ===================================================================
-- Table: CART_ITEMS
-- Shopping cart (one cart per user)
-- ===================================================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[CART_ITEMS]') AND type in (N'U'))
BEGIN
    CREATE TABLE CART_ITEMS (
        ID INT IDENTITY(1,1) PRIMARY KEY,
        USER_ID INT NOT NULL,
        PRODUCT_ID INT NOT NULL,
        QUANTITY INT NOT NULL CHECK (QUANTITY > 0),
        ADDED_AT DATETIME2 NOT NULL DEFAULT GETDATE(),  -- track when item added to cart

        CONSTRAINT FK_CART_USER FOREIGN KEY (USER_ID) 
            REFERENCES USERS(ID) ON DELETE CASCADE,
        CONSTRAINT FK_CART_PRODUCT FOREIGN KEY (PRODUCT_ID) 
            REFERENCES PRODUCTS(ID) ON DELETE CASCADE,
        CONSTRAINT UQ_CART_USER_PRODUCT UNIQUE(USER_ID, PRODUCT_ID)  -- prevent duplicate items in cart
    );

    CREATE INDEX IDX_CART_USER ON CART_ITEMS(USER_ID);
    CREATE INDEX IDX_CART_PRODUCT ON CART_ITEMS(PRODUCT_ID);
END
GO

-- ===================================================================
-- Table: ORDERS
-- Customer orders
-- ===================================================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ORDERS]') AND type in (N'U'))
BEGIN
    CREATE TABLE ORDERS (
        ID INT IDENTITY(1,1) PRIMARY KEY,
        USER_ID INT NOT NULL,
        FULL_NAME NVARCHAR(100) NOT NULL,  -- increased from 50
        ADDRESS NVARCHAR(500) NOT NULL,  -- increased from 255 for detailed address
        PHONE VARCHAR(20) NOT NULL,  -- increased from 10
        TOTAL_AMOUNT DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (TOTAL_AMOUNT >= 0),  -- denormalized for performance
        ORDER_DATE DATETIME2 NOT NULL DEFAULT GETDATE(),
        STATUS VARCHAR(20) CHECK (STATUS IN ('PENDING','PAID','SHIPPED','DELIVERED','CANCELLED'))
               NOT NULL DEFAULT 'PENDING',  -- added DELIVERED status
        PAYMENT_METHOD VARCHAR(50),  -- COD, Credit Card, E-wallet, etc
        NOTES NVARCHAR(500),  -- customer notes
        UPDATED_AT DATETIME2 NULL,

        CONSTRAINT FK_ORDERS_USER FOREIGN KEY (USER_ID) 
            REFERENCES USERS(ID) ON DELETE NO ACTION  -- keep order history even if user deleted
    );

    CREATE INDEX IDX_ORDERS_USER ON ORDERS(USER_ID);
    CREATE INDEX IDX_ORDERS_STATUS ON ORDERS(STATUS);
    CREATE INDEX IDX_ORDERS_DATE ON ORDERS(ORDER_DATE DESC);  -- for recent orders query
END
GO

-- ===================================================================
-- Table: ORDER_ITEMS
-- Line items for each order
-- ===================================================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ORDER_ITEMS]') AND type in (N'U'))
BEGIN
    CREATE TABLE ORDER_ITEMS (
        ID INT IDENTITY(1,1) PRIMARY KEY,
        ORDER_ID INT NOT NULL,
        PRODUCT_ID INT NOT NULL,
        PRODUCT_NAME NVARCHAR(200) NOT NULL,  -- denormalized: snapshot of product name at order time
        PRICE DECIMAL(18,2) NOT NULL CHECK (PRICE >= 0),  -- snapshot of price at order time
        QUANTITY INT NOT NULL CHECK (QUANTITY > 0),

        CONSTRAINT FK_ORDER_ITEMS_ORDER FOREIGN KEY (ORDER_ID) 
            REFERENCES ORDERS(ID) ON DELETE CASCADE,
        CONSTRAINT FK_ORDER_ITEMS_PRODUCT FOREIGN KEY (PRODUCT_ID) 
            REFERENCES PRODUCTS(ID) ON DELETE NO ACTION  -- keep reference even if product deleted
    );

    CREATE INDEX IDX_ORDER_ITEMS_ORDER ON ORDER_ITEMS(ORDER_ID);
    CREATE INDEX IDX_ORDER_ITEMS_PRODUCT ON ORDER_ITEMS(PRODUCT_ID);
END
GO

-- ===================================================================
-- Sample Data (Optional - for demo/development)
-- ===================================================================
-- Insert default admin user (password: admin123)
IF NOT EXISTS (SELECT 1 FROM USERS WHERE EMAIL = 'admin@shophub.com')
BEGIN
    INSERT INTO USERS (FULL_NAME, EMAIL, PASSWORD_HASH, ROLE) 
    VALUES (N'Admin User', 'admin@shophub.com', '$2a$10$SAMPLE_BCRYPT_HASH', 'ADMIN');
END

-- Insert sample categories
IF NOT EXISTS (SELECT 1 FROM CATEGORIES)
BEGIN
    INSERT INTO CATEGORIES (NAME, DESCRIPTION) VALUES
    (N'Smartphones', N'Latest smartphones and mobile devices'),
    (N'Laptops', N'Laptops and notebook computers'),
    (N'Headphones', N'Wireless and wired headphones'),
    (N'Smartwatches', N'Fitness trackers and smartwatches'),
    (N'Cameras', N'Action cameras and photography equipment'),
    (N'Accessories', N'Chargers, cables, cases and other accessories');
END
GO

PRINT 'Database schema created successfully!';
PRINT 'Remember to:';
PRINT '1. Update password hashes with real bcrypt/argon2 hashes (not plain text)';
PRINT '2. Configure application.properties with correct connection string';
PRINT '3. Review and adjust field lengths based on your business requirements';
GO
