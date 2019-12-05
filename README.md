# remit-now
Sample project consisting of a simple money transfer service and an embedded HTTP server, 
presenting a custom (basic) banking domain design, using Java concurrency utilities.

The inspiration for some concerns (like persistence/storage) was drawn from experience with Spring.

## Technology Stack
* Java 8
* Gradle
* Lombok
* OpenAPI 3.0 for the API specification
* Vert.x Web Api Service for API specification parsing and automatic router generation
* Spock & Groovy for testing

## Running
1. Build the jar: `./gradlew jar`
2. Run the application: `java -jar ./build/libs/remit-now-1.0-SNAPSHOT.jar`

NOTE: The application start a local HTTP server on port 8080.

## Future work
* Improve/extract validation (both design and scope - currency validation not implemented)
* Logging
* Separation of concerns (maybe use AOP)
* Authentication/user management
