# AquaShine

Car Wash Booking System — Spring Boot + PostgreSQL + Selenium.

## Prerequisites
- Java 21 (Temurin recommended)
- PostgreSQL 16+
- Google Chrome (for Selenium tests)

## First-time setup
1. Install PostgreSQL. Set the `postgres` user password (default assumed: `postgres`).
2. Create an empty database:
   CREATE DATABASE aquashine_java;
3. Clone the repo.
4. If your Postgres password isn't `postgres`, edit
   `src/main/resources/application.properties`:
   spring.datasource.password=YOUR_PASSWORD
5. Run the app:
   mvn spring-boot:run

Flyway automatically runs migrations V1–V10 to build the schema.
`DemoUserSeeder` then inserts two demo accounts.

## Demo credentials
- Admin:    abc@gmail.com  / 12345678
- Customer: abcd@gmail.com / 123456789

## Running tests
1. Start the app: `mvn spring-boot:run`
2. In a second terminal: `mvn test`
   Or run individual tests from IntelliJ.

## Project structure
- `src/main/java/com/aquashine/` — Spring Boot backend
- `src/main/resources/db/migration/` — Flyway migrations
- `src/main/resources/static/` — static HTML/CSS/JS frontend
- `src/test/java/com/aquashine/selenium/` — Selenium integration tests
- `src/test/java/com/aquashine/unit/` — JUnit unit tests
