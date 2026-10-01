# Advanced Library Management System

## 1. Introduction

This is a Java-based Library Management System made for the Programming in Java course project.

The application runs from the command line and uses MySQL for storing the library data.

The main purpose of the project is to manage books, book copies, members, librarians, loans, reservations, fines, payments and membership plans in one system.

---

## 2. Features

### Admin

The admin section provides options such as:

* Search books
* View all books
* Check book-copy availability
* View library information

### Librarian

The librarian can:

* Issue a book
* Return a book
* Renew a loan
* Check whether a book copy is available
* View member loans
* View overdue loans
* View reservations
* View unpaid fines

### Member

After logging in, a member can:

* Search books
* View all books
* Check book-copy availability
* View own loans
* View active loans
* Renew own loan
* Return a book
* Reserve a book
* View own reservations
* Cancel own reservation
* View own fines
* Pay a fine

The member menu gets the member information from the logged-in user, so the member normally does not have to enter their own member ID.

---

# 3. Technologies Used

* Java 17
* Maven
* MySQL
* JDBC
* JUnit 5
* Git and GitHub

---

# 4. Requirements

The following software is required:

* JDK 17 or later
* Maven 3.9 or later
* MySQL 8.0 or later
* Git

The project was developed using Java 17 and Maven 3.9.16.

---

# 5. Check Java

Open PowerShell or Command Prompt and run:

```powershell
java -version
```

Also check the compiler:

```powershell
javac -version
```

Java 17 should be installed before running the project.

---

# 6. Check Maven

Run:

```powershell
mvn -version
```

If Maven is not added to PATH, it can be run using its full path.

For example:

```powershell
& "C:\path\to\apache-maven-3.9.16\bin\mvn.cmd" -version
```

Change the path according to where Maven is installed.

---

# 7. Clone the Repository

Clone the project:

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
```

Then open the project directory:

```bash
cd Advanced-Library-Management-System
```

---

# 8. Database Setup

The application uses a MySQL database called:

```text
library_management
```

First start the MySQL server.

Then create the database:

```sql
CREATE DATABASE library_management;
```

Select it:

```sql
USE library_management;
```

The database tables and other SQL required by the project are present in:

```text
database/schema.sql
```

Run `schema.sql` after creating the database.

---

# 9. Database Configuration

The database connection code is in:

```text
src/main/java/com/library/util/DatabaseConnection.java
```

The application connects to the MySQL database:

```text
library_management
```

The database username and password are entered when database configuration is requested by the application.

Use the MySQL username and password available on the computer where the project is being run.

Do not upload the actual database password to GitHub.

---

# 10. Project Dependencies

The project uses Maven.

The dependency information is stored in:

```text
pom.xml
```

The project uses MySQL Connector/J for MySQL connectivity and JUnit 5 for testing.

To download the dependencies and build the project:

```powershell
mvn clean install
```

If Maven is not available in PATH:

```powershell
& "C:\path\to\apache-maven-3.9.16\bin\mvn.cmd" clean install
```

---

# 11. Compile the Project

Run:

```powershell
mvn clean compile
```

If Maven is not in PATH:

```powershell
& "C:\path\to\apache-maven-3.9.16\bin\mvn.cmd" clean compile
```

If the compilation is successful, Maven displays:

```text
BUILD SUCCESS
```

---

# 12. Run the Application

The main class is:

```text
com.library.Main
```

Run:

```powershell
mvn exec:java -Dexec.mainClass=com.library.Main
```

If Maven is not in PATH:

```powershell
& "C:\path\to\apache-maven-3.9.16\bin\mvn.cmd" exec:java "-Dexec.mainClass=com.library.Main"
```

The application starts in the terminal.

---

# 13. Basic Application Flow

The basic flow is:

```text
Start
  ↓
Database Configuration
  ↓
Login
  ↓
User Role
  ↓
Role Menu
  ↓
Select Operation
  ↓
Service
  ↓
Repository
  ↓
MySQL
```

For example, when a librarian issues a book, the application checks the member and the selected book copy before creating the loan.

---

# 14. Membership Plans

The project has membership plans stored in the database.

A membership plan contains information such as:

* Plan name
* Duration
* Maximum number of books
* Loan period
* Fine per day
* Membership fee

The member's plan is used when checking the borrowing limit and calculating the loan period.

---

# 15. Loan Management

The loan module handles:

* Issuing books
* Returning books
* Renewing loans
* Due dates
* Return dates
* Loan status
* Renewal count

Before issuing a book, the application checks the membership status and whether the selected copy is available.

The renewal operation also checks the renewal limit.

---

# 16. Reservations

Members can reserve books from the member menu.

A reservation stores:

* Book ID
* Member ID
* Reservation date
* Expiry date
* Reservation status
* Priority number

Members can view and cancel their reservations.

---

# 17. Fines

The application can calculate and store fines for loans.

A fine contains:

* Fine ID
* Loan ID
* Member ID
* Fine type
* Amount
* Paid amount
* Fine status
* Description
* Creation date

The fine-per-day value comes from the member's membership plan.

---

# 18. Payments

The project also contains a payment module for paying fines.

The supported payment methods are:

```text
CASH
CARD
UPI
NET_BANKING
```

Payment records contain:

* Payment ID
* Fine ID
* Member ID
* Amount
* Payment method
* Transaction reference
* Payment date
* Payment status

The application checks the payment amount before creating the payment record.

---

# 19. Exception Handling

The project contains custom exceptions for different operations.

Some of the exceptions are:

```text
LibraryException
BookNotFoundException
BookUnavailableException
BorrowingLimitExceededException
LoanNotFoundException
MemberNotFoundException
MembershipExpiredException
ReservationException
PaymentException
ValidationException
MembershipException
```

These are used to show a suitable error message when an operation cannot be completed.

---

# 20. Project Structure

```text
Advanced-Library-Management-System/
│
├── README.md
├── statement.md
├── pom.xml
│
├── database/
│   └── schema.sql
│
├── docs/
│
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── library/
    │               ├── Main.java
    │               ├── controller/
    │               ├── enums/
    │               ├── exception/
    │               ├── model/
    │               ├── repository/
    │               ├── service/
    │               └── util/
    │
    └── test/
        └── java/
```

### Main packages

**model**

Contains classes representing things such as users, members, loans, fines and payments.

**controller**

Connects the command-line operations with the service classes.

**service**

Contains the main application rules and validation.

**repository**

Contains database operations using JDBC.

**exception**

Contains the custom exceptions used by the application.

**enums**

Contains fixed values used by the application.

**util**

Contains utility classes such as the database connection class.

---

# 21. OOP Concepts Used

The project uses several Java concepts from the course.

These include:

* Classes and objects
* Encapsulation
* Inheritance
* Polymorphism
* Exception handling
* Collections
* Enums
* Generics
* Java Date and Time API
* JDBC

Inheritance is used in the user-related classes:

```text
User
├── Member
└── Librarian
```

---

# 22. Database Architecture

The application uses separate layers for handling different tasks.

```text
Main
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

For example, the repository classes handle SQL queries while the service classes perform checks before the database operation.

---

# 23. Validation and Access Checks

Some checks included in the project are:

* Invalid login details are rejected.
* Invalid IDs are rejected.
* Expired memberships cannot issue new books.
* Unavailable book copies cannot be issued.
* Borrowing limits are checked.
* Renewal limits are checked.
* Members can only access their own normal member operations.
* A member cannot renew another member's loan.
* A member cannot cancel another member's reservation.
* Payment amounts must be greater than zero.
* Payment amounts cannot exceed the remaining fine.

Prepared statements are used for database queries.

---

# 24. Testing

JUnit 5 is included in the project.

Run the tests using:

```powershell
mvn test
```

For a complete Maven build and test:

```powershell
mvn clean test
```

If Maven is not in PATH:

```powershell
& "C:\path\to\apache-maven-3.9.16\bin\mvn.cmd" clean test
```

The application was also checked manually through the command-line menus.

The manual checks included login, book search, viewing books, copy availability, issuing books, returning books, renewing loans, reservations, fines and member-access checks.

---

# 25. Future Improvements

Some features that can be added in a future version are:

* Web interface
* REST API
* Email reminders
* SMS notifications
* Barcode or QR-code support
* Online payment gateway
* Library reports and analytics
* Book recommendation system
* Mobile application

These are future ideas and are not part of the current command-line version.

---

# 26. Quick Setup

For a quick setup, follow these steps:

### Step 1

Install Java 17, Maven and MySQL.

### Step 2

Clone the repository:

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
```

### Step 3

Create the database:

```sql
CREATE DATABASE library_management;
```

### Step 4

Run:

```text
database/schema.sql
```

### Step 5

Open the project directory:

```bash
cd Advanced-Library-Management-System
```

### Step 6

Download dependencies and compile:

```bash
mvn clean compile
```

### Step 7

Run the application:

```bash
mvn exec:java -Dexec.mainClass=com.library.Main
```

### Step 8

Login through the command-line menu and select the required operation.

---

# 27. Project Information

**Project Name:** Advanced Library Management System

**Course:** Programming in Java

**Programming Language:** Java 17

**Database:** MySQL

**Build Tool:** Maven

**Application Type:** Command-Line Application
