# ScooterGo Scooter Rental

ScooterGo is a scooter rental prototype with a Spring Boot backend, PostgreSQL
database, and uni-app/Vue frontend. It includes user signup and login, scooter
discovery, ride start/end flows, profile pages, issue reporting, payment-method
records, and an admin dashboard for fleet operations.

## Tech Stack

- Backend: Java 17, Spring Boot 3, Spring Security, MyBatis Plus
- Frontend: uni-app, Vue 3, Vite
- Database: PostgreSQL
- API docs: Knife4j / OpenAPI
- Local deployment: Docker Compose

## Repository Layout

```text
backend/          Spring Boot API
frontend/         uni-app frontend
database/         PostgreSQL production schema migration and development seed data
docker-compose.yml
.env.example
```

## Configuration

Runtime secrets are read from environment variables. Do not commit real database
credentials.

```bash
cp .env.example .env
```

Important variables:

```text
DB_URL=jdbc:postgresql://localhost:5432/scooter_rental
DB_USERNAME=scooter_user
DB_PASSWORD=scooter_password
CORS_ALLOWED_ORIGIN_PATTERNS=http://localhost:3000,http://localhost:5173
PASSWORD_RESET_URL_TEMPLATE=http://localhost:3000/#/pages/reset-password?token={token}
BOOTSTRAP_DEFAULT_MANAGER_ENABLED=false
EMAIL_NOTIFICATIONS_ENABLED=false
EMAIL_FROM=no-reply@example.com
MAIL_HOST=smtp.example.com
MAIL_PORT=587
MAIL_USERNAME=replace-me
MAIL_PASSWORD=replace-me
SMS_NOTIFICATIONS_ENABLED=false
TWILIO_ACCOUNT_SID=replace-me
TWILIO_AUTH_TOKEN=replace-me
TWILIO_FROM_NUMBER=+10000000000
```

The backend also includes
`backend/src/main/resources/application-example.properties` as a reference for
deployment platforms that inject Spring properties directly.

Password reset now uses an email link flow. The API does not return reset tokens
to the frontend anymore. When SMTP is configured, the backend sends the reset
email directly and can optionally send an SMS alert through Twilio.

## Run Locally With Docker

```bash
docker compose up --build
```

Then open:

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html

The compose stack starts PostgreSQL, applies
`database/migrations/001_production_schema.sql`, then applies
`database/seeds/dev_seed.sql` on the first database volume initialization. This
keeps the clean production schema separate from the coursework demo accounts and
sample scooter data.

To reset the seeded database:

```bash
docker compose down -v
docker compose up --build
```

## Run Manually

Start PostgreSQL first, then export database variables:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/scooter_rental
export DB_USERNAME=scooter_user
export DB_PASSWORD=scooter_password
```

Backend:

```bash
cd backend
mvn spring-boot:run
```

Frontend:

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

## Tests And Checks

Backend:

```bash
cd backend
mvn test
```

Frontend build:

```bash
cd frontend
npm run build:h5
```

## Release Notes

This repository is ready for local development and containerized verification
after configuring environment variables. Current release-ready improvements in
this branch include:

- Frontend H5 production build passes.
- Docker Compose brings up PostgreSQL, backend, and Nginx-served frontend.
- Admin APIs are protected behind backend-enforced `ADMIN`/`MANAGER` roles.
- Password reset uses a mail-first flow, with optional Twilio SMS notification.
- Basic backend integration coverage exists for login, reset flow, and admin access control.

Remaining hardening work before a public production release:

- Replace the in-memory session token store with durable auth such as JWT or server-side session persistence.
- Replace the simulated payment gateway with a real provider integration.
- Adopt Flyway or Liquibase if you want versioned database upgrades beyond the single production schema file.
- Expand backend, frontend, and end-to-end test coverage.
