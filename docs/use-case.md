# Use Case Documentation

## 1. Overview

The Advanced Library Management System has three main user roles:

* Administrator
* Librarian
* Member

Each role has different responsibilities and access to different operations.

The main purpose of role-based operations is to ensure that users only perform actions appropriate to their role.

---

## 2. Actors

### 2.1 Administrator

The administrator manages and views important library information and administrative operations.

### 2.2 Librarian

The librarian handles day-to-day library transactions such as issuing books, returning books, renewals, reservations, fines, and payments.

### 2.3 Member

The member uses the system to manage their own library activities such as viewing loans, renewing books, reservations, fines, and payments.

### 2.4 MySQL Database

The MySQL database is not a human user, but it acts as an external system component that stores and retrieves application data.

---

# 3. Use Case Diagram

A simplified representation of the major use cases is:

```text
                         +--------------------------------------+
                         | Advanced Library Management System   |
                         |                                      |
Admin -----------------> | View/Search Books                    |
                         |                                      |
Librarian -------------> | Issue Book                           |
                         | Return Book                          |
                         | Renew Loan                           |
                         | View Member Loans                    |
                         | Check Book Availability              |
                         | Manage Reservations                  |
                         | Manage Fines                         |
                         | Process Fine Payments                |
                         |                                      |
Member ----------------> | View My Loans                        |
                         | View Active Loans                    |
                         | Renew My Loan                        |
                         | Return My Book                       |
                         | Reserve Book                         |
                         | View My Reservations                 |
                         | Cancel My Reservation                |
                         | View My Fines                        |
                         | Pay My Fine                          |
                         |                                      |
                         | User Login                           |
                         | Logout                               |
                         +--------------------------------------+
                                      |
                                      v
                               MySQL Database
```

---

# 4. Common Use Cases

## UC-01: User Login

**Actor:** Administrator, Librarian, Member

### Description

The user logs into the system using their username and password.

### Preconditions

* The user must have an account.
* The database must be available.

### Main Flow

1. User starts the application.
2. User selects login.
3. User enters username.
4. User enters password.
5. System verifies the credentials.
6. System checks the account status.
7. System identifies the user's role.
8. The appropriate menu is displayed.

### Alternative Flow

If the credentials are incorrect, the system displays a login error.

### Postcondition

The authenticated user is taken to the appropriate role menu.

---

# 5. Administrator Use Cases

## UC-02: View All Books

**Actor:** Administrator

### Description

The administrator can view the books available in the library database.

### Main Flow

1. Administrator logs in.
2. Administrator selects the book viewing option.
3. System retrieves book information.
4. System displays the available records.

---

## UC-03: Search Books

**Actor:** Administrator

### Description

The administrator can search for books using available search functionality.

### Main Flow

1. Administrator selects book search.
2. Administrator enters search information.
3. System searches the database.
4. Matching books are displayed.

---

# 6. Librarian Use Cases

## UC-04: Issue Book

**Actor:** Librarian

### Description

The librarian issues an available book copy to an eligible member.

### Preconditions

* Member must exist.
* Membership must be active.
* Member must not exceed the borrowing limit.
* Book copy must be available.

### Main Flow

1. Librarian selects Issue Book.
2. Librarian enters member ID.
3. Librarian enters copy ID.
4. System validates the member.
5. System checks membership status.
6. System checks borrowing limit.
7. System checks book-copy availability.
8. System calculates the due date.
9. System creates the loan.
10. System updates the copy status.
11. System displays a successful issue message.

### Alternative Flow

The operation is rejected if:

* Member does not exist.
* Membership is expired or inactive.
* Borrowing limit has been reached.
* Book copy is unavailable.

---

## UC-05: Return Book

**Actor:** Librarian

### Description

The librarian records the return of a borrowed book.

### Main Flow

1. Librarian selects Return Book.
2. Librarian enters the loan ID.
3. System finds the loan.
4. System validates the loan.
5. System records the return date.
6. System updates the loan status.
7. System updates the book-copy availability.
8. System checks whether an overdue fine is required.
9. System displays the result.

---

## UC-06: Renew Loan

**Actor:** Librarian

### Description

The librarian can renew an eligible loan.

### Main Flow

1. Librarian selects loan renewal.
2. Librarian enters the loan ID.
3. System retrieves the loan.
4. System checks the loan status.
5. System checks the renewal count.
6. System retrieves the applicable membership loan period.
7. System calculates the new due date.
8. System updates the loan.
9. System displays the updated due date.

### Alternative Flow

Renewal is rejected if the maximum renewal limit has been reached.

---

## UC-07: View Member Loans

**Actor:** Librarian

### Description

The librarian can view the loans associated with a member.

### Main Flow

1. Librarian selects member loan information.
2. Librarian enters the member ID.
3. System retrieves the member's loans.
4. System displays the loan information.

---

## UC-08: Check Book Availability

**Actor:** Librarian

### Description

The librarian can check whether a specific book copy is available.

### Main Flow

1. Librarian selects availability checking.
2. Librarian enters the copy ID.
3. System retrieves the copy.
4. System checks its availability status.
5. System displays the result.

---

## UC-09: Manage Reservations

**Actor:** Librarian

### Description

The librarian can view and manage reservations associated with books.

### Main Flow

1. Librarian selects reservation management.
2. Librarian enters the required book or reservation information.
3. System retrieves reservation records.
4. System displays the reservation information.
5. Required reservation operations can be performed.

---

## UC-10: View and Manage Fines

**Actor:** Librarian

### Description

The librarian can view fine information and outstanding amounts.

### Main Flow

1. Librarian selects fine management.
2. System retrieves fine information.
3. System displays outstanding fine records.
4. The librarian can process appropriate fine operations.

---

## UC-11: Process Fine Payment

**Actor:** Librarian

### Description

The system allows a fine payment to be recorded.

### Main Flow

1. Librarian selects the payment operation.
2. Fine information is provided.
3. System verifies the fine.
4. System checks the outstanding amount.
5. Payment information is validated.
6. Payment is recorded.
7. Fine payment information is updated.
8. System displays the payment result.

---

# 7. Member Use Cases

## UC-12: View My Loans

**Actor:** Member

### Description

The member can view their own loan records.

### Main Flow

1. Member logs in.
2. System identifies the member associated with the logged-in user.
3. Member selects My Loans.
4. System retrieves the member's loans.
5. System displays the results.

The member does not need to enter their own member ID for this operation.

---

## UC-13: View Active Loans

**Actor:** Member

### Description

The member can view only their currently active loans.

### Main Flow

1. Member selects Active Loans.
2. System identifies the logged-in member.
3. System retrieves active loans.
4. System displays the results.

---

## UC-14: Renew My Loan

**Actor:** Member

### Description

A member can renew their own eligible loan.

### Main Flow

1. Member selects Renew Loan.
2. Member enters the loan ID.
3. System retrieves the loan.
4. System verifies that the loan belongs to the logged-in member.
5. System checks the loan status.
6. System checks the renewal limit.
7. System calculates the new due date.
8. System updates the loan.
9. System displays the result.

### Security Rule

A member cannot renew another member's loan through the member interface.

---

## UC-15: Return My Book

**Actor:** Member

### Description

The member can initiate the return of a book associated with their own loan.

### Main Flow

1. Member selects Return Book.
2. Member enters the loan ID.
3. System retrieves the loan.
4. System verifies ownership.
5. System processes the return.
6. System updates the loan.
7. System updates the book-copy status.

### Security Rule

A member cannot return another member's loan.

---

## UC-16: Reserve Book

**Actor:** Member

### Description

The member can create a reservation for a book.

### Main Flow

1. Member selects Reserve Book.
2. Member enters the book ID.
3. System checks the book.
4. System checks reservation information.
5. System determines the reservation priority.
6. System creates the reservation.
7. System displays the reservation details.

---

## UC-17: View My Reservations

**Actor:** Member

### Description

The member can view their own reservations.

### Main Flow

1. Member selects My Reservations.
2. System identifies the logged-in member.
3. System retrieves the member's reservations.
4. System displays the records.

---

## UC-18: Cancel My Reservation

**Actor:** Member

### Description

The member can cancel one of their own reservations.

### Main Flow

1. Member selects Cancel Reservation.
2. Member enters the reservation ID.
3. System retrieves the reservation.
4. System verifies ownership.
5. System cancels the reservation.
6. System displays confirmation.

### Security Rule

A member cannot cancel another member's reservation.

---

## UC-19: View My Fines

**Actor:** Member

### Description

The member can view fines associated with their account.

### Main Flow

1. Member selects My Fines.
2. System identifies the logged-in member.
3. System retrieves the member's fines.
4. System displays fine information.

---

## UC-20: Pay My Fine

**Actor:** Member

### Description

The member can make a payment against an outstanding fine.

### Main Flow

1. Member selects Pay Fine.
2. Member provides the required fine information.
3. System verifies the fine.
4. System verifies that the fine belongs to the member.
5. System checks the outstanding amount.
6. System validates the payment.
7. System records the payment.
8. System updates the fine.
9. System displays the payment result.

---

# 8. Role-Based Access Summary

| Operation               | Admin                 | Librarian | Member                              |
| ----------------------- | --------------------- | --------- | ----------------------------------- |
| Login                   | Yes                   | Yes       | Yes                                 |
| View/Search Books       | Yes                   | Yes       | Available through system operations |
| Issue Book              | No                    | Yes       | No                                  |
| Return Book             | No                    | Yes       | Own loan                            |
| Renew Loan              | No                    | Yes       | Own loan                            |
| View Member Loans       | Yes/Administrative    | Yes       | Own loans                           |
| Check Copy Availability | Yes/Administrative    | Yes       | Through member operations           |
| Manage Reservations     | Administrative access | Yes       | Own reservations                    |
| View Fines              | Administrative access | Yes       | Own fines                           |
| Process Fine Payment    | Administrative access | Yes       | Own fine                            |
| Logout                  | Yes                   | Yes       | Yes                                 |

---

# 9. Security and Ownership Rules

The member interface contains ownership checks for member-specific operations.

For example:

```text
Logged-in Member
       |
       v
Requested Loan
       |
       v
Compare Loan.memberId
with logged-in memberId
       |
   +---+---+
   |       |
  Same   Different
   |       |
   v       v
Allow    Reject
```

The same principle is applied to member-specific reservations and fines.

This prevents a member from directly operating on another member's records through the member menu.

---

# 10. Use Case Summary

The major use cases of the system are:

```text
Authentication
      |
      +-- Login
      |
      +-- Logout

Book Management
      |
      +-- View Books
      +-- Search Books
      +-- Check Availability

Loan Management
      |
      +-- Issue Book
      +-- Return Book
      +-- Renew Loan
      +-- View Loans

Reservation Management
      |
      +-- Reserve Book
      +-- View Reservations
      +-- Cancel Reservation

Fine Management
      |
      +-- View Fines
      +-- Calculate Fine
      +-- Pay Fine
```

The use-case structure demonstrates how the different roles interact with the major functions of the Advanced Library Management System.
