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
credentials; `.env` is ignored by git.

```bash
cp .env.example .env
```

Important variables:

```text
DB_URL=jdbc:postgresql://localhost:5432/scooter_rental
DB_USERNAME=scooter_user
DB_PASSWORD=<long-random-password>
CORS_ALLOWED_ORIGIN_PATTERNS=http://scootergo.top:3000,http://www.scootergo.top:3000,http://scootergo.top,https://scootergo.top,http://www.scootergo.top,https://www.scootergo.top,http://localhost:3000,http://localhost:5173
PASSWORD_RESET_URL_TEMPLATE=https://scootergo.top:3000/#/pages/reset-password?token={token}
PASSWORD_RESET_MANUAL_LINK_FALLBACK_ENABLED=true
BOOTSTRAP_DEFAULT_MANAGER_ENABLED=false
EMAIL_NOTIFICATIONS_ENABLED=false
EMAIL_FROM=no-reply@example.com
MAIL_HOST=smtp.example.com
MAIL_PORT=587
MAIL_USERNAME=
MAIL_PASSWORD=
MAIL_SMTP_CONNECTION_TIMEOUT=5000
MAIL_SMTP_TIMEOUT=5000
MAIL_SMTP_WRITE_TIMEOUT=5000
SMS_NOTIFICATIONS_ENABLED=false
TWILIO_ACCOUNT_SID=
TWILIO_AUTH_TOKEN=
TWILIO_FROM_NUMBER=+10000000000
```

The backend also includes
`backend/src/main/resources/application-example.properties` as a reference for
deployment platforms that inject Spring properties directly.

Before the domain is approved for ports 80/443, point `scootergo.top` at the
host running Docker and open `http://scootergo.top:3000`. After approval, switch
`FRONTEND_PORT` back to `80` and remove `:3000` from the public URLs. For phone
testing on a local network, add the laptop's
LAN origin back into `CORS_ALLOWED_ORIGIN_PATTERNS` and open that LAN URL on the
phone. Keep
`BOOTSTRAP_DEFAULT_MANAGER_ENABLED=false` unless you intentionally want the
backend to create or overwrite the demo manager account at startup.

Password reset uses an email link flow when SMTP is configured. In demo or
coursework deployments without SMTP, keep
`PASSWORD_RESET_MANUAL_LINK_FALLBACK_ENABLED=true`; the forgot-password page will
show a one-time reset link so the flow still works end to end. Disable that
fallback in production once real email delivery is configured. The backend can
optionally send an SMS alert through Twilio. SMTP calls use short default
timeouts so an unavailable mail server fails quickly and returns the fallback
reset link instead of leaving the request hanging.

## Run Locally With Docker

```bash
docker compose up --build
```

Then open:

- Frontend: http://localhost:3000 or http://scootergo.top:3000
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
export DB_PASSWORD=<long-random-password>
export PASSWORD_RESET_URL_TEMPLATE=https://scootergo.top:3000/#/pages/reset-password?token={token}
export PASSWORD_RESET_MANUAL_LINK_FALLBACK_ENABLED=true
export BOOTSTRAP_DEFAULT_MANAGER_ENABLED=false
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
