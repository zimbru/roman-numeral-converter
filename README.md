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

## API Usage Examples

Here are some examples of how to use the Roman Numeral Converter API using CURL commands:

### Single Number Conversion

To convert a single number to a Roman numeral, use the following CURL command:

```bash
curl "http://localhost:8080/romannumeral?query=42"
```

Expected response:

```json
{
  "input": "42",
  "output": "XLII"
}
```

### Range Conversion

To convert a range of numbers to Roman numerals, use the following CURL command:

```bash
curl "http://localhost:8080/romannumeral?min=1&max=5"
```

Expected response:

```json
{
  "conversions": [
    {
      "input": "1",
      "output": "I"
    },
    {
      "input": "2",
      "output": "II"
    },
    {
      "input": "3",
      "output": "III"
    },
    {
      "input": "4",
      "output": "IV"
    },
    {
      "input": "5",
      "output": "V"
    }
  ]
}
```

### Error Example

If you provide an invalid input, such as a number out of the supported range (1-3999), you'll receive an error response:

```bash
curl "http://localhost:8080/romannumeral?query=4000"
```

Expected response:

```json
{
  "errorCode": "OUT_OF_RANGE",
  "message": "Numbers must be between 1 and 3999"
}
```

These examples demonstrate how to interact with the API using CURL commands and what responses to expect for successful conversions and error cases. You can easily test these commands once the application is running on your local machine.

## Engineering and Testing Methodology

My development process adheres to industry best practices and leverages modern Java features:

### Engineering Principles

- Test-Driven Development (TDD): I write tests before implementing features.
- SOLID Principles: My codebase is designed with a focus on maintainability and scalability.
- Agile Methodology: I employ iterative development with regular reviews and adaptations.

### Leveraging Java 21 Virtual Threads

This project takes advantage of Java 21's Virtual Threads feature to improve performance and scalability:

1. Implementation:
    - In `RomanNumeralConverterService`, I use Virtual Threads for parallel processing of number ranges.
    - Virtual Threads are created using `Executors.newVirtualThreadPerTaskExecutor()`.
    - Each batch of numbers in the conversion range is processed in its own Virtual Thread.

2. Benefits:
    - Improved Scalability: Virtual Threads allow for a high number of concurrent operations without the overhead of traditional threads.
    - Enhanced Performance: For I/O-bound tasks or when dealing with large ranges of numbers, Virtual Threads can significantly improve throughput.
    - Efficient Resource Utilization: Virtual Threads are managed by the JVM, leading to better resource management compared to platform threads.

3. Code Example:
   ```java
   CompletableFuture.supplyAsync(
       () -> processBatch(min, max, batchIndex, batchSize),
       Executors.newVirtualThreadPerTaskExecutor()
   )
   ```

4. Adaptive Approach:
    - My implementation dynamically adjusts the batch size based on the input range, ensuring optimal use of Virtual Threads for both small and large conversion requests.

By utilizing Virtual Threads, my application can handle a large number of concurrent conversion requests efficiently, making it well-suited for high-load scenarios and demonstrating the use of modern Java concurrency features.

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
│   │               ├── validator
│   │               └── exception
│   └── resources
│       └── openapi
│           └── openapi.yaml
└── test
    └── java
        └── com
            └── alex
                └── aem
                    ├── controller
                    └── service
```

- `controller`: Contains REST API endpoints.
- `service`: Houses business logic.
- `data`: Defines data transfer objects.
- `exception`: Custom exception classes.
- `validator`: Validation logic.
- `test`: Mirrors the main structure for unit and integration tests.

## Dependencies

Key dependencies include:

- Spring Boot (v3.3.2): Application framework
- JUnit 5: Testing framework
- Mockito: Mocking framework for unit tests
- SLF4J: Logging facade
- Springdoc: generating OpenAPI documentation and Swagger interface

For a complete list of dependencies and their versions, please refer to the `pom.xml` file.

## Additional Information

For further details on the API usage, configuration options, or contribution guidelines, please refer to the project documentation or contact me.