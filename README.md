# Complaint Service

This project implements a Complaint Service using Spring Boot, designed to handle student complaints within a college portal system. It provides RESTful APIs for submitting, managing, and resolving complaints, including file attachments.

## Table of Contents

1.  [Technologies Used](#technologies-used)
2.  [Project Structure](#project-structure)
3.  [Setup and Running the Application](#setup-and-running-the-application)
4.  [API Endpoints](#api-endpoints)
    *   [Base URL](#base-url)
    *   [1. Create a New Complaint (Student)](#1-create-a-new-complaint-student)
    *   [2. Assign Technician (Admin)](#2-assign-technician-admin)
    *   [3. Submit for HOD Verification (Technician)](#3-submit-for-hod-verification-technician)
    *   [4. Final Sign-off and Resolution (HOD)](#4-final-sign-off-and-resolution-hod)
    *   [5. Toggle Public Visibility (Admin)](#5-toggle-public-visibility-admin)
    *   [6. Get a Specific Complaint](#6-get-a-specific-complaint)
    *   [7. Get Global Public Feed](#7-get-global-public-feed)
    *   [8. Get a Specific User's Complaints](#8-get-a-specific-users-complaints)
    *   [9. Get Master List of All Complaints (Admin)](#9-get-master-list-of-all-complaints-admin)
5.  [Project Analysis and Observations](#project-analysis-and-observations)
    *   [Security](#security)
    *   [Database Schema Management](#database-schema-management)
    *   [Logging](#logging)
    *   [Performance](#performance)
    *   [Service Discovery](#service-discovery)
    *   [Error Handling](#error-handling)

## Technologies Used

*   **Spring Boot:** Framework for building robust, production-ready applications.
*   **Maven:** Dependency management and build automation tool.
*   **MySQL:** Relational database for data storage (Aiven Cloud MySQL in `application.properties`).
*   **JPA/Hibernate:** ORM for database interaction.
*   **Lombok:** To reduce boilerplate code (e.g., getters, setters, constructors).
*   **RESTful APIs:** For communication between client and server.
*   **MultipartFile:** For handling file uploads.
*   **Jackson (ObjectMapper):** For JSON serialization/deserialization.

## Project Structure

The project follows a standard layered architecture for Spring Boot applications:

*   `com.collegeportal.complaint_service.controller`: Handles incoming HTTP requests and returns responses.
*   `com.collegeportal.complaint_service.service`: Contains business logic and orchestrates operations.
*   `com.collegeportal.complaint_service.repository`: Provides data access operations to the database.
*   `com.collegeportal.complaint_service.entity`: Defines the database entities (data models).
*   `com.collegeportal.complaint_service.dto`: Data Transfer Objects for request/response payloads.
*   `com.collegeportal.complaint_service.config`: Configuration classes for the application.
*   `ComplaintServiceApplication.java`: The main entry point for the Spring Boot application.

## Setup and Running the Application

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd complaint-service
    ```
2.  **Configure Database:**
    *   Ensure your MySQL database is running and accessible.
    *   Update the `src/main/resources/application.properties` file with your database credentials if they differ from the Aiven Cloud MySQL configuration.
3.  **Build the project:**
    ```bash
    mvn clean install
    ```
4.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    The application will start on `http://localhost:8082` by default.

## API Endpoints

This section details the RESTful API endpoints provided by the Complaint Service. You can test these endpoints using tools like Postman.

### Base URL

All endpoints are prefixed with: `http://localhost:8082/api/complaints`

---

### 1. Create a New Complaint (Student)

*   **Method:** `POST`
*   **URL:** `/api/complaints`
*   **Description:** Allows a student to submit a new complaint, optionally including file attachments (images, documents, etc.).
*   **Postman Configuration:**
    *   **Method:** `POST`
    *   **URL:** `http://localhost:8082/api/complaints`
    *   **Body:** Select `form-data`
        *   **Key:** `data` (Type: `Text`)
            *   **Value (JSON):**
                ```json
                {
                    "studentId": 1,
                    "subject": "Leaky Faucet in Dorm Room 305",
                    "description": "The faucet in my bathroom has been leaking continuously for the past two days. It's wasting water and making noise.",
                    "category": "Plumbing",
                    "location": "Dorm Room 305",
                    "contactInfo": "student1@example.com"
                }
                ```
        *   **Key:** `files` (Type: `File`, `required = false`)
            *   **Value:** Select one or more image/document files (e.g., `image.jpg`, `report.txt`). You can add multiple `files` keys for multiple files.

---

### 2. Assign Technician (Admin)

*   **Method:** `PUT`
*   **URL:** `/api/complaints/{id}/assign`
*   **Description:** An administrator assigns a complaint to a specific technician/dispatcher.
*   **Path Variable:**
    *   `id`: The ID of the complaint (e.g., `1`)
*   **Query Parameters:**
    *   `adminId`: The ID of the admin performing the assignment (e.g., `101`)
    *   `dispatcherId`: The ID of the technician/dispatcher to assign (e.g., `201`)
*   **Postman Example URL:** `http://localhost:8082/api/complaints/1/assign?adminId=101&dispatcherId=201`

---

### 3. Submit for HOD Verification (Technician)

*   **Method:** `PUT`
*   **URL:** `/api/complaints/{id}/submit-verification`
*   **Description:** A technician submits a complaint for Head of Department (HOD) verification after initial work.
*   **Path Variable:**
    *   `id`: The ID of the complaint (e.g., `1`)
*   **Query Parameter:**
    *   `adminNote`: A note from the technician/admin regarding the status (e.g., `"Faucet repaired, awaiting HOD approval."`)
*   **Postman Example URL:** `http://localhost:8082/api/complaints/1/submit-verification?adminNote=Faucet%20repaired,%20awaiting%20HOD%20approval.`

---

### 4. Final Sign-off and Resolution (HOD)

*   **Method:** `PUT`
*   **URL:** `/api/complaints/{id}/resolve`
*   **Description:** The HOD provides final sign-off and resolves the complaint.
*   **Path Variable:**
    *   `id`: The ID of the complaint (e.g., `1`)
*   **Query Parameters:**
    *   `hodId`: The ID of the HOD (e.g., `301`)
    *   `hodNote`: A note from the HOD regarding the resolution (e.g., `"Approved resolution. Complaint closed."`)
*   **Postman Example URL:** `http://localhost:8082/api/complaints/1/resolve?hodId=301&hodNote=Approved%20resolution.%20Complaint%20closed.`

---

### 5. Toggle Public Visibility (Admin)

*   **Method:** `PUT`
*   **URL:** `/api/complaints/{id}/visibility`
*   **Description:** An administrator can toggle whether a complaint is publicly visible or not.
*   **Path Variable:**
    *   `id`: The ID of the complaint (e.g., `1`)
*   **Query Parameter:**
    *   `isPublic`: `true` or `false`
*   **Postman Example URL:** `http://localhost:8082/api/complaints/1/visibility?isPublic=true`

---

### 6. Get a Specific Complaint

*   **Method:** `GET`
*   **URL:** `/api/complaints/{id}`
*   **Description:** Retrieves the detailed information for a single complaint by its ID.
*   **Path Variable:**
    *   `id`: The ID of the complaint (e.g., `1`)
*   **Postman Example URL:** `http://localhost:8082/api/complaints/1`

---

### 7. Get Global Public Feed

*   **Method:** `GET`
*   **URL:** `/api/complaints/public`
*   **Description:** Retrieves a list of all complaints that have been marked as publicly visible.
*   **Postman Example URL:** `http://localhost:8082/api/complaints/public`

---

### 8. Get a Specific User's Complaints

*   **Method:** `GET`
*   **URL:** `/api/complaints/user/{userId}`
*   **Description:** Retrieves all complaints submitted by a specific student user.
*   **Path Variable:**
    *   `userId`: The ID of the student (e.g., `1`)
*   **Postman Example URL:** `http://localhost:8082/api/complaints/user/1`

---

### 9. Get Master List of All Complaints (Admin)

*   **Method:** `GET`
*   **URL:** `/api/complaints/all`
*   **Description:** Retrieves a comprehensive list of all complaints in the system (typically for administrative use).
*   **Postman Example URL:** `http://localhost:8082/api/complaints/all`

---

## Project Analysis and Observations

Based on the code review and `application.properties`, here are some observations and potential areas for improvement:

### Security

*   **Hardcoded Database Credentials:** The `spring.datasource.username` and `spring.datasource.password` are directly present in `application.properties`.
    *   **Recommendation:** For production environments, externalize sensitive credentials using environment variables, a secrets management service (e.g., HashiCorp Vault, AWS Secrets Manager), or Spring Cloud Config. This prevents credentials from being committed to version control.

### Database Schema Management

*   **`spring.jpa.hibernate.ddl-auto=update`**: This setting allows Hibernate to automatically update the database schema based on entity changes.
    *   **Recommendation:** While convenient for development, `update` can be risky in production as it might lead to unexpected schema changes or data loss. For production, consider using `validate` or `none` and manage schema migrations explicitly with tools like Flyway or Liquibase.

### Logging

*   **`spring.jpa.show-sql=true`**: This enables logging of all SQL statements executed by Hibernate.
    *   **Recommendation:** This is useful for debugging and development. However, in production, it can generate excessive log volume and potentially expose sensitive data in logs. It should generally be set to `false` in production or configured with a more granular logging level.

### Performance

*   **`spring.datasource.hikari.maximum-pool-size=3`**: The Hikari connection pool size is set to a very low value.
    *   **Recommendation:** While noted as "Crucial for Cloud Free-Tiers," this can become a significant performance bottleneck under even moderate load. If the service is expected to handle more than a few concurrent requests, this value will likely need to be increased after proper load testing and resource monitoring.

### Service Discovery

*   **Eureka Client Disabled**: `eureka.client.register-with-eureka=false` and `eureka.client.fetch-registry=false` indicate that the service is not participating in Eureka service discovery.
    *   **Observation:** If this service is intended to be part of a microservices architecture that uses Eureka, these properties should be set to `true`. If it's a standalone service or uses a different discovery mechanism, then the current configuration is appropriate.

### Error Handling

*   **`IOException` in Controller**: The `createComplaint` method in `ComplaintController` directly throws `IOException` (e.g., from `mapper.readValue`).
    *   **Recommendation:** In a production application, it's better to catch specific exceptions within the controller or service layer and return appropriate HTTP status codes (e.g., `400 Bad Request` for invalid input, `500 Internal Server Error` for unexpected server issues) with a custom error response body, rather than letting the server return a generic 500 error page. Consider implementing a global exception handler using `@ControllerAdvice`.