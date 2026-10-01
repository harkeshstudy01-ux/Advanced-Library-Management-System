# Advanced Library Management System

## 1. Project Title

**Advanced Library Management System**

## 2. Introduction

The Advanced Library Management System is a Java-based command-line application developed to manage the main activities of a library in a structured way. The system connects to a MySQL database so that information about books, members, loans, reservations, fines, payments, librarians, and membership plans can be stored and managed.

The main purpose of this project is to replace basic manual library operations with a computerized system that provides better organization, validation, and control over library transactions.

The project is developed using Java and follows a layered structure consisting of controllers, services, repositories, models, utilities, and custom exceptions.

---

## 3. Problem Statement

Managing a library manually can become difficult when the number of books and members increases. Tracking book copies, issuing and returning books, checking membership validity, calculating fines, handling reservations, and recording payments requires accurate and consistent data.

A small mistake in these operations can result in incorrect loan records, duplicate reservations, incorrect fines, or unauthorized access to member information.

Therefore, this project aims to develop a Java-based Library Management System that can:

* Maintain book and book-copy information.
* Manage library members and membership plans.
* Issue and return books.
* Renew books according to membership rules.
* Check book and membership availability.
* Manage book reservations.
* Calculate and manage overdue fines.
* Record fine payments.
* Apply role-based access for administrators, librarians, and members.
* Store application data persistently in MySQL.

---

## 4. Objectives

The main objectives of this project are:

1. To develop a functional Library Management System using Java.
2. To apply Object-Oriented Programming concepts in a practical project.
3. To connect a Java application with a MySQL database using JDBC.
4. To implement a layered architecture for better code organization.
5. To provide separate operations for administrators, librarians, and members.
6. To validate membership rules before allowing borrowing operations.
7. To implement book issue, return, renewal, and reservation functionality.
8. To calculate fines according to membership-plan rules.
9. To record payments related to library fines.
10. To handle invalid operations using custom exception classes.
11. To provide a command-line interface that can be executed from a terminal.
12. To create a maintainable project structure using Maven.

---

## 5. Scope of the Project

The project focuses on the core operations required to manage a library.

### Included in the scope

* User login and role-based access.
* Administrator operations.
* Librarian operations.
* Member self-service operations.
* Book and book-copy searching.
* Book availability checking.
* Member validation.
* Membership-plan based borrowing limits.
* Book issue and return.
* Book renewal.
* Book reservations.
* Fine calculation.
* Fine payment recording.
* MySQL database integration.
* Input validation.
* Exception handling.
* Maven-based project management.

### Outside the current scope

The current version is a command-line application. Web-based access, mobile applications, online payment gateways, barcode hardware integration, and cloud deployment are not part of the current implementation.

These can be considered as possible future improvements.

---

## 6. Users and Roles

The system supports three main roles.

### Administrator

The administrator can perform administrative library operations such as viewing and searching library information.

### Librarian

The librarian is responsible for operational activities such as:

* Issuing books.
* Returning books.
* Renewing loans.
* Checking member loans.
* Checking book-copy availability.
* Managing reservations.
* Viewing and managing fines.

### Member

Members can perform operations related to their own account, including:

* Viewing their loans.
* Viewing active loans.
* Renewing their own eligible loans.
* Returning their borrowed books.
* Reserving books.
* Viewing their reservations.
* Cancelling their own reservations.
* Viewing their fines.
* Making fine payments.

The application also validates that members cannot perform operations on another member's records through the member interface.

---

## 7. Functional Requirements

### FR1 – User Authentication

The system shall allow registered users to log in using their credentials.

### FR2 – Role-Based Access

The system shall provide different operations according to the user's role:

* ADMIN
* LIBRARIAN
* MEMBER

### FR3 – Book Management

The system shall allow authorized users to:

* View books.
* Search books.
* View book details.
* View book copies.
* Check copy availability.

### FR4 – Member Management

The system shall maintain member information and validate whether a member is eligible to use library services.

### FR5 – Membership Plan Management

The system shall use membership-plan information such as:

* Maximum number of books.
* Loan period.
* Fine per day.
* Membership duration.

### FR6 – Book Issue

The system shall allow eligible members to borrow available book copies.

Before issuing a book, the system checks relevant conditions such as membership validity, borrowing limits, and copy availability.

### FR7 – Book Return

The system shall allow borrowed books to be returned and update the corresponding loan and copy status.

### FR8 – Book Renewal

The system shall allow eligible loans to be renewed according to the configured renewal limit and membership loan period.

### FR9 – Reservation Management

The system shall allow members to reserve books and manage their own active reservations.

### FR10 – Fine Management

The system shall calculate overdue fines based on the applicable membership-plan fine rate and maintain fine records.

### FR11 – Payment Management

The system shall record payments made against library fines and maintain payment information.

### FR12 – Validation and Exception Handling

The system shall validate user input and library operations and provide meaningful error messages when an operation cannot be completed.

---

## 8. Non-Functional Requirements

### NFR1 – Usability

The command-line interface should provide clear menus, prompts, and error messages so that users can understand the available operations.

### NFR2 – Reliability

The system should maintain consistent records when books are issued, returned, renewed, reserved, or paid for.

### NFR3 – Security

The system should restrict operations according to user roles and prevent members from directly operating on other members' records.

### NFR4 – Maintainability

The application should use separate model, repository, service, controller, exception, and utility classes so that individual components can be modified more easily.

### NFR5 – Performance

Database operations should retrieve only the required information and use appropriate SQL queries and database indexes where applicable.

### NFR6 – Portability

The application should be executable on systems that have a compatible Java JDK, Maven, and MySQL environment.

### NFR7 – Data Persistence

Library information should be stored in the MySQL database so that records are not lost when the application is closed.

---

## 9. Major Modules

The project is divided into several logical modules.

### 9.1 Authentication Module

Handles user login and role identification.

### 9.2 Book Management Module

Handles books, book copies, searching, and availability.

### 9.3 Member Management Module

Handles member information and membership validation.

### 9.4 Loan Management Module

Handles:

* Book issue.
* Book return.
* Loan tracking.
* Loan renewal.
* Overdue identification.

### 9.5 Reservation Module

Handles book reservations, cancellation, expiry, and reservation priority.

### 9.6 Membership Module

Handles membership plans and rules such as maximum books, loan duration, and fine rates.

### 9.7 Fine Module

Handles fine calculation, outstanding amounts, and fine status.

### 9.8 Payment Module

Handles payments made against library fines.

### 9.9 Exception Handling Module

Provides custom exceptions for different library-related errors such as unavailable books, invalid loans, membership problems, validation errors, and payment errors.

---

## 10. Technology Stack

| Technology   | Purpose                         |
| ------------ | ------------------------------- |
| Java 17      | Application development         |
| Maven        | Build and dependency management |
| MySQL        | Database management             |
| JDBC         | Java-to-MySQL connectivity      |
| JUnit 5      | Unit testing                    |
| Git          | Version control                 |
| GitHub       | Source-code repository          |
| Command Line | Application interface           |

---

## 11. System Architecture

The application follows a layered architecture.

```text
+----------------------+
|    Command Line UI   |
|       Main.java      |
+----------+-----------+
           |
           v
+----------------------+
|     Controllers      |
+----------+-----------+
           |
           v
+----------------------+
|       Services       |
| Business Logic       |
+----------+-----------+
           |
           v
+----------------------+
|     Repositories     |
|    JDBC / SQL        |
+----------+-----------+
           |
           v
+----------------------+
|    MySQL Database    |
+----------------------+
```

### Layer Responsibilities

**Main / CLI Layer**

Provides menus and receives input from the user.

**Controller Layer**

Connects user operations with the appropriate service.

**Service Layer**

Contains the main business rules and validations.

**Repository Layer**

Handles database operations using JDBC and SQL queries.

**Model Layer**

Represents entities such as users, members, books, loans, reservations, fines, payments, and membership plans.

**Exception Layer**

Contains custom exceptions used to handle application and business errors.

**Utility Layer**

Contains common utilities such as database connection handling.

---

## 12. Main System Workflow

A typical borrowing operation follows this flow:

```text
User Login
    |
    v
Role Verification
    |
    v
Member Validation
    |
    v
Check Membership Status
    |
    v
Check Borrowing Limit
    |
    v
Check Book Copy Availability
    |
    v
Create Loan Record
    |
    v
Update Book Copy Status
```

For returning a book:

```text
Select Loan
    |
    v
Validate Loan
    |
    v
Return Book
    |
    v
Update Loan Status
    |
    v
Update Book Copy Availability
    |
    v
Calculate Fine if Required
```

---

## 13. Object-Oriented Programming Concepts Used

The project applies important Java OOP concepts.

### Encapsulation

Entity classes use private fields with getters and setters to control access to object data.

### Inheritance

Classes such as `Member` and `Librarian` extend the common `User` class.

### Abstraction

Business operations are separated into service and repository classes instead of keeping all logic inside the main program.

### Polymorphism

Different controllers and service operations work with related objects and provide specialized behavior for different operations.

---

## 14. Database Integration

The application uses MySQL as the persistent database.

Java communicates with MySQL using JDBC.

The repository layer is responsible for operations such as:

* SELECT
* INSERT
* UPDATE
* Database record retrieval
* Generated-key retrieval
* Mapping database rows to Java objects

The database contains related entities for users, members, librarians, books, book copies, loans, reservations, membership plans, fines, and payments.

---

## 15. Validation and Exception Handling

The application performs validation before important operations.

Examples include:

* Checking whether a member exists.
* Checking whether membership is active.
* Checking whether a book copy is available.
* Checking borrowing limits.
* Checking loan ownership.
* Checking renewal limits.
* Checking valid payment amounts.
* Checking whether a fine exists.
* Checking whether a reservation belongs to the logged-in member.

Custom exceptions are used to make errors easier to understand and handle.

Examples include:

* `BookNotFoundException`
* `BookUnavailableException`
* `LoanNotFoundException`
* `MemberNotFoundException`
* `MembershipExpiredException`
* `BorrowingLimitExceededException`
* `ReservationException`
* `PaymentException`
* `ValidationException`
* `MembershipException`

---

## 16. Expected Outcome

The expected outcome of the project is a working Java-based Library Management System that can manage common library operations through a command-line interface and store data in MySQL.

The system demonstrates practical use of:

* Java OOP.
* Collections and classes.
* Exception handling.
* JDBC.
* SQL and relational databases.
* Layered software architecture.
* Input validation.
* Maven.
* Unit testing.
* Git and GitHub.

---

## 17. Limitations

The current version has some limitations:

1. The application is command-line based.
2. It requires a locally available MySQL database.
3. It does not currently provide a web or mobile interface.
4. Online payment gateway integration is not included.
5. Hardware-based barcode scanning is not included.
6. Deployment to a cloud server is outside the current project scope.

---

## 18. Future Enhancements

Possible future improvements include:

* Web-based interface.
* Mobile application.
* Barcode or QR-code integration.
* Email and SMS notifications.
* Online payment gateway integration.
* Advanced library reports and dashboards.
* Cloud deployment.
* Automated backup and recovery.
* More extensive automated testing.

---

## 19. Conclusion

The Advanced Library Management System provides a practical implementation of Java programming concepts in a real-world library scenario. It combines object-oriented programming, database connectivity, business logic, validation, exception handling, and role-based operations into one application.

The project is designed to be modular and maintainable so that additional functionality can be added in the future without changing the entire system.
