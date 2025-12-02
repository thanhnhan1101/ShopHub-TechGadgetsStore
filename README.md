# ShopHub — Tech Gadgets Store

This repository is a small full-stack sample intended for a CV/portfolio.
It includes a Spring Boot backend (REST + JPA) and a Vue 3 frontend (Vite).
The backend is configured to use Microsoft SQL Server (JDBC).

## Layout
- backend/ — Spring Boot app (port 8080)
- frontend/ — Vue 3 app (Vite dev server, port 5173)
- init_db.sql — example SQL Server DB creation script

## Quick start (development)

1) Prepare SQL Server
- Ensure SQL Server is running locally or accessible.
- Create database `shophub` (see `init_db.sql`) or update connection.

2) Configure backend
- Edit `backend/src/main/resources/application.properties` and set the correct
  `spring.datasource.username` and `spring.datasource.password` and `url`.

3) Run backend
- From project root (this README) run:

```powershell
cd backend
mvn spring-boot:run
```

The backend REST API will be available at `http://localhost:8080/api/products`.

4) Run frontend
- In another terminal:

```powershell
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173` to see the simple UI. The UI posts/fetches
products from the backend.

## What to include in your CV
- Mention: "ShopHub — Tech Gadgets Store: Full-stack demo with Spring Boot (REST + JPA), Vue 3 + Vite, and SQL Server persistence."
- Optionally include a screenshot and a link to the repository or demo.

## Notes / next steps
- Add authentication (JWT), file uploads for product images.
- Add unit/integration tests and CI pipeline.
- Dockerize backend + SQL Server for an easy demo setup.
