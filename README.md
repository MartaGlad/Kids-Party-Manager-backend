# Kids Party Manager – Backend

## Project Overview

This project is a Spring Boot application designed to manage event reservations for children (birthday parties).

It provides a REST API for handling core business operations such as managing:
- animators,
- orderers,
- event packages,
- reservations,
- pricing,
- event ratings.

A Vaadin-based frontend client demonstrating basic functionality of this project is available here:

[Kids Party Manager](https://github.com/MartaGlad/Kids-Party-Manager-frontend)

---

## Features

The system allows users to:
- create and manage reservations,
- assign event packages, animators and orderers,
- validate availability and prevent overlapping reservations,
- automatically calculate pricing based on business rules:
  - selected package,
  - number of children,
  - date (included holidays),
  - currency conversion,
- automatically update reservation statuses:
  - `NEW → CANCELLED` (after 48h),
  - `CONFIRMED → COMPLETED` (after event date),
- store and process customer feedback (ratings and comments),
- provide aggregated statistics (e.g., average animator ratings).

---

## External Integrations

The application integrates with external APIs:

- **NBP API** → currency exchange rates (EUR, USD, GBP),
- **Nager.Date API** → public holidays.

Additional details:
- Custom SSL truststore is used for secure API communication,
- External data is cached in the database,
- Synchronization is handled via scheduled jobs.

---

## Architecture

The application is organized with clear separation of responsibilities.

It includes:
- **Controllers** → handle HTTP requests and expose REST endpoints,
- **Services** → contain core business logic and coordinate application workflows,
- **Integration components (adapters)** → handle communication with external APIs,
- **Schedulers** → execute background jobs (status updates, data synchronization),
- **Repositories** → provide data access using Spring Data JPA,
- **DTOs and mappers** → separate API layer from internal domain model,
- **Domain layer** → contains core entities and business rules,
- **Exception handling** → centralized error handling using `@ControllerAdvice`,
- **Configuration classes** → define application and integration setup.

The project uses design patterns such as:
- **Adapter Pattern** → to isolate external API integrations,
- **Strategy Pattern** → to handle flexible pricing logic.

---

## Logging

Structured logging is implemented across the application:

- **Schedulers**
  - start / finish / duration / errors 

- **Services**
  - business operation results (e.g., number of updated records) 

- **Adapters**
  - external API requests and failures 

This ensures full observability of background processes.

---

## Technologies

- Java 21 
- Spring Boot 
- Spring Web (REST API) 
- Spring Data JPA 
- Hibernate 
- Bean Validation (Jakarta Validation) 
- MySQL (production database) 
- H2 (test database) 
- Spring Boot Test
- Gradle (build tool) 
- Lombok 

---

## Running the Application

### 1. Clone the repository

Open a terminal and run:
```bash
git clone https://github.com/MartaGlad/Kids-Party-Manager-backend.git
cd Kids-Party-Manager-backend
```

### 2. Make sure MySQL is running

Ensure that your MySQL server is installed and running on your machine.

### 3. Create a MySQL database

In terminal, run:
```bash
mysql -u root -p
```
Enter your password, then execute:
```sql
CREATE DATABASE kidspartymanagerdb;
```

### 4. Configure environment variables

Set the following environment variables in your IDE run configuration:

  - MYSQL_CONNECTION_URI 
  - DB_USERNAME 
  - DB_PASSWORD 

Example:
```bash
  MYSQL_CONNECTION_URI=jdbc:mysql://localhost:3306/kidspartymanagerdb?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=Europe/Warsaw
  DB_USERNAME=root
  DB_PASSWORD=your_password
```

### 5. Run the application

Run the main Spring Boot application class `KidsPartyManagerApplication` from your IDE (recommended).


### 6. Application URL

```http://localhost:8080```

### 7. Database initialization

- Database schema is created automatically (ddl-auto=create)

- Initial test data is loaded from data.sql 

---

## Testing

The project includes unit and integration tests covering core business logic and REST endpoints.


