# POC Contactability API Issues

This project is a Proof of Concept (POC) for the Contactability API, which is designed to
create a duplicated client for the actual API in order to test the functionality and record
issues that arise during its use.

The POC is built using Java in order to replicate the usage of the current clients.

## TL;DR

```sh
./gradlew bootRun
# or spring_profiles_active=intg ./gradlew bootRun
```

## Functionality

When executed, this project launch a Spring Boot application that consumes the Contactability API
a random number of times, delayed by 30 seconds max, divided by the number of threads.

The amount of calls to the API is controlled by the environment variable `CONTACTABILITY_TEST_MAX_API_CALLS`,
being 100 by default.
