# Spring Boot Todo Application

This is a Spring Boot application with full CRUD operations for managing todos. It uses an in-memory data store for simplicity and includes proper error handling, validation, and API response wrappers. This project also includes features like JWT-based authentication, user management, and CSV file processing.

## Features

- Full CRUD operations for todos
- RESTful API endpoints with consistent response format
- Proper validation with 422 status codes for validation errors
- Error handling with appropriate HTTP status codes (400, 404, 422, 500)
- API response wrapper: `{status: true/false, message: "message", data: {}/[]}`
- Clean code architecture with separation of concerns
- Unit and integration tests
- **Authentication & Authorization**: JWT-based security for accessing protected endpoints.
- **User Management**: Endpoints for user registration and login.
- **CSV Processing**: Multiple endpoints for uploading and processing CSV files, including single record, batch, and Spring Batch-based methods.

## Architecture

The application follows a clean architecture pattern with the following layers:

- **Controller Layer**: Handles HTTP requests, validation, and API responses.
- **Service Layer**: Contains business logic.
- **Repository Layer**: Handles data persistence.
- **Entity/Model Layer**: Represents the data structure.
- **DTO Layer**: Data transfer objects for API requests/responses.
- **Security**: Handles authentication and authorization using JWT.
- **Common Layer**: Shared utilities like API response wrapper.

## Project Structure

```
src/main/kotlin/com/world/spring/
├── core/
│   └── security/          # Security configuration, JWT provider, and user details service
│       ├── config/
│       ├── jwt/
│       └── service/
├── features/              # Feature modules
│   ├── auth/              # User authentication and registration
│   │   ├── controller/
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   ├── csv/               # CSV upload and processing (single and batch)
│   │   ├── controller/
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   ├── new_csv/           # Spring Batch-based CSV processing
│   │   ├── config/
│   │   ├── controller/
│   │   ├── entity/
│   │   └── repository/
│   └── todo/              # Todo module
│       ├── controller/
│       ├── dto/
│       ├── entity/
│       ├── repository/
│       └── service/
├── shared/                # Shared utilities and configurations
│   ├── annotations/
│   └── response/
├── GlobalExceptionHandler.kt # Global exception handling
└── Application.kt       # Main application class
```

## API Response Format

All API endpoints return responses in the following format:
```json
{
  "status": true/false,
  "message": "Success or error message",
  "data": {} or [] or null
}
```

## HTTP Status Codes

- `200 OK` - Successful GET, PUT, PATCH and DELETE requests
- `201 Created` - Successful POST request
- `400 Bad Request` - Invalid request parameters (negative IDs, etc.)
- `401 Unauthorized` - Authentication failed
- `404 Not Found` - Resource not found
- `422 Unprocessable Entity` - Validation errors
- `500 Internal Server Error` - Server errors

## Validation Rules

- **Title**: Required, cannot be blank, max 255 characters
- **Description**: Optional, max 1000 characters
- **ID**: Must be a positive number (> 0)

## API Endpoints

See `API_DOCUMENTATION.md` for detailed API documentation.

## Running the Application

1. Make sure you have Java 21+ and Gradle installed
2. Run the application:
   ```bash
   ./gradlew bootRun
   ```
3. The application will start on `http://localhost:8081`

## Testing

Run the unit and integration tests:
```bash
./gradlew test
```

## Dependencies

- Spring Boot Web Starter
- Spring Security
- Spring Batch
- JJWT for JWT support
- Kotlin
- Jackson for JSON processing
- Mockito-Kotlin for testing

## Sample Data

The application includes some sample todos on startup that will be accessible through the API.