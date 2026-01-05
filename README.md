# Metropolitan Backend - Spring Boot

Spring Boot backend for the Metropolitan website.

## Features

- RESTful API with pagination support
- PostgreSQL database with JPA/Hibernate
- Automatic database seeding
- Input validation
- CORS enabled

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL database

## Configuration

Set the following environment variables or update `application.properties`:

```
DATABASE_URL=jdbc:postgresql://localhost:5432/metropolitan
DB_USERNAME=postgres
DB_PASSWORD=postgres
```

## Build and Run

### Using Maven

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### Using Java

```bash
# Build
mvn clean package

# Run
java -jar target/backend-1.0.0.jar
```

The server will start on port 5000.

## API Endpoints

### Case Studies
- `GET /api/case-studies` - List case studies (paginated)
  - Query params: `division`, `page` (default: 1), `limit` (default: 10)
- `GET /api/case-studies/{id}` - Get single case study

### News
- `GET /api/news` - List news items (paginated)
  - Query params: `page` (default: 1), `limit` (default: 10)
- `GET /api/news/{id}` - Get single news item

### Inquiries
- `POST /api/inquiries` - Create inquiry
  - Body: `{ name, email, phone, subject, message, division }`

### Careers
- `POST /api/careers/apply` - Submit job application
  - Body: `{ name, email, position, portfolioUrl, coverLetter }`

### Testimonials
- `GET /api/testimonials` - Get testimonials (paginated)
  - Query params: `division`, `page`, `limit`

## Database

The application uses PostgreSQL with JPA/Hibernate. Tables are automatically created on startup.

Database is automatically seeded with sample data on first run.

## Technology Stack

- Spring Boot 3.2.1
- Spring Data JPA
- PostgreSQL
- Lombok
- Maven
