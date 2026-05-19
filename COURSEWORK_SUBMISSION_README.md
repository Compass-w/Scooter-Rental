# ScooterGo Coursework Submission README

This file is intended for markers who need to install, configure, run, and assess
the submitted ScooterGo codebase. The main project README is also included, but
this document focuses on the CW2 final deliverable checklist.

## Live Demo

The deployed coursework demo is currently available at:

- https://scootergo.top:3000

Use the demo accounts below to check both customer and manager/admin functions.

## 1. Project Contents

```text
backend/        Spring Boot 3 backend API, services, security, tests
frontend/       uni-app / Vue 3 frontend source code
database/       PostgreSQL schema and seed data for the coursework demo
docker-compose.yml
.env.example    Safe environment template; copy this to .env before running
README.md       General project documentation
```

The real `.env`, dependency folders, local build output, and Git metadata are not
required for marking and should not be submitted. The database is supplied as SQL:

- `database/migrations/001_production_schema.sql` creates all tables.
- `database/seeds/dev_seed.sql` creates demo accounts, scooters, bookings, and a
  masked card record.

Docker Compose applies both files automatically when the PostgreSQL volume is
first initialized.

## 2. Demo Accounts

| Role | Username | Password | Purpose |
| --- | --- | --- | --- |
| Customer | `student1` | `123456` | Browse scooters, book rides, view profile, cards, and history |
| Manager | `manager1` | `Tyz114031!` | Access admin dashboard, fleet management, POS booking, analytics |

## 3. Recommended Installation: Docker Compose

Prerequisite: Docker Desktop or another Docker Compose compatible runtime.

1. Copy the environment template:

   ```bash
   cp .env.coursework .env
   ```

2. The included `.env.coursework` is a safe demo configuration for local marking.
   It contains no real SMTP, SMS, or production secrets. If you instead use
   `.env.example`, set `POSTGRES_PASSWORD` and `DB_PASSWORD` to the same value.
   Keep:

   ```text
   BOOTSTRAP_DEFAULT_MANAGER_ENABLED=false
   PASSWORD_RESET_MANUAL_LINK_FALLBACK_ENABLED=true
   EMAIL_NOTIFICATIONS_ENABLED=false
   ```

3. Start the full system:

   ```bash
   docker compose up --build
   ```

4. Open:

   - Frontend: <http://localhost:3000>
   - Live deployed demo: <https://scootergo.top:3000>
   - Backend API: <http://localhost:8080>
   - Swagger UI: <http://localhost:8080/swagger-ui/index.html>

5. To reset the demo database:

   ```bash
   docker compose down -v
   docker compose up --build
   ```

## 4. Manual Installation

Prerequisites:

- Java 17 or later
- Maven 3.9 or later
- Node.js 18 or later and npm
- PostgreSQL 16 or compatible

Create the database and apply the SQL files in this order:

```bash
psql -U postgres -c "CREATE DATABASE scooter_rental;"
psql -U postgres -d scooter_rental -f database/migrations/001_production_schema.sql
psql -U postgres -d scooter_rental -f database/seeds/dev_seed.sql
```

Start the backend:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/scooter_rental
export DB_USERNAME=scooter_user
export DB_PASSWORD=<your-password>
export PASSWORD_RESET_URL_TEMPLATE=http://localhost:3000/#/pages/reset-password?token={token}
export PASSWORD_RESET_MANUAL_LINK_FALLBACK_ENABLED=true
export BOOTSTRAP_DEFAULT_MANAGER_ENABLED=false

cd backend
mvn spring-boot:run
```

Start the frontend in a second terminal:

```bash
cd frontend
npm install
npm run dev:h5
```

Production frontend build:

```bash
cd frontend
npm run build:h5
```

## 5. Verification Performed Before Packaging

These checks passed on 2026-05-19:

```bash
cd backend && mvn test
# Tests run: 19, Failures: 0, Errors: 0, Skipped: 0

cd frontend && npm run build:h5
# DONE Build complete.
```

## 6. CW2 Backlog Feature Coverage

| ID | Requirement | Evidence in this codebase |
| --- | --- | --- |
| 1 | User accounts and login | `AuthController`, `UserService`, signup/login/reset pages |
| 2 | Store customer card details | `BankCardController`, `bank_cards` table, profile wallet UI |
| 3 | Good security for user accounts/cards | BCrypt password hashing, bearer session filter, role checks, masked card numbers |
| 4 | View hire options and cost | Home pricing section and `BookingOptions.vue` plan selector |
| 5 | Book e-scooter by ID and hire period | `BookingController.startRide`, scooter map/list booking flow |
| 6 | Simulated card payment | `PaymentGatewayService`, payment transaction table, booking payment state |
| 7 | Send booking confirmation by email | `NotificationService`, booking confirmation audit event, admin resend action |
| 8 | Store/display booking confirmation/history | `bookings` table, profile trips, active ride summary |
| 9 | Staff bookings for unregistered users | Admin POS booking flow, `staff_bookings` table |
| 10 | Update scooter status after booking | `BookingService` updates scooter availability and ride state |
| 11 | Extend current booking | `POST /api/bookings/{bookingId}/extend`, active ride UI |
| 12 | Cancel booking | `POST /api/bookings/cancel`, profile booking actions |
| 13 | Submit issue/fault feedback | `IssueController`, issue reporting UI and `issue_reports` table |
| 14 | Prioritise/escalate feedback | `IssueReportService` and admin issue workflow fields |
| 15 | View high-priority issues | Admin issue list and priority filters/summary |
| 16 | Configure scooter details/costs | Admin scooter create/update/status endpoints and dashboard UI |
| 17 | Display availability/location | Scooter list, map data, `GET /api/scooters/available` |
| 18 | Visual map of scooter locations | `frontend/static/scooter-map.html` and find-scooter map screen |
| 19 | Weekly income by hire option | Admin analytics weekly revenue table |
| 20 | Daily income over week | Admin analytics daily revenue table |
| 21 | Graph weekly income | Admin dashboard line/bar SVG charts |
| 22 | Discounts for frequent/student/senior users | `BookingService.resolveDiscountOutcome`, admin discount rules |
| 23 | Multiple clients simultaneously | Stateless API sessions, PostgreSQL backend, Dockerized deployment |
| 24 | Responsive UI | uni-app/Vue layouts with mobile and desktop responsive styles |
| 25 | Accessibility color/font considerations | Consistent typography, visible controls, high-contrast admin/user screens |

## 7. Notes For Maximum-Marks Submission

- Include the development documents required by the marking scheme separately:
  requirements report, design report, and test report.
- Include Sprint 1-4 demo slides/videos separately if the submission portal asks
  for them. The code archive only covers the final software deliverable.
- For the software system section, demonstrate both roles: log in as `student1`
  for the customer journey and `manager1` for admin analytics/fleet/POS flows.
- During marking, run with Docker Compose first. It is the shortest path because
  it creates PostgreSQL, loads the schema, seeds demo data, runs the backend, and
  serves the frontend through Nginx.
