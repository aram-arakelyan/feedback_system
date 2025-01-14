# Customer Feedback

## Overview

A Spring Boot application for managing feedback submissions.
Features include user registration, JWT-based authentication, and CRUD operations for feedback.

## Authentication
- The application uses JWT for securing endpoints.
- The JWT secret key is read from an environment variable JWT_SECRET_KEY or falls back to default_secret_key in application.yml.
- JWT_SECRET_KEY is a secure 64-byte Base64-encoded string (using HS512).
- To generate
   ```bash
  openssl rand -base64 64
  ```

## Technologies

- Java 17
- Spring Boot 3.x
- Docker and Docker Compose
- Postgres
- Maven

## Installation

1. Install Java 17 and Maven.
2. Install Docker and Docker Compose.

## Running the Project

1. Build the project:
    ```bash
    mvn clean install
    ```
2. Start the services:
    ```bash
    docker-compose up --build
    ```
## Test Endpoints
### User Signup
Creates a new user with an email and password:
```
curl -X POST \
  http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
  "email": "paul.smith@example.com",
  "password": "securepassword"
  }'
```
Expected Response:
```
{
  "id": 1,
  "email": "john.doe@example.com"
}

```
### User Login
Authenticates a registered user and returns a JWT token:
```
curl -X POST \
  http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "paul.smith@example.com",
    "password": "securepassword"
  }'
```
Expected Response:
```
{
  "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."
}

```
Once you have the token, you can use it in the Authorization header for other protected endpoints, like so:
```
curl -X GET \
  http://localhost:8080/api/v1/feedback?establishmentId=123 \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."

```
### Create feedback
Creates feedback for a specific establishment:
```
curl -X POST \
  http://localhost:8080/api/v1/feedback \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "establishmentId": 2,
    "title": "Great Service",
    "textComment": "The staff was very helpful and friendly.",
    "score": 5
  }'

```
Expected response:
```
{
  "id": 1,
  "title": "Great Service",
  "textComment": "The staff was very helpful and friendly.",
  "score": 5,
  "establishmentId": 2
}
```
### Retrieve Feedback by Establishment ID
Retrieves a list of feedbacks for a given establishment:
```
curl -X GET \
  "http://localhost:8080/api/v1/feedback?establishmentId=123" \
  -H "Authorization: Bearer <your-jwt-token>"
```
Expected response:
```
[
  {
    "id": 1,
    "title": "Great Service",
    "textComment": "The staff was very helpful and friendly.",
    "score": 5,
    "establishmentId": 2
  },
  {
    "id": 2,
    "title": "Average Experience",
    "textComment": "The service was okay but can be improved.",
    "score": 3,
    "establishmentId": 2
  }
]
```
### Delete Feedback

Deletes a specific feedback by its ID for the authenticated user:
```
curl -X DELETE \
  http://localhost:8080/api/v1/feedback/1 \
  -H "Authorization: Bearer <your-jwt-token>"

```