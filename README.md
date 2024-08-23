# Roman Numeral Converter

## Overview

This Spring Boot application provides a REST API for converting integers to Roman numerals. It supports both single number conversion and range conversion.

## References
Roman numerals definition and description was taken from the wiki: https://en.wikipedia.org/wiki/Roman_numerals

## Build and Run Instructions

### Prerequisites

- Java 21 or later
- Maven 3.9 or later

### Building the Project

1. Clone the repository:
   ```
   git clone https://github.com/zimbru/roman-numeral-converter.git
   cd roman-numeral-converter
   ```

2. Build the project:
   ```
   mvn clean install
   ```

### Running the Application

Execute the following command:
```
java -jar target/aem-0.0.1-SNAPSHOT.jar
```
Alternatively
```
mvn clean spring-boot:run
```
The application will start on `http://localhost:8080`.

### API Usage

- Single number conversion:
  `GET http://localhost:8080/romannumeral?query={number}`

- Range conversion:
  `GET http://localhost:8080/romannumeral?min={min}&max={max}`

## API Documentation

This project includes an OpenAPI definition for the REST API. The OpenAPI YAML file is located at:

```
src/main/resources/openapi/openapi.yaml
```

After starting the application, you can access:

- OpenAPI JSON: `http://localhost:8080/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

The Swagger UI provides an interactive interface to explore and test the API endpoints.

## Engineering and Testing Methodology

My development process adheres to industry best practices:

### Engineering Principles

- Test-Driven Development (TDD): I write tests before implementing features.
- SOLID Principles: My codebase is designed with a focus on maintainability and scalability.
- Agile Methodology: I employ iterative development with regular reviews and adaptations.

### Testing Strategy

My comprehensive testing approach includes:

1. Unit Tests: For individual components, primarily services.
2. Integration Tests: To ensure correct interaction between components.
3. API Tests: Validating the behavior of my REST endpoints.

Execute the test suite using:
```
mvn test
```

## Project Structure

The project follows a standard Spring Boot layout:

```
src
├── main
│   ├── java
│   │   └── com
│   │       └── alex
│   │           └── aem
│   │               ├── controller
│   │               ├── service
│   │               ├── data
│   │               └── exception
│   └── resources
│       └── openapi
│           └── api-docs.yaml
└── test
    └── java
        └── com
            └── alex
                └── aem
                    ├── controller
                    ├── service
                    └── integration
```

- `controller`: Contains REST API endpoints.
- `service`: Houses business logic.
- `data`: Defines data transfer objects.
- `exception`: Custom exception classes.
- `test`: Mirrors the main structure for unit and integration tests.

## Dependencies

Key dependencies include:

- Spring Boot (v3.3.2): Application framework
- JUnit 5: Testing framework
- Mockito: Mocking framework for unit tests
- SLF4J: Logging facade

For a complete list of dependencies and their versions, please refer to the `pom.xml` file.

## Additional Information

For further details on the API usage, configuration options, or contribution guidelines, please refer to the project documentation or contact the development team.