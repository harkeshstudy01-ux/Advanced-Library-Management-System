# System Workflow

## 1. Overview

The Advanced Library Management System uses a menu-driven command-line interface. A user first logs into the system, and the available operations depend on the user's role.

The main workflow is:

```text
Start Application
       |
       v
Database Configuration
       |
       v
User Login
       |
       v
Role Identification
       |
       +------------------+------------------+
       |                  |                  |
       v                  v                  v
    ADMIN            LIBRARIAN            MEMBER
       |                  |                  |
       v                  v                  v
 Admin Menu          Librarian Menu       Member Menu
       |                  |                  |
       +------------------+------------------+
                          |
                          v
                       Logout
                          |
                          v
                         End
```

---

## 2. Application Startup Workflow

When the application starts, the following process takes place:

```text
Start
  |
  v
Launch Main.java
  |
  v
Configure Database
  |
  v
Test Database Connection
  |
  v
Display Login Menu
```

The application requires a valid MySQL connection before database-dependent operations can be performed.

---

## 3. Login Workflow

The login process is:

```text
User enters username and password
              |
              v
       LoginController
              |
              v
        Verify user
              |
              v
      Check account status
              |
        +-----+-----+
        |           |
      Valid       Invalid
        |           |
        v           v
   Identify role  Show error
        |
        v
  Display role menu
```

If authentication is successful, the system identifies whether the user is an administrator, librarian, or member.

---

## 4. Administrator Workflow

The administrator can access administrative operations.

```text
Admin Login
    |
    v
Admin Menu
    |
    +--> View All Books
    |
    +--> Search Books
    |
    +--> Other available administrative operations
    |
    v
Logout
```

The administrator operations are handled through the controller and service layers before accessing the database.

---

## 5. Librarian Workflow

The librarian performs the main day-to-day library operations.

```text
Librarian Login
       |
       v
Librarian Menu
       |
       +--> Issue Book
       |
       +--> Return Book
       |
       +--> Renew Loan
       |
       +--> View Member Loans
       |
       +--> Check Book Availability
       |
       +--> View Overdue Loans
       |
       +--> Manage Reservations
       |
       +--> View Fines
       |
       +--> Process Fine Payment
       |
       v
Logout
```

---

# 6. Book Issue Workflow

Book issue is one of the main transactions of the system.

### Process

```text
Librarian selects Issue Book
            |
            v
Enter Member ID
            |
            v
Enter Copy ID
            |
            v
Check Member
            |
            v
Check Membership Status
            |
            +---- Invalid/Expired
            |          |
            |          v
            |      Reject Request
            |
            v
Check Borrowing Limit
            |
            +---- Limit Reached
            |          |
            |          v
            |      Reject Request
            |
            v
Check Copy Availability
            |
            +---- Not Available
            |          |
            |          v
            |      Reject Request
            |
            v
Calculate Due Date
            |
            v
Create Loan
            |
            v
Update Copy Status
            |
            v
Display Success
```

The due date is determined using the member's membership plan.

---

# 7. Member Book Issue Rules

Before creating a loan, the system checks important conditions.

### Membership

The member must have an active membership.

### Borrowing Limit

The member cannot exceed the maximum number of active books allowed by the membership plan.

### Copy Availability

The selected copy must be available for issue.

### Loan Period

The due date is calculated according to the loan period associated with the member's membership plan.

---

# 8. Book Return Workflow

The return process is:

```text
Librarian selects Return Book
             |
             v
Enter Loan ID
             |
             v
Find Loan
             |
             v
Check Loan Status
             |
             v
Update Return Date
             |
             v
Change Loan Status
to RETURNED
             |
             v
Update Book Copy
to AVAILABLE
             |
             v
Check Overdue Condition
             |
        +----+----+
        |         |
       Yes        No
        |         |
        v         v
 Calculate Fine  Complete
        |
        v
 Display Result
```

If a loan is overdue, the system can calculate the applicable fine.

---

# 9. Loan Renewal Workflow

Members can renew their own eligible loans.

```text
Member selects Renew Loan
             |
             v
Enter Loan ID
             |
             v
Find Loan
             |
             v
Verify Loan Belongs
to Logged-in Member
             |
             +---- No
             |     |
             |     v
             |   Reject
             |
             v
Check Loan Status
             |
             v
Check Renewal Count
             |
             +---- Maximum Reached
             |          |
             |          v
             |        Reject
             |
             v
Get Membership Loan Period
             |
             v
Calculate New Due Date
             |
             v
Update Loan
             |
             v
Display Success
```

The application currently limits the number of renewals according to the configured renewal rule.

---

# 10. Member Self-Service Workflow

After a member logs in, the application automatically identifies the member associated with the logged-in user.

The member does not need to repeatedly enter their own member ID for normal self-service operations.

```text
Member Login
     |
     v
Find Member from User ID
     |
     v
Member Menu
     |
     +--> View My Loans
     |
     +--> View Active Loans
     |
     +--> Renew My Loan
     |
     +--> Return My Book
     |
     +--> Reserve Book
     |
     +--> View My Reservations
     |
     +--> Cancel My Reservation
     |
     +--> View My Fines
     |
     +--> Pay My Fine
     |
     v
Logout
```

The system also checks ownership before member-specific operations are performed.

---

# 11. Reservation Workflow

A member can reserve a book through the reservation menu.

```text
Member selects Reserve Book
          |
          v
Enter Book ID
          |
          v
Check Book
          |
          v
Check Existing Reservation
          |
          v
Calculate Reservation Priority
          |
          v
Create Reservation
          |
          v
Display Reservation Details
```

A reservation contains information such as:

* Book ID
* Member ID
* Reservation date
* Expiry date
* Reservation status
* Priority number

---

# 12. Reservation Cancellation Workflow

```text
Member selects Cancel Reservation
              |
              v
Enter Reservation ID
              |
              v
Find Reservation
              |
              v
Verify Reservation Ownership
              |
              +---- Not Owner
              |       |
              |       v
              |     Reject
              |
              v
Cancel Reservation
              |
              v
Display Confirmation
```

This prevents a member from cancelling another member's reservation through the member interface.

---

# 13. Fine Calculation Workflow

Fine calculation uses the applicable membership-plan fine rate.

```text
Loan
 |
 v
Check Due Date
 |
 v
Check Return / Current Date
 |
 v
Determine Overdue Days
 |
 v
Get Member
 |
 v
Get Membership Plan
 |
 v
Get Fine Per Day
 |
 v
Calculate Fine
 |
 v
Store Fine
```

Conceptually:

```text
Fine Amount = Overdue Days × Fine Per Day
```

The actual fine rate depends on the member's membership plan.

---

# 14. Fine Payment Workflow

The payment process is:

```text
Member selects Pay Fine
          |
          v
Enter Fine ID
          |
          v
Find Fine
          |
          v
Check Fine Status
          |
          v
Calculate Remaining Amount
          |
          v
Validate Payment Amount
          |
          v
Validate Member
          |
          v
Create Payment Record
          |
          v
Update Fine Payment Amount
          |
          v
Update Fine Status
          |
          v
Display Payment Result
```

The payment record stores information such as:

* Fine ID
* Member ID
* Amount
* Payment method
* Transaction reference
* Payment date
* Payment status

---

# 15. Payment Validation

Before processing a payment, the system checks:

```text
Payment Request
      |
      v
Is Fine Valid?
      |
      v
Does Member Exist?
      |
      v
Does Fine Belong to Member?
      |
      v
Is Amount Valid?
      |
      v
Is Amount Within Outstanding Amount?
      |
      v
Create Payment
```

Invalid payment requests are rejected with an appropriate application exception.

---

# 16. Book Search Workflow

Book searching is available through the book management operations.

```text
User selects Search Books
          |
          v
Enter Search Text
          |
          v
BookController
          |
          v
BookService
          |
          v
BookRepository
          |
          v
MySQL
          |
          v
Matching Books
          |
          v
Display Results
```

The user can use the search operation to find relevant book records instead of manually checking every book.

---

# 17. Database Operation Workflow

Whenever a repository operation is required, the database flow is:

```text
Controller
    |
    v
Service
    |
    v
Repository
    |
    v
DatabaseConnection
    |
    v
JDBC Connection
    |
    v
SQL Query
    |
    v
MySQL
    |
    v
ResultSet / Generated Key
    |
    v
Java Object
    |
    v
Service
    |
    v
Controller
    |
    v
User
```

---

# 18. Exception Handling Workflow

When an invalid operation occurs, the system uses custom exceptions.

```text
User Operation
      |
      v
Validation
      |
      +---- Valid ------> Continue
      |
      +---- Invalid
              |
              v
       Custom Exception
              |
              v
       Error is handled
              |
              v
      Meaningful message
              |
              v
       Return to menu
```

Examples include:

* Book not found.
* Book unavailable.
* Loan not found.
* Membership expired.
* Borrowing limit exceeded.
* Invalid payment.
* Invalid input.
* Reservation error.

---

# 19. Logout Workflow

After completing operations, the user can select logout.

```text
Current Role Menu
       |
       v
Select Logout
       |
       v
End Current Session
       |
       v
Return to Login Menu
```

The application can then allow another user to log in.

---

# 20. Complete System Workflow

The complete system can be represented as:

```text
                         START
                           |
                           v
                  Database Configuration
                           |
                           v
                       Login Menu
                           |
                           v
                     Authenticate User
                           |
              +------------+------------+
              |            |            |
              v            v            v
            ADMIN      LIBRARIAN     MEMBER
              |            |            |
              v            v            v
         Admin Menu   Librarian Menu  Member Menu
              |            |            |
              |       +----+----+       |
              |       |    |    |       |
              |       v    v    v       |
              |     Issue Return Renew   |
              |       |    |    |       |
              |       +----+----+       |
              |            |            |
              |            v            |
              |      Reservations      |
              |            |            |
              |            v            |
              |          Fines          |
              |            |            |
              |            v            |
              |         Payments        |
              |                         |
              +------------+------------+
                           |
                           v
                         Logout
                           |
                           v
                       Login Menu
                           |
                           v
                          END
```

---

# 21. Summary

The workflow of the Advanced Library Management System is designed around controlled access and validation.

The general pattern is:

```text
Input
  ↓
Controller
  ↓
Service Validation
  ↓
Repository
  ↓
Database
  ↓
Result
  ↓
User
```

This workflow keeps the application's user interaction, business rules, and database operations separated while allowing the major library operations to work together as one system.
