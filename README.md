# Spring Boot AWS Lambda Demo

A simple AWS Lambda function built with Spring Boot and Spring Cloud Function, with support for both JVM (Java 25) and native (`provided.al2023`) runtimes via GraalVM.

The function accepts a name string, runs a compute-heavy benchmark (1 billion vector dot products), and returns a greeting with the elapsed time and a checksum:
```
"World" → "Hello, World! Computation finished in 1234ms (checksum: -8446744040951443456)"
```

The elapsed time in the response makes it easy to compare JVM vs native performance — native typically completes the computation significantly faster.

## Prerequisites

- Java 25 JDK (GraalVM recommended — required for the native build, works for JVM too)
- [AWS SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html)
- Docker (required by SAM for local invocation)

## Running locally with SAM

### JVM runtime (Java 25)

**1. Build the Lambda JAR:**
```bash
./gradlew shadowJar
```

**2. Invoke the function:**
```bash
sam local invoke HelloFunction -t template.yaml -e events/hello.json
```

### Native runtime (provided.al2023)

Requires Docker — the build runs `nativeCompile` inside a `ghcr.io/graalvm/native-image-community:25` Linux arm64 container to produce an ELF binary compatible with Lambda.

**1. Build via SAM (calls `make build-HelloFunction` internally):**
```bash
sam build -t template-native.yaml
```

**2. Invoke the function:**
```bash
sam local invoke HelloFunction -t .aws-sam/build/template.yaml -e events/hello.json
```

You can also compile the native binary directly without SAM:
```bash
./gradlew nativeCompile
# binary is output to build/native/nativeCompile/bootstrap
```

## Project structure

```
.
├── template.yaml               # SAM template — JVM runtime
├── template-native.yaml        # SAM template — native runtime
├── Makefile                    # Used by SAM to build the native binary
├── events/
│   └── hello.json              # Sample invocation payload
└── src/main/java/org/local/springawslambda/
    ├── SpringAwsLambdaApplication.java
    └── HelloFunction.java      # Function<String, String> bean
```

## Adding a new function

1. Add a new `@Bean` returning a `java.util.function.Function` in any `@Configuration` class.
2. Update `spring.cloud.function.definition` in `application.properties` (comma-separate multiple names).
3. Update the `SPRING_CLOUD_FUNCTION_DEFINITION` environment variable in the SAM template(s) to match.
