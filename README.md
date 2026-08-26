# RouteMaster — Logistics Management System

RouteMaster is a full-stack logistics management platform that handles customer and employee records, parcel tracking, and route assignment for a delivery operation — with a built-in route optimization engine that clusters parcels geographically and assigns them to routes based on vehicle capacity and distance.

It's a Java/Spring Boot REST API backed by MySQL, secured with JWT authentication, with a React frontend for day-to-day operational use.

## Why this exists

Most sample logistics/CRUD projects stop at "create, read, update, delete." RouteMaster goes a step further: it includes a real route-optimization service that groups parcels by pincode, calculates distances using the Haversine formula, and assigns parcels to routes under capacity constraints — the kind of problem a real delivery operation actually has to solve, not just a demo of REST conventions.

## Features

- **Customer, Employee, Parcel, and Route management** — full CRUD across all core entities
- **Parcel tracking** — separate tracking records and status lookups per parcel
- **Route optimization engine**
  - Clusters parcels by pincode into geographic groups
  - Computes real-world distances between locations using the Haversine formula (great-circle distance)
  - Assigns parcels to routes under configurable vehicle capacity constraints
  - Exposes health-check, diagnostics, and stats endpoints for monitoring the optimizer itself
- **JWT-based authentication** — registration, login, and token-secured endpoints across the API
- **Operational dashboard** — endpoint-driven counts and summaries (customers, parcels, payments, route load) for a live operations view
- **React frontend** — a working UI on top of the API for day-to-day use, not just a Postman collection

## Tech Stack

**Backend:** Java 17, Spring Boot 3.2, Spring Security, Spring Data JPA, MySQL
**Auth:** JWT (jjwt)
**Frontend:** React 19, React-Bootstrap
**Build:** Maven

## API Overview

| Area | Base Path | Examples |
|---|---|---|
| Auth | `/api/auth` | `register`, `login` |
| Customers | `/api/customers` | create, update, list, delete, `me` |
| Employees | `/api/employees` | create, update, list, delete |
| Parcels | `/api/parcels` | create, update, list, delete, `my-parcels`, `last-month`, `delayed` |
| Routes | `/api/routes` | create, update, list, delete |
| Parcel Tracking | `/api/track` | create, update, list, delete |
| Route Optimizer | `/api/optimizer` | `health`, `diagnostics`, `optimize-routes`, `optimize-with-clustering`, `stats` |
| Dashboard | `/dashboard` | `customercount`, `parcelscount`, `parcelpayment`, `parcelroutecount` |

## Getting Started

### Prerequisites
- Java 17+
- Maven
- MySQL
- Node.js + npm (for the frontend)

### Backend

```bash
# Configure your database credentials in src/main/resources/application.properties
mvn clean install
mvn spring-boot:run
```

The API will start on the configured port (default Spring Boot port unless overridden in `application.properties`).

### Frontend

```bash
cd routemaster-frontend/routemaster-frontend
npm install
npm start
```

## Project Structure

```
src/main/java/com/routemasterapi/api/
├── controller/     # REST endpoints (auth, customers, employees, parcels, routes, tracking, optimizer, dashboard)
├── service/        # Business logic, including the route optimization engine
├── entity/         # JPA entities
├── repositories/   # Spring Data JPA repositories
├── security/       # JWT filter and token utilities
├── model/          # Request/response DTOs
└── config/         # Security configuration
```

## License

See [LICENSE](LICENSE).
