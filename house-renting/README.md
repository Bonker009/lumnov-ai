# Renthouse Management Backend

A comprehensive REST API for renthouse management platform built with Spring Boot, featuring JWT authentication, role-based access control, and PostgreSQL database integration.

## Features

### Authentication & Security
- JWT-based stateless authentication
- Role-based access control (OWNER, USER)
- Password encryption with BCrypt
- Method-level security annotations
- Global exception handling

### User Features (ROLE_USER)
- Search renthouses by location, name, or price range
- Find nearby renthouses using geolocation
- View renthouse and room details
- Book available rooms
- Manage favorite rooms
- View payment records and QR codes

### Owner Features (ROLE_OWNER)
- Full CRUD operations for renthouses, floors, and rooms
- View rooms with renter information
- Search rooms by number or renter
- Create and manage monthly payment records
- Generate QR codes for payments
- Income reports (monthly/yearly)

## Tech Stack

- **Framework**: Spring Boot 3.2.0
- **Security**: Spring Security with JWT
- **Database**: PostgreSQL with Spring Data JPA
- **Validation**: Hibernate Validator (JSR 380)
- **Documentation**: Swagger/OpenAPI 3
- **Build Tool**: Maven

## Database Configuration

The application connects to a PostgreSQL database with the following configuration:
- Host: `35.247.147.122`
- Database: `lumnov-db`
- Username: `postgres`
- Password: `123`

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL database access

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

### API Documentation

Once the application is running, you can access the Swagger UI at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## API Endpoints

### Authentication Endpoints
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### User Endpoints (Requires ROLE_USER)
- `GET /api/user/renthouses/nearby` - Get nearby renthouses
- `GET /api/user/renthouses/search` - Search renthouses
- `GET /api/user/renthouses/{id}` - Get renthouse details
- `GET /api/user/renthouses/{id}/rooms/available` - Get available rooms
- `POST /api/user/rooms/{id}/book` - Book a room
- `POST /api/user/favorites/{roomId}` - Add to favorites
- `DELETE /api/user/favorites/{roomId}` - Remove from favorites
- `GET /api/user/favorites` - Get favorite rooms
- `GET /api/user/payments` - Get payment records
- `GET /api/user/payments/{id}/qr-code` - Get payment QR code

### Owner Endpoints (Requires ROLE_OWNER)
- `GET /api/owner/renthouses` - Get owned renthouses
- `POST /api/owner/renthouses` - Create renthouse
- `PUT /api/owner/renthouses/{id}` - Update renthouse
- `DELETE /api/owner/renthouses/{id}` - Delete renthouse
- `POST /api/owner/renthouses/{id}/floors` - Create floor
- `POST /api/owner/floors/{id}/rooms` - Create room
- `GET /api/owner/rooms` - Get owned rooms
- `GET /api/owner/rooms/search` - Search rooms
- `POST /api/owner/payments` - Create payment record
- `GET /api/owner/payments` - Get payment records
- `GET /api/owner/income/monthly` - Get monthly income
- `GET /api/owner/income/yearly` - Get yearly income

## Authentication

All protected endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

To get a token:
1. Register a new user or login with existing credentials
2. Use the returned token in subsequent requests

## Data Models

### User
- Authentication and profile information
- Role-based access (OWNER/USER)

### Renthouse
- Property information with geolocation
- Owned by users with OWNER role

### Floor
- Belongs to a renthouse
- Contains multiple rooms

### Room
- Bookable units with status tracking
- Can be rented by users with USER role

### Payment
- Monthly payment records with detailed charges
- QR code integration for payments

### Favorite
- User's favorite rooms for quick access

## Validation

The API includes comprehensive input validation:
- Email format validation
- Password strength requirements
- Required field validation
- Numeric constraint validation
- Custom business rule validation

## Error Handling

Global exception handler provides consistent error responses:
- Validation errors with field-specific messages
- Authentication and authorization errors
- Business logic errors
- Resource not found errors

## Security Features

- Stateless JWT authentication
- Role-based access control
- Password hashing with BCrypt
- CORS configuration
- Method-level security annotations

## Development

The application uses Spring Boot's auto-configuration for easy development and deployment. Database tables are automatically created/updated using Hibernate DDL.

For production deployment, consider:
- Using environment variables for sensitive configuration
- Setting up proper CORS policies
- Implementing rate limiting
- Adding logging and monitoring
- Setting up SSL/TLS encryption