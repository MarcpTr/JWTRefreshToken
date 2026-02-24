
# JWT Refresh Authentication API

### Spring Boot 3 • Spring Security 6 • JWT • MariaDB

A production-style secure RESTful API implementing **JWT Authentication (Access + Refresh Tokens)** with **Spring Boot 3** and **Spring Security 6**.

The project demonstrates:

* Stateless authentication
* Token revocation & persistence
* Role-Based Access Control (RBAC)
* User-scoped resources (TODO Notes)
* Clean layered architecture
# Tech Stack

* **Java 17+**
* **Spring Boot 3.x**
* **Spring Security 6**
* **JWT**
* **MariaDB**
* **Spring Data JPA**
* **Maven**

---

# Features

## Authentication & Authorization

* User registration & login
* JWT-based authentication:

  * Short-lived **Access Tokens**
  * Long-lived **Refresh Tokens**
* Refresh token endpoint
* Token revocation stored in database
* Role-Based Access Control (RBAC)
* Stateless security configuration (no HTTP sessions)

---
## User authentication
Base path:
```
/api/auth
```
| Endpoint | HTTP Method | Access Level  | Description                                  |
| -------- | ----------- | ------------- | -------------------------------------------- |
| `/register`      | POST        | Public | Register a new user account |
| `/login`      | POST        |  Public|Authenticate a user and return an access token.      |
| `/refresh`      | POST        | Public| Generate new access and refresh tokens using a refresh token      |

---

## Notes Module (User-Scoped Resource)

Authenticated users can:

* Create personal notes
* Retrieve only their own notes
* Access protected endpoints using a valid Access Token

Base path:
```
/api/notes
```
| Endpoint | HTTP Method | Access Level  | Description                                  |
| -------- | ----------- | ------------- | -------------------------------------------- |
| `/`      | POST        | Authenticated | Create a new note for the authenticated user |
| `/`      | GET         | Authenticated | Get all notes of the authenticated user      |

---

## RBAC Demonstration Endpoints

Base path:

```
/api/test
```

| Endpoint  | Access Level  | Description                          |
| --------- | ------------- | ------------------------------------ |
| `/public` | Public        | No authentication required           |
| `/user`   | USER / ADMIN  | Requires `ROLE_USER` or `ROLE_ADMIN` |
| `/admin`  | ADMIN only    | Requires `ROLE_ADMIN`                |
| `/whoami` | Authenticated | Returns username and role            |

These endpoints demonstrate fine-grained authorization using Spring Security method-level security.

---
# Architecture

Layered architecture following clean separation of concerns:

```
src/main/java/com/example/jwt_demo
│
├── config        → DataInitializer, Security configuration & JWT filter
├── controller    → AuthController, NoteController, TestController
├── dto           → Authentication & request/response models
├── model         → User, Token, Note, TokenType
├── exception     → BusinessValidationException, BusinessValidationException 
├── repository    → TokenRepository, UserRepository, NoteRepository
├── service       → AuthService, JwtService,TokenService , TokenService, NoteService
└── JwtRefreshAuthApplication.java
```

---

# Configuration

Edit `application.properties`:

```properties
spring.application.name=jwtRefreshToken-demo
server.port=8080
# DB config
spring.datasource.url=jdbc:mariadb://localhost:3306/jwt_refresh_auth
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect
# JWT config
jwt.secret=084894c7d31418cee00e6958b1f63abf6ef192fb83d88ee3759ca7af4abfdf49
jwt.expiration=864000000
jwt.refresh-expiration=894000000
# Admin
admin.username=Admin
admin.password=u8SDDu322asy8DA
admin.email=admin@admin.es
```

---

# Database Structure


 1. **Users**

| # | Name      | type                                   | Description|
|---|------------|----------------------------------------|------------|
| 1 | id         | bigint(20)                             |    Unique identifier of the User|
| 2 | created_at | datetime(6)                            |    Date and time when the user was created        |
| 3 | email      | varchar(255)                           |  User’s email address, unique for each account          |
| 4 | password   | varchar(255)                           |  User’s encrypted password          |
| 5 | username   | varchar(255)                           |  Username displayed on the platform        |
| 6 | role       | enum('ROLE_ADMIN', 'ROLE_USER')        |    User role that determines access permissions        |
---
 2. **Tokens**

| # | Name      | type                                   | Description|
|---|------------|----------------------------------------|------------|
| 1 | id         | bigint(20)                             |    Unique identifier of the token|
| 2 | expired| bit(1))                            |    Indicates if the token has expired; set to `1` when the token’s lifetime is over time expired        |
| 3 | revoked| bit(1)                           |  Indicates if the token has been revoked, e.g., after user logout          |
| 4 | token| varchar(512)                           |  The actual token string used for authentication          |
| 5 | token_type| enum('ACCESS', 'REFRESH')                           |  Specifies the purpose of the token: `ACCESS` for API access, `REFRESH` for refreshing access tokens        |
| 6 | user_id| bigint(20)        |    Foreign key referencing the associated user (`users.id`)        |
---
 3. **Notes**

| # | Name      | type                                   | Description|
|---|------------|----------------------------------------|------------|
| 1 | id         | bigint(20)                             |    Unique identifier of the token|
| 2 | title| text                           |    Title of the note       |
| 3 | content|   varchar(255)                          |  Content or body of the note         |
| 4 | created_at| datetime(6)                           |  Date and time when the note was created        |
| 5 | user_id| bigint(20)        |    Foreign key referencing the associated user (`users.id`)        |

---
# Security Design

* Only **Access Tokens** can access protected endpoints (e.g. `/api/notes`)
* Only **Refresh Tokens** are accepted at `/api/auth/refresh`
* Tokens are persisted in the database
* Tokens can be:

  * Revoked
  * Marked as expired
* Access tokens are rotated on refresh
* Stateless authentication (no session storage)

---

