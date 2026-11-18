# Secure REST application

A simple Spring Boot REST API protected by HTTP Basic authentication. It uses
Spring Security's in-memory user and requires Java 17 or later and Maven.

## Run

Set a username and password without storing credentials in the repository:

```sh
export SPRING_SECURITY_USER_NAME=myuser
read -rs SPRING_SECURITY_USER_PASSWORD
export SPRING_SECURITY_USER_PASSWORD
mvn spring-boot:run
```

Enter the password when prompted by `read`. If no credentials are configured,
Spring Boot uses the username `user` and generates a password printed at
startup; this is intended for development only.

## Call the API

```sh
curl -u "$SPRING_SECURITY_USER_NAME" http://localhost:8080/api/hello
```

Enter the password when curl prompts for it. A successful response is
`{"message":"Hello, world!"}`; missing or incorrect credentials receive HTTP
401.

HTTP Basic sends credentials with every request. Use HTTPS for any deployment
outside local development, and provide strong credentials through a secure
configuration mechanism. CSRF protection remains enabled.

## Test and build

```sh
mvn verify
```
