 Java Spring Boot Backend

This is the backend of the project built using Java Spring Boot.  
It handles user registration, login authentication, password encryption, and database operations.

 Features
• Login authentication using encrypted passwords  
• PostgreSQL database connection  
• CRUD operations for the dashboard  
• JWT-based authentication (optional upgrade)  

 Tech Stack
• Java Spring Boot  
• PostgreSQL  
• Maven  
• Spring Security + BCrypt encryption  

 How to Run the Backend
1. Install dependencies
   mvn clean install

2. Start the application
   mvn spring-boot:run

3. Backend runs at
   http://localhost:8080

 Database Configuration
Update `application.properties`:
spring.datasource.url=jdbc:postgresql://localhost:5432/mydb
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update

 Main Endpoints
POST /api/auth/login  
POST /api/auth/register  
GET  /api/dashboard/data  

 Requirements
• Java 17 or above  
• PostgreSQL installed  
