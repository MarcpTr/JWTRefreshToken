
# JWT Refresh Auth API with Spring Boot, Spring Security 6, and MySQL

This is a secure RESTful API built with **Spring Boot**, **Spring Security 6**, **JWT (access + refresh tokens)**, and **MySQL**, with a simple `TODO Notes` feature for authenticated users.

---

##  Features

- User registration and login
- JWT authentication with:
  - Short-lived **Access Tokens**
  - Long-lived **Refresh Tokens**
- Token revocation and persistence
- Protected endpoints using Spring Security 6
- Users can:
  - Create personal notes
  - List only their own notes
- Compatible with Spring Boot 3.x+

---

##  Technologies

- Java 17+
- Spring Boot
- Spring Security 6
- JWT
- MySQL
- Maven

---

##  Project Structure

src  
├── main  
│ ├── java  
│ │ └── com.example.jwt_demo

│ │ ├── config → SecurityConf, JWT filter

│ │ ├── controller → Auth, Note 

│ │ ├── DTO → Auth, Login, Note, Refresh, Register    

│ │ ├── model → User, Token, Note, TokenType  

│ │ ├── repository →User, Token, Note  

│ │ ├── service → Auth, Jwt, Note, Token, User  

│ │ └── JwtRefreshAuthApplication.java  

│ └── resources  

│ ├── application.properties

### 2. Configure the applications.properties

Edit your `application.properties`:

    spring.application.name=jwtRefreshToken-demo  
	server.port=8080  
    spring.datasource.url=jdbc:mariadb://localhost:3306/jwt_refresh_auth  
    spring.datasource.username=root  
    spring.datasource.password=  
    spring.datasource.driver-class-name=org.mariadb.jdbc.Driver  
      
   
    spring.jpa.hibernate.ddl-auto=update  
    spring.jpa.show-sql=true  
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect  
      
    
    jwt.secret=  
    jwt.expiration=900000  
    jwt.refresh-expiration=604800000

### 3. Create the database and tables

You can use the following SQL:

    CREATE DATABASE jwt_refresh_auth;
    
    USE jwt_refresh_auth;
    
    CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL);

    CREATE TABLE tokens (
        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        token TEXT NOT NULL,
        token_type VARCHAR(20) NOT NULL,
        expired BOOLEAN NOT NULL,
        revoked BOOLEAN NOT NULL,
        user_id BIGINT NOT NULL,
        CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );
    
    CREATE TABLE notes (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    created_at DATETIME NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_note_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE);


##  Security Notes

-   Only `ACCESS` tokens are allowed to access protected endpoints like `/api/notes`
    
-   Only `REFRESH` tokens are accepted in `/api/auth/refresh`
    
-   Tokens are stored in the database with status (revoked / expired)
    
-   On refresh, only access tokens are rotated (you may change this to rotate refresh tokens too)
