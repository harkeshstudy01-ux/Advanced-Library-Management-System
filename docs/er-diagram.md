# ER Diagram

## 1. Overview

The Advanced Library Management System uses a relational MySQL database named `library_management`.

The database contains tables for users, members, librarians, books, authors, categories, publishers, book copies, loans, reservations, fines, payments, membership plans, reviews, requests, notifications, audit information and other library operations.

The ER diagram below represents the major entities and their relationships.

---

## 2. Main ER Diagram

```text
                                      ┌──────────────────────┐
                                      │   membership_plans    │
                                      ├──────────────────────┤
                                      │ PK plan_id           │
                                      │    plan_name         │
                                      │    duration_months   │
                                      │    max_books         │
                                      │    loan_period_days  │
                                      │    fine_per_day      │
                                      │    membership_fee    │
                                      └──────────┬───────────┘
                                                 │
                                                 │ 1
                                                 │
                                                 │ N
                                      ┌──────────▼───────────┐
                                      │       members        │
                                      ├──────────────────────┤
                                      │ PK member_id         │
                                      │ FK user_id           │
                                      │ FK plan_id           │
                                      │ UK membership_number │
                                      │    registration_date │
                                      │    expiry_date       │
                                      │    membership_status │
                                      └──────────┬───────────┘
                                                 │
                                                 │ N
                                                 │
                                                 │ 1
                                      ┌──────────▼───────────┐
                                      │        users         │
                                      ├──────────────────────┤
                                      │ PK user_id           │
                                      │    username          │
                                      │    password_hash     │
                                      │    full_name         │
                                      │    email             │
                                      │    phone             │
                                      │    role              │
                                      │    account_status    │
                                      └──────────────────────┘
                                                 ▲
                                                 │
                                                 │ 1
                                      ┌──────────┴───────────┐
                                      │      librarians      │
                                      ├──────────────────────┤
                                      │ PK librarian_id      │
                                      │ FK user_id           │
                                      │ UK employee_code     │
                                      │    department        │
                                      │    joining_date      │
                                      │    shift             │
                                      └──────────────────────┘
```

---

## 3. Book Management Entities

```text
┌──────────────────────┐
│      categories      │
├──────────────────────┤
│ PK category_id       │
│ UK category_name     │
│    description       │
└──────────┬───────────┘
           │
           │ 1:N
           │
┌──────────▼───────────┐
│        books         │
├──────────────────────┤
│ PK book_id           │
│ UK isbn              │
│    title             │
│    subtitle          │
│ FK category_id       │
│ FK publisher_id      │
│    publication_year  │
│    edition           │
│    language          │
│    pages             │
│    description       │
│    price             │
│    added_date        │
│    book_status       │
└───────┬──────────────┘
        │
        │ 1:N
        │
        ▼
┌──────────────────────┐
│    book_copies       │
├──────────────────────┤
│ PK copy_id           │
│ FK book_id           │
│ FK shelf_id          │
│ UK accession_number  │
│ UK barcode           │
│    purchase_date     │
│    purchase_price    │
│    condition_status  │
│    availability_status│
└──────────┬───────────┘
           │
           │ N:1
           │
           ▼
┌──────────────────────┐
│    book_shelves      │
├──────────────────────┤
│ PK shelf_id          │
│ UK shelf_code        │
│    floor_number      │
│    section_name      │
│    capacity          │
│    current_count     │
└──────────────────────┘


┌──────────────────────┐
│      publishers      │
├──────────────────────┤
│ PK publisher_id      │
│ UK publisher_name    │
└──────────┬───────────┘
           │
           │ 1:N
           │
           └──────────────► books
```

---

## 4. Author and Tag Relationships

Books can have multiple authors and authors can be associated with multiple books.

Therefore, the many-to-many relationship is implemented using the `book_authors` junction table.

```text
┌──────────────────────┐
│       authors        │
├──────────────────────┤
│ PK author_id         │
│    author_name       │
│    biography         │
│    nationality       │
│    email             │
│    phone             │
│    city              │
│    country           │
└──────────┬───────────┘
           │
           │ 1:N
           │
┌──────────▼───────────┐
│    book_authors      │
├──────────────────────┤
│ PK/FK book_id        │
│ PK/FK author_id      │
└──────────┬───────────┘
           │
           │ N:1
           │
           ▼
       ┌────────┐
       │ books  │
       └────────┘
```

### Book Tags

```text
┌──────────────────────┐
│         tags         │
├──────────────────────┤
│ PK tag_id            │
│ UK tag_name          │
└──────────┬───────────┘
           │
           │ 1:N
           │
┌──────────▼───────────┐
│      book_tags       │
├──────────────────────┤
│ PK/FK book_id        │
│ PK/FK tag_id         │
└──────────┬───────────┘
           │
           │ N:1
           ▼
        ┌───────┐
        │ books │
        └───────┘
```

Thus:

```text
Authors  N ─── M  Books
          through
      book_authors

Tags     N ─── M  Books
          through
       book_tags
```

---

## 5. Loan Management

The `loans` table connects members with physical book copies.

```text
┌──────────────────────┐
│       members        │
│    PK member_id      │
└──────────┬───────────┘
           │
           │ 1:N
           │
           ▼
┌──────────────────────┐
│        loans         │
├──────────────────────┤
│ PK loan_id           │
│ FK copy_id           │
│ FK member_id         │
│ FK librarian_id      │
│    issue_date        │
│    due_date          │
│    return_date       │
│    loan_status       │
│    renewal_count     │
└───────┬──────────────┘
        │
        │ N:1
        │
        ▼
┌──────────────────────┐
│    book_copies       │
│    PK copy_id        │
└──────────────────────┘

loans
   │
   │ FK librarian_id
   │
   ▼
librarians
```

### Loan Relationship

A member can have multiple loans:

```text
Member 1 ───────── N Loans
```

A physical book copy can appear in multiple loan records over time:

```text
Book Copy 1 ─────── N Loans
```

A librarian can process multiple loans:

```text
Librarian 1 ─────── N Loans
```

---

## 6. Reservation Management

Reservations connect members with books.

```text
┌──────────────────────┐
│       members        │
├──────────────────────┤
│ PK member_id         │
└──────────┬───────────┘
           │
           │ 1:N
           │
           ▼
┌──────────────────────┐
│    reservations      │
├──────────────────────┤
│ PK reservation_id    │
│ FK book_id           │
│ FK member_id         │
│    reservation_date  │
│    expiry_date       │
│    reservation_status│
│    priority_number   │
└──────────┬───────────┘
           │
           │ N:1
           ▼
┌──────────────────────┐
│        books         │
│    PK book_id        │
└──────────────────────┘
```

Relationship:

```text
Member 1 ───── N Reservations N ───── 1 Book
```

The `priority_number` field allows reservations to be handled in an ordered queue.

---

## 7. Fine Management

A fine is associated with a loan and a member.

```text
┌──────────────────────┐
│        loans         │
├──────────────────────┤
│ PK loan_id           │
└──────────┬───────────┘
           │
           │ 1:1
           │
           ▼
┌──────────────────────┐
│        fines         │
├──────────────────────┤
│ PK fine_id           │
│ UK loan_id           │
│ FK member_id         │
│    fine_type         │
│    amount            │
│    paid_amount       │
│    fine_status       │
│    description       │
│    created_at        │
└──────────┬───────────┘
           │
           │ 1:N
           │
           ▼
┌──────────────────────┐
│       payments       │
├──────────────────────┤
│ PK payment_id        │
│ FK fine_id           │
│ FK member_id         │
│    amount            │
│    payment_method    │
│ UK transaction_ref  │
│    payment_date      │
│    payment_status    │
└──────────────────────┘
```

Relationship:

```text
Loan 1 ───── 1 Fine
Fine 1 ───── N Payments
Member 1 ─── N Fines
Member 1 ─── N Payments
```

The `payments` table supports partial payments because a fine can have a `paid_amount` smaller than its total `amount`.

---

## 8. Membership Payment

Membership-related payments are stored separately.

```text
┌──────────────────────┐
│       members        │
├──────────────────────┤
│ PK member_id         │
└──────────┬───────────┘
           │
           │ 1:N
           │
           ▼
┌─────────────────────────┐
│   membership_payments   │
├─────────────────────────┤
│ PK membership_payment_id│
│ FK member_id            │
│ FK plan_id              │
│    amount               │
│    payment_method       │
│    transaction_reference│
│    payment_date         │
│    payment_status       │
└────────────┬────────────┘
             │
             │ N:1
             ▼
┌──────────────────────┐
│   membership_plans   │
│    PK plan_id        │
└──────────────────────┘
```

Relationship:

```text
Member 1 ───── N Membership Payments
Plan   1 ───── N Membership Payments
```

---

## 9. Book Reviews

Members can review books.

```text
┌──────────────────────┐
│       members        │
│    PK member_id      │
└──────────┬───────────┘
           │
           │ 1:N
           ▼
┌──────────────────────┐
│     book_reviews     │
├──────────────────────┤
│ PK review_id         │
│ FK book_id           │
│ FK member_id         │
│    rating            │
│    review_text       │
│    review_date       │
│    review_status     │
└──────────┬───────────┘
           │
           │ N:1
           ▼
┌──────────────────────┐
│        books         │
│    PK book_id        │
└──────────────────────┘
```

Relationship:

```text
Member 1 ───── N Reviews N ───── 1 Book
```

---

## 10. Book Requests

Members can request books that are not currently available in the library collection.

```text
┌──────────────────────┐
│       members        │
│    PK member_id      │
└──────────┬───────────┘
           │
           │ 1:N
           ▼
┌──────────────────────┐
│    book_requests     │
├──────────────────────┤
│ PK request_id        │
│ FK member_id         │
│    title             │
│    author_name       │
│    isbn              │
│    reason             │
│    request_date      │
│    request_status    │
│ FK processed_by      │
└──────────┬───────────┘
           │
           │ processed_by
           ▼
       librarians
```

Relationships:

```text
Member 1 ───── N Book Requests
Librarian 1 ── N Book Requests
```

---

## 11. Damage Reports

Damage reports are associated with physical book copies.

```text
┌──────────────────────┐
│    book_copies       │
│    PK copy_id        │
└──────────┬───────────┘
           │
           │ 1:N
           ▼
┌──────────────────────┐
│    damage_reports    │
├──────────────────────┤
│ PK damage_report_id  │
│ FK copy_id           │
│ FK reported_by       │
│    damage_type       │
│    description       │
│    estimated_cost    │
│    report_date       │
│    resolution_status │
└──────────┬───────────┘
           │
           │ reported_by
           ▼
         users
```

Relationships:

```text
Book Copy 1 ───── N Damage Reports
User      1 ───── N Damage Reports
```

---

## 12. User Activity and Security Tables

Several tables maintain user activity and system records.

### Notifications

```text
users 1 ───── N notifications
```

```text
┌──────────────────────┐
│    notifications     │
├──────────────────────┤
│ PK notification_id   │
│ FK user_id           │
│    title             │
│    message           │
│    notification_type │
│    is_read           │
│    created_at        │
└──────────────────────┘
```

### Login History

```text
users 1 ───── N login_history
```

```text
┌──────────────────────┐
│    login_history     │
├──────────────────────┤
│ PK login_id          │
│ FK user_id           │
│    login_time        │
│    logout_time       │
│    ip_address        │
│    login_status      │
│    device_info       │
└──────────────────────┘
```

### Audit Logs

```text
users 1 ───── N audit_logs
```

```text
┌──────────────────────┐
│     audit_logs       │
├──────────────────────┤
│ PK audit_id          │
│ FK user_id           │
│    action_type       │
│    table_name        │
│    record_id         │
│    action_description│
│    ip_address        │
│    action_time       │
└──────────────────────┘
```

---

## 13. Library Configuration Tables

### Holidays

The `holidays` table stores library holidays and closure information.

```text
┌──────────────────────┐
│      holidays        │
├──────────────────────┤
│ PK holiday_id        │
│ UK holiday_date      │
│    holiday_name      │
│    description       │
│    is_library_closed │
└──────────────────────┘
```

This table is independent and is used for library calendar/configuration purposes.

### Library Settings

```text
┌──────────────────────┐
│  library_settings    │
├──────────────────────┤
│ PK setting_id        │
│ UK setting_key       │
│    setting_value     │
│    description       │
│    updated_at        │
└──────────────────────┘
```

The table stores configurable library settings using key-value style records.

---

## 14. Complete Relationship Overview

```text
                         ┌──────────────────┐
                         │ membership_plans │
                         └────────┬─────────┘
                                  │
                                  │
                         ┌────────▼─────────┐
                         │     members      │
                         └────┬───┬───┬─────┘
                              │   │   │
               ┌──────────────┘   │   └──────────────┐
               │                  │                  │
               ▼                  ▼                  ▼
             loans          reservations        book_reviews
               │                  │                  │
               ▼                  ▼                  ▼
         book_copies            books               books
               │                  │
               │                  │
               ▼                  ▼
       damage_reports         book_authors
                                  │
                                  ▼
                               authors

books
  │
  ├──────────────► categories
  │
  ├──────────────► publishers
  │
  ├──────────────► book_copies
  │
  ├──────────────► reservations
  │
  └──────────────► book_reviews

book_copies
  │
  ├──────────────► book_shelves
  │
  ├──────────────► loans
  │
  └──────────────► damage_reports

loans
  │
  └──────────────► fines
                         │
                         ▼
                      payments

members
  │
  ├──────────────► membership_payments
  │
  ├──────────────► notifications
  │
  ├──────────────► login_history
  │
  └──────────────► audit_logs

users
  │
  ├──────────────► members
  │
  ├──────────────► librarians
  │
  ├──────────────► notifications
  │
  ├──────────────► login_history
  │
  └──────────────► audit_logs
```

---

## 15. Primary Keys and Foreign Keys

| Table               | Primary Key             | Important Foreign Keys                 |
| ------------------- | ----------------------- | -------------------------------------- |
| users               | `user_id`               | —                                      |
| members             | `member_id`             | `user_id`, `plan_id`                   |
| librarians          | `librarian_id`          | `user_id`                              |
| membership_plans    | `plan_id`               | —                                      |
| categories          | `category_id`           | —                                      |
| publishers          | `publisher_id`          | —                                      |
| authors             | `author_id`             | —                                      |
| books               | `book_id`               | `category_id`, `publisher_id`          |
| book_authors        | `book_id + author_id`   | `book_id`, `author_id`                 |
| tags                | `tag_id`                | —                                      |
| book_tags           | `book_id + tag_id`      | `book_id`, `tag_id`                    |
| book_shelves        | `shelf_id`              | —                                      |
| book_copies         | `copy_id`               | `book_id`, `shelf_id`                  |
| loans               | `loan_id`               | `copy_id`, `member_id`, `librarian_id` |
| reservations        | `reservation_id`        | `book_id`, `member_id`                 |
| fines               | `fine_id`               | `loan_id`, `member_id`                 |
| payments            | `payment_id`            | `fine_id`, `member_id`                 |
| membership_payments | `membership_payment_id` | `member_id`, `plan_id`                 |
| book_reviews        | `review_id`             | `book_id`, `member_id`                 |
| book_requests       | `request_id`            | `member_id`, `processed_by`            |
| damage_reports      | `damage_report_id`      | `copy_id`, `reported_by`               |
| notifications       | `notification_id`       | `user_id`                              |
| login_history       | `login_id`              | `user_id`                              |
| audit_logs          | `audit_id`              | `user_id`                              |
| holidays            | `holiday_id`            | —                                      |
| library_settings    | `setting_id`            | —                                      |

---

## 16. Important Cardinalities

The major cardinalities in the system are:

```text
Membership Plan 1 ───── N Members

User            1 ───── 1 Member
User            1 ───── 1 Librarian

Category        1 ───── N Books
Publisher       1 ───── N Books

Book            1 ───── N Book Copies
Book Shelf      1 ───── N Book Copies

Book            N ───── M Authors
                         through book_authors

Book            N ───── M Tags
                         through book_tags

Member          1 ───── N Loans
Book Copy       1 ───── N Loans
Librarian       1 ───── N Loans

Member          1 ───── N Reservations
Book            1 ───── N Reservations

Loan            1 ───── 1 Fine
Fine            1 ───── N Payments

Member          1 ───── N Payments
Member          1 ───── N Membership Payments

Member          1 ───── N Reviews
Book            1 ───── N Reviews

Member          1 ───── N Book Requests
Librarian       1 ───── N Book Requests

Book Copy       1 ───── N Damage Reports
User            1 ───── N Damage Reports

User            1 ───── N Notifications
User            1 ───── N Login History
User            1 ───── N Audit Logs
```

---

## 17. Database Design Principles

The database follows a relational design in which related information is separated into appropriate tables.

For example:

* Book information is stored separately from physical book copies.
* Author information is separated from books.
* Many-to-many relationships use junction tables.
* Members and librarians are connected to the common `users` table.
* Loans connect members, book copies and librarians.
* Fines are connected to loans.
* Payments are connected to fines.
* Membership plans control borrowing limits and loan periods.

This separation reduces unnecessary duplication and makes the database easier to maintain.

---

## 18. Conclusion

The ER diagram represents the database structure of the Advanced Library Management System.

The central entities are users, members, librarians, books, book copies, membership plans, loans, reservations, fines and payments. Additional entities support authors, categories, publishers, tags, reviews, requests, damage tracking, notifications, security logging and library configuration.

The database design supports the major functions of the Java application while keeping data organized through primary keys, foreign keys, unique constraints and relational connections.
