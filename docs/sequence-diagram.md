# Sequence Diagram

## 1. Overview

The sequence diagram shows how different parts of the Advanced Library Management System communicate when a librarian issues a book copy to a member.

The main components involved are:

* Librarian
* Main / CLI
* LoanController
* LoanService
* MemberService
* MembershipPlanService
* LoanRepository
* Database
* Book copy information

The diagram focuses on the **book issue operation** because it is one of the main operations of the library system.

---

## 2. Book Issue Sequence Diagram

```text
Librarian
    |
    | 1. Select "Issue Book"
    |
    v
Main / CLI
    |
    | 2. Enter Member ID
    | 3. Enter Copy ID
    |
    v
LoanController
    |
    | 4. issueBook(memberId, copyId, librarianId)
    |
    v
LoanService
    |
    | 5. Validate member
    |
    v
MemberService
    |
    | 6. Find member
    |
    v
MemberRepository
    |
    | 7. SELECT member + user details
    |
    v
MySQL Database
    |
    | 8. Return member information
    |
    v
MemberRepository
    |
    | 9. Return Member object
    |
    v
MemberService
    |
    | 10. Check membership status
    |     and membership expiry
    |
    v
LoanService
    |
    | 11. Get membership plan
    |
    v
MembershipPlanService
    |
    | 12. Find membership plan
    |
    v
MembershipPlanRepository
    |
    | 13. SELECT membership plan
    |
    v
MySQL Database
    |
    | 14. Return plan information
    |
    v
MembershipPlanRepository
    |
    v
MembershipPlanService
    |
    | 15. Return max books and loan period
    |
    v
LoanService
    |
    | 16. Check member's active loan count
    |
    | 17. Check book copy availability
    |
    | 18. Validate borrowing rules
    |
    |-------------------------------|
    |                               |
    |  If validation fails          | If validation succeeds
    |                               |
    v                               v
Exception                       LoanRepository
    |                               |
    |                               | 19. Insert new loan
    |                               |
    |                               v
    |                           MySQL Database
    |                               |
    |                               | 20. Create loan record
    |                               |     with ISSUED status
    |                               |
    |                               v
    |                           LoanRepository
    |                               |
    |                               | 21. Return loan ID
    |                               |
    |                               v
    |                           LoanService
    |                               |
    |                               | 22. Return Loan object
    |                               |
    |                               v
    |                           LoanController
    |                               |
    |                               | 23. Return result
    |                               |
    |                               v
    |                           Main / CLI
    |                               |
    |                               | 24. Display success message
    |                               |
    |                               v
    |                            Librarian
```

---

## 3. Simplified UML-Style Sequence Diagram

The same operation can be represented more compactly as:

```text
Librarian -> Main:
    Select Issue Book

Main -> LoanController:
    issueBook(memberId, copyId, librarianId)

LoanController -> LoanService:
    issueBook(...)

LoanService -> MemberService:
    Validate member

MemberService -> MemberRepository:
    findById(memberId)

MemberRepository -> Database:
    SELECT member information

Database --> MemberRepository:
    Member data

MemberRepository --> MemberService:
    Member

MemberService --> LoanService:
    Valid member

LoanService -> MembershipPlanService:
    Get membership plan

MembershipPlanService -> MembershipPlanRepository:
    findById(planId)

MembershipPlanRepository -> Database:
    SELECT membership plan

Database --> MembershipPlanRepository:
    Plan data

MembershipPlanRepository --> MembershipPlanService:
    MembershipPlan

MembershipPlanService --> LoanService:
    Plan limits

LoanService -> LoanRepository:
    Check active loans

LoanRepository -> Database:
    SELECT active loans

Database --> LoanRepository:
    Loan count

LoanRepository --> LoanService:
    Active loan count

LoanService -> LoanRepository:
    Check copy availability

LoanRepository -> Database:
    SELECT book copy status

Database --> LoanRepository:
    Copy status

LoanRepository --> LoanService:
    Available

LoanService -> LoanRepository:
    Create loan

LoanRepository -> Database:
    INSERT loan

Database --> LoanRepository:
    Loan created

LoanRepository --> LoanService:
    Loan ID

LoanService --> LoanController:
    Loan object

LoanController --> Main:
    Success

Main --> Librarian:
    "Book issued successfully"
```

---

## 4. Successful Book Issue Flow

When all validations pass, the following sequence takes place:

1. Librarian selects **Issue Book**.
2. The system asks for the member ID.
3. The system asks for the book copy ID.
4. `LoanController` receives the request.
5. `LoanService` processes the request.
6. The member is retrieved from the database.
7. The system checks whether the member is active.
8. The membership expiry is checked.
9. The member's membership plan is retrieved.
10. The maximum allowed books are obtained from the membership plan.
11. The member's current active loan count is checked.
12. The selected book copy is checked for availability.
13. If all rules are satisfied, a new loan is created.
14. The new loan is stored in MySQL.
15. The loan receives an `ISSUED` status.
16. The result is returned to the controller.
17. The CLI displays the successful issue message.

---

## 5. Validation and Exception Flow

The system can stop the operation if any important validation fails.

### Expired Membership

```text
Librarian
   |
   v
Main
   |
   v
LoanController
   |
   v
LoanService
   |
   v
MemberService
   |
   |-- Membership expired
   |
   v
MembershipExpiredException
   |
   v
Main
   |
   v
Display error message
```

Example:

```text
Member membership is expired or inactive.
```

### Book Copy Unavailable

```text
Librarian
   |
   v
LoanController
   |
   v
LoanService
   |
   |-- Check copy
   |
   |-- Copy unavailable
   |
   v
BookUnavailableException
   |
   v
Main
   |
   v
Display error message
```

Example:

```text
Book copy is currently unavailable.
```

### Borrowing Limit Exceeded

```text
LoanService
     |
     |-- Get membership plan
     |
     |-- Check maximum allowed books
     |
     |-- Active loans >= maximum
     |
     v
BorrowingLimitExceededException
     |
     v
Main
     |
     v
Display error message
```

---

## 6. Important Objects Involved

| Component                  | Responsibility                               |
| -------------------------- | -------------------------------------------- |
| `Main`                     | Handles CLI interaction                      |
| `LoanController`           | Receives loan-related requests               |
| `LoanService`              | Applies borrowing business rules             |
| `MemberService`            | Retrieves and validates members              |
| `MembershipPlanService`    | Provides membership limits and loan period   |
| `LoanRepository`           | Performs loan database operations            |
| `MemberRepository`         | Performs member database operations          |
| `MembershipPlanRepository` | Performs membership-plan database operations |
| MySQL                      | Stores persistent library data               |

---

## 7. Layer Interaction

The sequence demonstrates the layered architecture used by the project:

```text
Presentation Layer
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
Database Layer
```

The layers have separate responsibilities. The CLI does not directly perform SQL operations. Database access is handled by repository classes.

---

## 8. Example Database Operation

During a successful issue operation, the repository creates a loan record containing information such as:

```text
copy_id
member_id
librarian_id
issue_date
due_date
loan_status
renewal_count
```

The initial loan status is:

```text
ISSUED
```

The due date is calculated according to the member's membership plan.

For example, a membership plan with a 21-day loan period results in a due date approximately 21 days after the issue date.

---

## 9. Book Issue Sequence Summary

```text
Librarian
    ↓
Main / CLI
    ↓
LoanController
    ↓
LoanService
    ↓
Validate Member
    ↓
Validate Membership
    ↓
Get Membership Plan
    ↓
Check Loan Limit
    ↓
Check Copy Availability
    ↓
LoanRepository
    ↓
MySQL
    ↓
Create Loan
    ↓
Return Result
    ↓
Main / CLI
    ↓
Librarian
```

---

## 10. Conclusion

The sequence diagram demonstrates how the Advanced Library Management System processes a book issue request through multiple application layers.

It shows the separation between user interaction, controllers, business logic, repositories, and the database. It also demonstrates validation and exception handling before a loan is created.

This design makes the application easier to maintain because each layer has a specific responsibility.
