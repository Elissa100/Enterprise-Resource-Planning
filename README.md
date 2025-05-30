# 🏢 Enterprise Resource Planning (ERP) System

<div align="center">

![ERP Banner](https://img.shields.io/badge/ERP-Payroll%20%26%20Employee%20Management-blue?style=for-the-badge&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0id2hpdGUiPjxwYXRoIGQ9Ik0xMiAyQzYuNDggMiAyIDYuNDggMiAxMnM0LjQ4IDEwIDEwIDEwIDEwLTQuNDggMTAtMTBTMTcuNTIgMiAxMiAyem0wIDE4Yy00LjQxIDAtOC0zLjU5LTgtOHMzLjU5LTggOC04IDggMy41OSA4IDgtMy41OSA4LTggOHptLjg5LTguOWMyLjA3LS4wNSAzLjEzIDEuNTMgMy4xMyAzLjEgMCAxLjk3LTEuNDEgMy4yLTMuMTMgMy4yLTEuMzkgMC0yLjYtLjg3LTMuMDUtMi4xLS4wNS0uMTUtLjA1LS4zLS4wNS0uNDVoMmMwIC4zMS4xNS42MS40LjgxLjI1LjIuNi4zLjk1LjMuODUgMCAxLjY1LS42NSAxLjY1LTEuNjUgMC0uOS0uNi0xLjYtMS42LTEuNmgtLjM1VjguOWguOTV6Ii8+PC9zdmc+)

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen?style=flat&logo=spring)](https://spring.io/projects/spring-boot)
[![JWT](https://img.shields.io/badge/JWT-Authentication-blue?style=flat&logo=json-web-tokens)](https://jwt.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue?style=flat&logo=postgresql)](https://www.postgresql.org/)
[![Swagger](https://img.shields.io/badge/Swagger-API%20Docs-green?style=flat&logo=swagger)](https://swagger.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat)](LICENSE)

</div>

A robust backend application for Enterprise Resource Planning, focusing on Payroll Management and Employee Management.

## 📋 Table of Contents

- [🚀 Features](#-features)
- [🛠️ Technologies Used](#️-technologies-used)
- [📊 Database Schema](#-database-schema)
- [📝 API Documentation](#-api-documentation)
- [🔧 Running the Application](#-running-the-application)
- [🔒 Security](#-security)
- [👥 Role-Based Access](#-role-based-access)
- [⚠️ Error Handling](#️-error-handling)
- [📄 License](#-license)
- [👨‍💻 Contributing](#-contributing)
- [📞 Contact](#-contact)

## 🚀 Features

### 👤 Employee Management
- ✅ CRUD operations for employee personal information
- 🔐 Role-based security (ROLE_ADMIN, ROLE_MANAGER, ROLE_EMPLOYEE)
- 🔑 Authentication and Authorization using JWT

### 💼 Employment Management
- ✅ CRUD operations for employee employment details
- 📋 Track department, position, base salary, status, and joining date

### 💰 Deduction Management
- ✅ CRUD operations for deduction percentages
- 📊 Default deductions:
  - 💸 Employee Tax (30%)
  - 🏦 Pension (6%)
  - 🏥 Medical Insurance (5%)
  - 🏠 Housing (14%)
  - 🚌 Transport (14%)
  - 📦 Others (5%)

### 💵 Payroll Generation and Payslip
- 📈 Generate payroll for a given month/year based on base salary and deductions
- 👁️ View payslips for individual employees or all employees
- 🔄 Update payslip status (pending or paid)

### 📨 Messaging Feature
- 🔔 Automatic message generation when payslip status is updated to paid
- 📧 Email notification to employees

## 🛠️ Technologies Used

- ![Java](https://img.shields.io/badge/-Java%2021-orange?style=flat&logo=java) Java 21
- ![Spring Boot](https://img.shields.io/badge/-Spring%20Boot%203.5.0-brightgreen?style=flat&logo=spring) Spring Boot 3.5.0
- ![Spring Security](https://img.shields.io/badge/-Spring%20Security-brightgreen?style=flat&logo=spring-security) Spring Security with JWT Authentication
- ![Spring Data JPA](https://img.shields.io/badge/-Spring%20Data%20JPA-brightgreen?style=flat&logo=spring) Spring Data JPA
- ![PostgreSQL](https://img.shields.io/badge/-PostgreSQL-blue?style=flat&logo=postgresql) PostgreSQL
- ![Swagger](https://img.shields.io/badge/-Swagger/OpenAPI-green?style=flat&logo=swagger) Swagger/OpenAPI for API Documentation
- ![Spring Mail](https://img.shields.io/badge/-Spring%20Mail-brightgreen?style=flat&logo=spring) Spring Mail for Email Notifications

## 📊 Database Schema

The application uses the following entities:

- 👤 **Employee**: Stores employee personal information and authentication details
- 💼 **Employment**: Stores employee employment details
- 💰 **Deduction**: Stores deduction percentages
- 💵 **Payslip**: Stores payslip information
- 📨 **Message**: Stores messages sent to employees

## 📝 API Documentation

The API is documented using Swagger UI. Once the application is running, you can access the documentation at:

```bash
http://localhost:8080/swagger-ui.html
```

## 🔧 Running the Application

1. Make sure you have Java 21 installed
2. Configure PostgreSQL database in `application.properties`
3. Build the application using Maven:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## 🔒 Security

The application uses JWT for authentication and authorization. To access protected endpoints, you need to:

1. Register a user using `/api/v1/auth/register`
2. Login using `/api/v1/auth/login` to get a JWT token
3. Include the token in the Authorization header for subsequent requests:
   ```
   Authorization: Bearer <token>
   ```

## 👥 Role-Based Access

- 👑 **ROLE_ADMIN**: Can approve salary and perform all operations
- 👨‍💼 **ROLE_MANAGER**: Can process salary and add employee details
- 👨‍💻 **ROLE_EMPLOYEE**: Can view their own details, download payslip, and view pending salary payments

## ⚠️ Error Handling

The application implements a comprehensive error handling mechanism to provide consistent and informative error responses:

### 📋 Error Response Format

All API errors return a standardized JSON response with the following structure:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found with id: 123",
  "path": "/api/v1/employees/123",
  "timestamp": "2023-06-15 10:30:45",
  "errors": []
}
```

For validation errors, the `errors` array contains field-specific validation errors:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation error",
  "path": "/api/v1/employees",
  "timestamp": "2023-06-15 10:30:45",
  "errors": [
    {
      "field": "email",
      "message": "Email should be valid"
    },
    {
      "field": "firstName",
      "message": "First name is required"
    }
  ]
}
```

### 🔍 Exception Types

The application handles different types of exceptions:

1. ❌ **ResourceNotFoundException**: When a requested resource is not found (HTTP 404)
2. 🔄 **DuplicateResourceException**: When attempting to create a resource that already exists (HTTP 409)
3. 🔒 **AuthenticationException**: For authentication-related errors (HTTP 401)
4. ⚠️ **ValidationException**: For validation errors (HTTP 400)
5. 🚫 **BusinessException**: For general business logic errors (HTTP 400)
6. 🛑 **Access Denied**: For authorization errors (HTTP 403)
7. ⚠️ **Validation Errors**: From @Valid annotations (HTTP 400)
8. 💥 **Unexpected Errors**: All other exceptions (HTTP 500)

### 🌐 Global Exception Handling

All exceptions are handled centrally by a global exception handler, ensuring consistent error responses across the application.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📞 Contact

For any questions or suggestions, please contact:

- 👨‍💻 **Developer**: Elissa Sibomana
- 📧 **Email**: sibomanaelissa71@gmail.com
- 📞 **Phone**/**Whatsapp**: +250796146694
