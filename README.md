# Spring Boot Todo Application

This is a Spring Boot application with full CRUD operations for managing todos. It uses an in-memory data store for simplicity and includes proper error handling, validation, and API response wrappers.

## Features

- Full CRUD operations for todos
- RESTful API endpoints with consistent response format
- Proper validation with 422 status codes for validation errors
- Error handling with appropriate HTTP status codes (400, 404, 422, 500)
- API response wrapper: `{status: true/false, message: "message", data: {}/[]}`
- Clean code architecture with separation of concerns
- Unit and integration tests

## Architecture

The application follows a clean architecture pattern with the following layers:

- **Controller Layer**: Handles HTTP requests, validation, and API responses
- **Service Layer**: Contains business logic (without validation)
- **Repository Layer**: Handles data persistence
- **Entity/Model Layer**: Represents the data structure
- **DTO Layer**: Data transfer objects for API requests/responses
- **Common Layer**: Shared utilities like API response wrapper

## Project Structure

```
src/main/kotlin/com/world/spring/
├── common/              # Common utilities
│   └── ApiResponse.kt   # API response wrapper
├── todo/                # Todo module
│   ├── Todo.kt          # Entity model
│   ├── TodoRepository.kt # Repository interface
│   ├── InMemoryTodoRepository.kt # In-memory implementation
│   ├── TodoService.kt   # Business logic layer
│   ├── TodoRequest.kt   # DTOs for requests/responses
│   └── TodoController.kt # REST endpoints with validation and error handling
├── config/              # Configuration classes
│   └── WebConfig.kt     # Web configuration (CORS, etc.)
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
- Kotlin
- Jackson for JSON processing
- Mockito-Kotlin for testing

## Sample Data

The application includes some sample todos on startup that will be accessible through the API.