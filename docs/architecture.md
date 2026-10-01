# System Architecture

## 1. Overview

The Advanced Library Management System follows a layered architecture. The application is divided into different layers so that user interaction, business logic, database operations, and data models remain separated.

The main flow of the application is:

```text
User
  |
  v
Main.java
  |
  v
Controller Layer
  |
  v
Service Layer
  |
  v
Repository Layer
  |
  v
JDBC
  |
  v
MySQL Database
```

This structure makes the application easier to understand, test, maintain, and extend.

---

## 2. Architecture Layers

### 2.1 Main / Command-Line Layer

The main entry point of the application is:

```text
src/main/java/com/library/Main.java
```

`Main.java` provides the command-line interface of the application.

It is responsible for:

* Starting the application.
* Displaying menus.
* Reading user input.
* Managing the login flow.
* Displaying available operations.
* Calling the appropriate controller methods.

The main class does not directly execute SQL queries. Database operations are handled by the repository layer.

---

## 3. Controller Layer

Location:

```text
src/main/java/com/library/controller/
```

The controller layer connects the command-line interface with the service layer.

Controllers receive requests from `Main.java` and call the appropriate service methods.

Current controllers include:

```text
AdminController
BookController
FineController
LoanController
LoginController
MemberController
PaymentController
ReservationController
```

### Responsibilities

The controller layer is responsible for:

* Receiving operations from the user interface.
* Calling the appropriate service.
* Returning results to the main application.
* Keeping the user-interface code separate from business logic.

For example:

```text
Main
  |
  v
LoanController
  |
  v
LoanService
```

The controller should not contain large amounts of database code. Its main purpose is to coordinate application operations.

---

## 4. Service Layer

Location:

```text
src/main/java/com/library/service/
```

The service layer contains the main business logic of the application.

Examples include:

```text
LoanService
MemberService
MembershipPlanService
FineService
PaymentService
```

### Responsibilities

The service layer handles rules such as:

* Checking whether a member exists.
* Checking membership validity.
* Checking borrowing limits.
* Checking whether a book copy is available.
* Calculating loan periods.
* Applying membership-plan rules.
* Checking renewal limits.
* Validating payments.
* Managing fine-related operations.

For example, when a member tries to borrow a book:

```text
LoanService
    |
    +-- Check member
    |
    +-- Check membership
    |
    +-- Check borrowing limit
    |
    +-- Check book-copy availability
    |
    +-- Create loan
```

Keeping these rules in the service layer prevents the `Main.java` class from becoming too large and makes the business logic easier to maintain.

---

## 5. Repository Layer

Location:

```text
src/main/java/com/library/repository/
```

The repository layer handles communication with the database.

Examples include repositories for:

* Members
* Loans
* Reservations
* Fines
* Payments
* Membership plans
* Books

### Responsibilities

The repository layer performs database operations such as:

* `SELECT`
* `INSERT`
* `UPDATE`
* Retrieving generated keys.
* Mapping database rows to Java objects.

The repository layer uses JDBC to communicate with MySQL.

Example:

```text
LoanService
     |
     v
LoanRepository
     |
     v
JDBC
     |
     v
MySQL
```

This separation means that SQL code does not need to be written inside the controller or service classes.

---

## 6. Model Layer

Location:

```text
src/main/java/com/library/model/
```

The model layer contains Java classes that represent the main entities of the system.

Examples include:

```text
User
Member
Librarian
Loan
Reservation
Fine
Payment
MembershipPlan
```

These classes contain data related to the corresponding entities.

For example, a `Loan` object contains information such as:

* Loan ID
* Copy ID
* Member ID
* Librarian ID
* Issue date
* Due date
* Return date
* Loan status
* Renewal count

The model classes allow database records to be represented as Java objects.

---

## 7. Enum Layer

Location:

```text
src/main/java/com/library/enums/
```

The enum layer contains predefined values used by the application.

Examples include:

```text
AvailabilityStatus
BookCondition
FineStatus
LoanStatus
ReservationStatus
Role
```

Enums help prevent invalid values from being used in the application.

For example, a loan can have statuses such as:

```text
ISSUED
RETURNED
OVERDUE
LOST
```

---

## 8. Exception Layer

Location:

```text
src/main/java/com/library/exception/
```

The exception layer contains custom exceptions used for application and business errors.

Examples include:

```text
BookNotFoundException
BookUnavailableException
BorrowingLimitExceededException
LoanNotFoundException
MemberNotFoundException
MembershipExpiredException
MembershipException
PaymentException
ReservationException
ValidationException
```

These exceptions allow the application to provide meaningful error messages instead of exposing raw database or programming errors to the user.

For example:

```text
Member attempts to borrow a book
          |
          v
Membership expired
          |
          v
MembershipExpiredException
          |
          v
Meaningful message shown to user
```

---

## 9. Utility Layer

Location:

```text
src/main/java/com/library/util/
```

The utility layer contains common supporting functionality used by other parts of the application.

The main utility currently used is:

```text
DatabaseConnection
```

`DatabaseConnection` is responsible for:

* Configuring database credentials.
* Creating JDBC connections.
* Testing the database connection.
* Providing connections to repository classes.

The repositories use this utility instead of creating database connections independently.

---

## 10. Database Layer

The database is MySQL.

Database name:

```text
library_management
```

The database contains tables for the major entities and operations of the library system.

Important tables include:

```text
users
books
book_copies
members
librarians
membership_plans
loans
reservations
fines
payments
categories
authors
publishers
```

The database provides persistent storage for the application.

---

## 11. Complete Request Flow

A typical operation follows this structure:

```text
                    USER
                      |
                      v
                 Main.java
                      |
                      v
                Controller
                      |
                      v
                  Service
                      |
                      v
                Repository
                      |
                      v
                    JDBC
                      |
                      v
              MySQL Database
                      |
                      v
                Repository
                      |
                      v
                  Service
                      |
                      v
                Controller
                      |
                      v
                 Main.java
                      |
                      v
                  USER
```

---

## 12. Example: Book Issue

When a librarian issues a book, the operation follows these steps:

```text
1. User selects "Issue Book"
             |
             v
2. Main.java receives input
             |
             v
3. LoanController receives the request
             |
             v
4. LoanService validates the operation
             |
             +---- Check member
             |
             +---- Check membership status
             |
             +---- Check borrowing limit
             |
             +---- Check copy availability
             |
             v
5. LoanRepository creates the loan
             |
             v
6. JDBC executes SQL
             |
             v
7. MySQL stores the loan
             |
             v
8. Result returns through the layers
             |
             v
9. User receives confirmation
```

---

## 13. Example: Fine and Payment Flow

Fine and payment operations follow a similar layered structure:

```text
User
 |
 v
Main.java
 |
 v
FineController / PaymentController
 |
 v
FineService / PaymentService
 |
 v
FineRepository / PaymentRepository
 |
 v
JDBC
 |
 v
MySQL
```

The service layer validates the fine and payment information before the repository performs the database operation.

---

## 14. Package Structure

The main Java package structure is:

```text
com.library
|
+-- Main.java
|
+-- controller
|   +-- AdminController
|   +-- BookController
|   +-- FineController
|   +-- LoanController
|   +-- LoginController
|   +-- MemberController
|   +-- PaymentController
|   +-- ReservationController
|
+-- enums
|   +-- AvailabilityStatus
|   +-- BookCondition
|   +-- FineStatus
|   +-- LoanStatus
|   +-- ReservationStatus
|   +-- Role
|
+-- exception
|   +-- Custom application exceptions
|
+-- model
|   +-- User
|   +-- Member
|   +-- Librarian
|   +-- Loan
|   +-- Reservation
|   +-- Fine
|   +-- Payment
|   +-- MembershipPlan
|
+-- repository
|   +-- Database access classes
|
+-- service
|   +-- Business logic classes
|
+-- util
    +-- DatabaseConnection
```

---

## 15. Advantages of the Architecture

The layered architecture provides several benefits:

### Separation of Responsibilities

Each layer has a specific responsibility.

### Maintainability

Changes in one layer can generally be made without rewriting the complete application.

### Reusability

Service methods can be called by different controllers when required.

### Testability

Business logic can be tested separately from the command-line interface.

### Database Separation

SQL and JDBC code are kept inside the repository layer rather than being mixed with user-interface code.

### Extensibility

The current command-line application can later be extended with a web or desktop interface while keeping much of the service and repository logic.

---

## 16. Summary

The Advanced Library Management System uses a layered Java architecture:

```text
Main / CLI
    ↓
Controller
    ↓
Service
    ↓
Repository
    ↓
JDBC
    ↓
MySQL
```

The model, enum, exception, and utility packages support these layers.

This structure keeps the project organized and separates user interaction, business rules, database access, data representation, and error handling.
