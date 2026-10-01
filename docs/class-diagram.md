# Class Diagram

## 1. Overview

The class structure of the Advanced Library Management System follows an object-oriented design. The application separates data models, business logic, controllers, repositories, and utility classes.

The main relationships between the classes are shown below.

---

## 2. Main Class Diagram

```text
+----------------------+
|        User          |
+----------------------+
| - userId             |
| - username           |
| - passwordHash       |
| - fullName           |
| - email              |
| - phone              |
| - role               |
| - accountStatus      |
+----------------------+
| + isActive()         |
+----------+-----------+
           ^
           |
     +-----+------+
     |            |
     |            |
+---------+   +------------+
| Member  |   | Librarian  |
+---------+   +------------+
| memberId|   |librarianId |
| planId  |   |employeeCode|
| member- |   |department  |
| Number  |   |joiningDate |
| expiry  |   |shift       |
| status  |   +------------+
+---------+
     |
     | uses
     v
+----------------------+
|  MembershipPlan      |
+----------------------+
| - planId             |
| - planName           |
| - durationMonths     |
| - maxBooks           |
| - loanPeriodDays     |
| - finePerDay         |
| - membershipFee      |
+----------------------+

+----------------------+
|        Loan          |
+----------------------+
| - loanId             |
| - copyId             |
| - memberId           |
| - librarianId        |
| - issueDate          |
| - dueDate            |
| - returnDate         |
| - loanStatus         |
| - renewalCount       |
+----------------------+
| + isReturned()       |
| + isOverdue()        |
| + getOverdueDays()   |
+----------------------+

+----------------------+
|     Reservation      |
+----------------------+
| - reservationId      |
| - bookId             |
| - memberId           |
| - reservationDate    |
| - expiryDate         |
| - reservationStatus  |
| - priorityNumber     |
+----------------------+
| + isActive()         |
| + isExpired()        |
+----------------------+

+----------------------+
|        Fine          |
+----------------------+
| - fineId             |
| - loanId             |
| - memberId           |
| - fineType           |
| - amount             |
| - paidAmount         |
| - fineStatus         |
| - description        |
| - createdAt          |
+----------------------+
| + getRemainingAmount()|
| + isPaid()           |
+----------------------+

+----------------------+
|       Payment        |
+----------------------+
| - paymentId          |
| - fineId             |
| - memberId           |
| - amount             |
| - paymentMethod      |
| - transactionRef     |
| - paymentDate        |
| - paymentStatus      |
+----------------------+
```

---

# 3. Entity Relationships

The main relationships between the model classes can be represented as:

```text
User
 |
 +--------------------+
 |                    |
 v                    v
Member             Librarian
 |
 v
MembershipPlan

Member
 |
 +---------> Loan
 |
 +---------> Reservation
 |
 +---------> Fine
 |
 +---------> Payment

Loan
 |
 +---------> Fine

Fine
 |
 +---------> Payment
```

---

# 4. User Inheritance

`Member` and `Librarian` extend the `User` class.

```text
                 +-------------+
                 |    User     |
                 +-------------+
                       ^
                       |
             +---------+---------+
             |                   |
             |                   |
      +-------------+     +-------------+
      |   Member    |     |  Librarian  |
      +-------------+     +-------------+
```

This represents inheritance.

The common user information is stored in `User`, while member-specific and librarian-specific information is stored in their respective classes.

---

# 5. Member and MembershipPlan

A member is associated with a membership plan.

```text
+----------------+       +---------------------+
|     Member     |       |   MembershipPlan    |
+----------------+       +---------------------+
| membershipPlanId| ----> | planId             |
| membershipNumber|       | planName           |
| expiryDate      |       | maxBooks           |
| status          |       | loanPeriodDays     |
+----------------+       | finePerDay         |
                         | membershipFee      |
                         +---------------------+
```

The membership plan determines important rules such as:

* Maximum number of active books.
* Loan period.
* Fine per day.
* Membership fee.

---

# 6. Member and Loan

A member can have multiple loan records over time.

```text
+----------------+       +----------------+
|     Member     | 1   * |      Loan      |
+----------------+-------+----------------+
| memberId       |       | loanId         |
+----------------+       | memberId       |
                         | copyId         |
                         | dueDate        |
                         | loanStatus     |
                         +----------------+
```

The `memberId` connects a loan with its member.

---

# 7. Loan and Fine

A fine can be associated with a loan.

```text
+----------------+       +----------------+
|      Loan      | 1   0..1 |    Fine    |
+----------------+-------+----------------+
| loanId         |       | fineId         |
| dueDate        |       | loanId         |
| returnDate     |       | amount         |
| loanStatus     |       | paidAmount     |
+----------------+       | fineStatus     |
                         +----------------+
```

The system can create a fine when a loan requires a fine, such as an overdue transaction.

---

# 8. Fine and Payment

A fine can have payment records associated with it.

```text
+----------------+       +----------------+
|      Fine      | 1   * |    Payment     |
+----------------+-------+----------------+
| fineId         |       | paymentId      |
| amount         |       | fineId         |
| paidAmount     |       | memberId       |
| fineStatus     |       | amount         |
+----------------+       | paymentMethod  |
                         | paymentStatus  |
                         +----------------+
```

The payment record stores information about a payment made against a fine.

---

# 9. Member and Reservation

A member can create reservations.

```text
+----------------+       +--------------------+
|     Member     | 1   * |    Reservation     |
+----------------+-------+--------------------+
| memberId       |       | reservationId      |
| membershipNo   |       | bookId             |
+----------------+       | memberId           |
                         | reservationDate    |
                         | expiryDate         |
                         | priorityNumber     |
                         | reservationStatus  |
                         +--------------------+
```

The reservation stores which member made the reservation and which book was reserved.

---

# 10. Controller Classes

The controller layer contains classes that coordinate user operations.

```text
+-----------------------+
|   Controller Layer    |
+-----------------------+
| AdminController       |
| BookController        |
| FineController        |
| LoanController        |
| LoginController       |
| MemberController      |
| PaymentController     |
| ReservationController |
+-----------+-----------+
            |
            v
      Service Layer
```

Each controller calls the appropriate service class instead of directly performing database operations.

---

# 11. Service Classes

The service layer contains the business logic.

```text
+--------------------------+
|      Service Layer       |
+--------------------------+
| LoanService              |
| MemberService            |
| MembershipPlanService    |
| FineService              |
| PaymentService           |
+-------------+------------+
              |
              v
       Repository Layer
```

The service layer is responsible for applying business rules before database operations are performed.

---

# 12. Repository Classes

The repository layer communicates with MySQL through JDBC.

```text
+--------------------------+
|    Repository Layer      |
+--------------------------+
| MemberRepository         |
| LoanRepository           |
| ReservationRepository    |
| FineRepository           |
| PaymentRepository        |
| MembershipPlanRepository |
| BookRepository           |
+-------------+------------+
              |
              v
             JDBC
              |
              v
        MySQL Database
```

The repository classes contain SQL and database access operations.

---

# 13. Utility Class

The main database utility is:

```text
+-----------------------+
| DatabaseConnection    |
+-----------------------+
| configure()           |
| getConnection()       |
| testConnection()      |
| configureFromConsole()|
+-----------------------+
```

It provides database connections to repository classes.

---

# 14. Exception Classes

The application uses custom exceptions derived from `LibraryException`.

```text
                    +----------------------+
                    |   LibraryException   |
                    +----------+-----------+
                               ^
                               |
       +-----------+-----------+-----------+-----------+
       |           |           |           |           |
       v           v           v           v           v
BookNotFound   LoanNotFound  MemberNotFound  PaymentException
Exception      Exception     Exception       ValidationException
       |
       +--> BookUnavailableException
       |
       +--> BorrowingLimitExceededException
       |
       +--> MembershipExpiredException
       |
       +--> MembershipException
       |
       +--> ReservationException
```

These classes allow different application errors to be represented clearly.

---

# 15. Complete Layer Relationship

The overall class organization can be represented as:

```text
                         +----------------+
                         |    Main.java   |
                         +-------+--------+
                                 |
                                 v
                    +-------------------------+
                    |     Controllers         |
                    +------------+------------+
                                 |
                                 v
                    +-------------------------+
                    |       Services          |
                    +------------+------------+
                                 |
                                 v
                    +-------------------------+
                    |      Repositories       |
                    +------------+------------+
                                 |
                                 v
                              JDBC
                                 |
                                 v
                    +-------------------------+
                    |     MySQL Database       |
                    +-------------------------+

 Supporting classes:

 Models <------ Services
   |
   +-- User
   +-- Member
   +-- Librarian
   +-- Loan
   +-- Reservation
   +-- Fine
   +-- Payment
   +-- MembershipPlan

 Enums <------ Models / Services

 Exceptions <------ Services / Controllers

 Utilities <------ Repositories
```

---

# 16. Important Relationships Summary

| Class      | Relationship | Related Class      |
| ---------- | ------------ | ------------------ |
| Member     | Extends      | User               |
| Librarian  | Extends      | User               |
| Member     | Uses         | MembershipPlan     |
| Member     | Has          | Loan               |
| Member     | Has          | Reservation        |
| Member     | Has          | Fine               |
| Member     | Has          | Payment            |
| Loan       | May have     | Fine               |
| Fine       | Has          | Payment            |
| Controller | Uses         | Service            |
| Service    | Uses         | Repository         |
| Repository | Uses         | DatabaseConnection |
| Repository | Uses         | JDBC               |
| Models     | Represent    | Database entities  |

---

# 17. Object-Oriented Concepts Represented

The class design demonstrates several Java object-oriented concepts.

### Encapsulation

Model fields are private and accessed through methods such as getters and setters.

### Inheritance

`Member` and `Librarian` inherit common properties from `User`.

### Abstraction

The service and repository layers hide implementation details from the main application.

### Association

Classes such as `Member`, `Loan`, `Fine`, `Payment`, and `Reservation` are related through their identifiers and application logic.

### Separation of Responsibilities

Different classes are responsible for different parts of the application instead of placing all functionality in one class.

---

# 18. Conclusion

The class design separates the data models, business logic, database operations, controllers, utilities, and exception handling.

The major application flow is:

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

This structure makes the Advanced Library Management System easier to understand, maintain, test, and extend.
