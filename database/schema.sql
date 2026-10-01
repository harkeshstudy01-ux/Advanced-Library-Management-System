/* ============================================================
   ADVANCED LIBRARY MANAGEMENT SYSTEM
   Database: MySQL 8+
   File: database/schema.sql
   ============================================================ */

DROP DATABASE IF EXISTS library_management;

CREATE DATABASE library_management
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE library_management;


/* ============================================================
   1. USERS
   ============================================================ */

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    phone VARCHAR(20),
    role ENUM('ADMIN','LIBRARIAN','MEMBER') NOT NULL,
    account_status ENUM('ACTIVE','INACTIVE','SUSPENDED')
        DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);


/* ============================================================
   2. MEMBERSHIP PLANS
   ============================================================ */

CREATE TABLE membership_plans (
    plan_id INT AUTO_INCREMENT PRIMARY KEY,
    plan_name VARCHAR(50) NOT NULL UNIQUE,
    duration_months INT NOT NULL,
    max_books INT NOT NULL,
    loan_period_days INT NOT NULL,
    fine_per_day DECIMAL(10,2) NOT NULL DEFAULT 2.00,
    membership_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    CHECK (duration_months > 0),
    CHECK (max_books > 0),
    CHECK (loan_period_days > 0),
    CHECK (fine_per_day >= 0),
    CHECK (membership_fee >= 0)
);


/* ============================================================
   3. MEMBERS
   ============================================================ */

CREATE TABLE members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    plan_id INT NOT NULL,
    membership_number VARCHAR(30) NOT NULL UNIQUE,
    registration_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    occupation VARCHAR(100),
    address VARCHAR(255),
    date_of_birth DATE,
    membership_status ENUM('ACTIVE','EXPIRED','SUSPENDED')
        DEFAULT 'ACTIVE',

    FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,

    FOREIGN KEY (plan_id)
        REFERENCES membership_plans(plan_id)
        ON DELETE RESTRICT
);


/* ============================================================
   4. LIBRARIANS
   ============================================================ */

CREATE TABLE librarians (
    librarian_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    employee_code VARCHAR(30) NOT NULL UNIQUE,
    department VARCHAR(100),
    joining_date DATE NOT NULL,
    shift ENUM('MORNING','AFTERNOON','EVENING','FULL_DAY')
        DEFAULT 'FULL_DAY',

    FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);


/* ============================================================
   5. MEMBERSHIP PAYMENTS
   ============================================================ */

CREATE TABLE membership_payments (
    membership_payment_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    plan_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('CASH','CARD','UPI','NET_BANKING') NOT NULL,
    transaction_reference VARCHAR(100) UNIQUE,
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    payment_status ENUM('SUCCESS','PENDING','FAILED','REFUNDED')
        DEFAULT 'SUCCESS',

    FOREIGN KEY (member_id)
        REFERENCES members(member_id)
        ON DELETE CASCADE,

    FOREIGN KEY (plan_id)
        REFERENCES membership_plans(plan_id)
        ON DELETE RESTRICT,

    CHECK (amount >= 0)
);


/* ============================================================
   6. CATEGORIES
   ============================================================ */

CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);


/* ============================================================
   7. PUBLISHERS
   ============================================================ */

CREATE TABLE publishers (
    publisher_id INT AUTO_INCREMENT PRIMARY KEY,
    publisher_name VARCHAR(150) NOT NULL UNIQUE,
    email VARCHAR(120),
    phone VARCHAR(20),
    city VARCHAR(100),
    country VARCHAR(100)
);


/* ============================================================
   8. AUTHORS
   ============================================================ */

CREATE TABLE authors (
    author_id INT AUTO_INCREMENT PRIMARY KEY,
    author_name VARCHAR(150) NOT NULL,
    biography TEXT,
    nationality VARCHAR(80)
);


/* ============================================================
   9. BOOKS
   ============================================================ */

CREATE TABLE books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    subtitle VARCHAR(255),
    category_id INT NOT NULL,
    publisher_id INT,
    publication_year YEAR,
    edition VARCHAR(50),
    language VARCHAR(50) DEFAULT 'English',
    pages INT,
    description TEXT,
    price DECIMAL(10,2) DEFAULT 0.00,
    added_date DATE DEFAULT (CURRENT_DATE),
    book_status ENUM('ACTIVE','INACTIVE','ARCHIVED')
        DEFAULT 'ACTIVE',

    FOREIGN KEY (category_id)
        REFERENCES categories(category_id)
        ON DELETE RESTRICT,

    FOREIGN KEY (publisher_id)
        REFERENCES publishers(publisher_id)
        ON DELETE SET NULL,

    CHECK (pages IS NULL OR pages > 0),
    CHECK (price >= 0)
);


/* ============================================================
   10. BOOK AUTHORS
   ============================================================ */

CREATE TABLE book_authors (
    book_id INT NOT NULL,
    author_id INT NOT NULL,

    PRIMARY KEY (book_id, author_id),

    FOREIGN KEY (book_id)
        REFERENCES books(book_id)
        ON DELETE CASCADE,

    FOREIGN KEY (author_id)
        REFERENCES authors(author_id)
        ON DELETE CASCADE
);


/* ============================================================
   11. TAGS
   ============================================================ */

CREATE TABLE tags (
    tag_id INT AUTO_INCREMENT PRIMARY KEY,
    tag_name VARCHAR(50) NOT NULL UNIQUE
);


/* ============================================================
   12. BOOK TAGS
   ============================================================ */

CREATE TABLE book_tags (
    book_id INT NOT NULL,
    tag_id INT NOT NULL,

    PRIMARY KEY (book_id, tag_id),

    FOREIGN KEY (book_id)
        REFERENCES books(book_id)
        ON DELETE CASCADE,

    FOREIGN KEY (tag_id)
        REFERENCES tags(tag_id)
        ON DELETE CASCADE
);


/* ============================================================
   13. BOOK SHELVES
   ============================================================ */

CREATE TABLE book_shelves (
    shelf_id INT AUTO_INCREMENT PRIMARY KEY,
    shelf_code VARCHAR(30) NOT NULL UNIQUE,
    floor_number INT NOT NULL,
    section_name VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    current_count INT DEFAULT 0,

    CHECK (floor_number > 0),
    CHECK (capacity > 0),
    CHECK (current_count >= 0)
);


/* ============================================================
   14. BOOK COPIES
   ============================================================ */

CREATE TABLE book_copies (
    copy_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    shelf_id INT,
    accession_number VARCHAR(50) NOT NULL UNIQUE,
    barcode VARCHAR(80) NOT NULL UNIQUE,
    purchase_date DATE,
    purchase_price DECIMAL(10,2),

    condition_status ENUM(
        'NEW',
        'GOOD',
        'FAIR',
        'DAMAGED',
        'LOST'
    ) DEFAULT 'GOOD',

    availability_status ENUM(
        'AVAILABLE',
        'ISSUED',
        'RESERVED',
        'LOST',
        'DAMAGED',
        'MAINTENANCE'
    ) DEFAULT 'AVAILABLE',

    FOREIGN KEY (book_id)
        REFERENCES books(book_id)
        ON DELETE CASCADE,

    FOREIGN KEY (shelf_id)
        REFERENCES book_shelves(shelf_id)
        ON DELETE SET NULL,

    CHECK (purchase_price IS NULL OR purchase_price >= 0)
);


/* ============================================================
   15. LOANS
   ============================================================ */

CREATE TABLE loans (
    loan_id INT AUTO_INCREMENT PRIMARY KEY,
    copy_id INT NOT NULL,
    member_id INT NOT NULL,
    librarian_id INT,
    issue_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    due_date DATETIME NOT NULL,
    return_date DATETIME,

    loan_status ENUM(
        'ISSUED',
        'RETURNED',
        'OVERDUE',
        'LOST'
    ) DEFAULT 'ISSUED',

    renewal_count INT DEFAULT 0,

    FOREIGN KEY (copy_id)
        REFERENCES book_copies(copy_id)
        ON DELETE RESTRICT,

    FOREIGN KEY (member_id)
        REFERENCES members(member_id)
        ON DELETE RESTRICT,

    FOREIGN KEY (librarian_id)
        REFERENCES librarians(librarian_id)
        ON DELETE SET NULL,

    CHECK (renewal_count >= 0)
);


/* ============================================================
   16. RESERVATIONS
   ============================================================ */

CREATE TABLE reservations (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    member_id INT NOT NULL,
    reservation_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    expiry_date DATETIME,

    reservation_status ENUM(
        'ACTIVE',
        'FULFILLED',
        'CANCELLED',
        'EXPIRED'
    ) DEFAULT 'ACTIVE',

    priority_number INT,

    FOREIGN KEY (book_id)
        REFERENCES books(book_id)
        ON DELETE CASCADE,

    FOREIGN KEY (member_id)
        REFERENCES members(member_id)
        ON DELETE CASCADE,

    CHECK (priority_number IS NULL OR priority_number > 0)
);


/* ============================================================
   17. FINES
   ============================================================ */

CREATE TABLE fines (
    fine_id INT AUTO_INCREMENT PRIMARY KEY,
    loan_id INT NOT NULL UNIQUE,
    member_id INT NOT NULL,

    fine_type ENUM(
        'OVERDUE',
        'LOST_BOOK',
        'DAMAGE',
        'OTHER'
    ) NOT NULL,

    amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    paid_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    fine_status ENUM(
        'UNPAID',
        'PARTIAL',
        'PAID',
        'WAIVED'
    ) DEFAULT 'UNPAID',

    description VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (loan_id)
        REFERENCES loans(loan_id)
        ON DELETE CASCADE,

    FOREIGN KEY (member_id)
        REFERENCES members(member_id)
        ON DELETE CASCADE,

    CHECK (amount >= 0),
    CHECK (paid_amount >= 0),
    CHECK (paid_amount <= amount)
);


/* ============================================================
   18. PAYMENTS
   ============================================================ */

CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    fine_id INT NOT NULL,
    member_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,

    payment_method ENUM(
        'CASH',
        'CARD',
        'UPI',
        'NET_BANKING'
    ) NOT NULL,

    transaction_reference VARCHAR(100) UNIQUE,
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,

    payment_status ENUM(
        'SUCCESS',
        'FAILED',
        'REFUNDED'
    ) DEFAULT 'SUCCESS',

    FOREIGN KEY (fine_id)
        REFERENCES fines(fine_id)
        ON DELETE CASCADE,

    FOREIGN KEY (member_id)
        REFERENCES members(member_id)
        ON DELETE CASCADE,

    CHECK (amount > 0)
);


/* ============================================================
   19. BOOK REVIEWS
   ============================================================ */

CREATE TABLE book_reviews (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    member_id INT NOT NULL,
    rating INT NOT NULL,
    review_text TEXT,
    review_date DATETIME DEFAULT CURRENT_TIMESTAMP,

    review_status ENUM(
        'VISIBLE',
        'HIDDEN',
        'FLAGGED'
    ) DEFAULT 'VISIBLE',

    FOREIGN KEY (book_id)
        REFERENCES books(book_id)
        ON DELETE CASCADE,

    FOREIGN KEY (member_id)
        REFERENCES members(member_id)
        ON DELETE CASCADE,

    UNIQUE (book_id, member_id),

    CHECK (rating BETWEEN 1 AND 5)
);


/* ============================================================
   20. BOOK REQUESTS
   ============================================================ */

CREATE TABLE book_requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    author_name VARCHAR(150),
    isbn VARCHAR(20),
    reason VARCHAR(500),
    request_date DATETIME DEFAULT CURRENT_TIMESTAMP,

    request_status ENUM(
        'PENDING',
        'APPROVED',
        'REJECTED',
        'PURCHASED'
    ) DEFAULT 'PENDING',

    processed_by INT,

    FOREIGN KEY (member_id)
        REFERENCES members(member_id)
        ON DELETE CASCADE,

    FOREIGN KEY (processed_by)
        REFERENCES librarians(librarian_id)
        ON DELETE SET NULL
);


/* ============================================================
   21. DAMAGE REPORTS
   ============================================================ */

CREATE TABLE damage_reports (
    damage_report_id INT AUTO_INCREMENT PRIMARY KEY,
    copy_id INT NOT NULL,
    reported_by INT,

    damage_type ENUM(
        'PAGE_DAMAGE',
        'COVER_DAMAGE',
        'WATER_DAMAGE',
        'MARKING',
        'OTHER'
    ) NOT NULL,

    description TEXT,
    estimated_cost DECIMAL(10,2) DEFAULT 0.00,
    report_date DATETIME DEFAULT CURRENT_TIMESTAMP,

    resolution_status ENUM(
        'PENDING',
        'UNDER_REVIEW',
        'REPAIRED',
        'REPLACED'
    ) DEFAULT 'PENDING',

    FOREIGN KEY (copy_id)
        REFERENCES book_copies(copy_id)
        ON DELETE CASCADE,

    FOREIGN KEY (reported_by)
        REFERENCES users(user_id)
        ON DELETE SET NULL,

    CHECK (estimated_cost >= 0)
);


/* ============================================================
   22. AUDIT LOGS
   ============================================================ */

CREATE TABLE audit_logs (
    audit_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    action_type VARCHAR(100) NOT NULL,
    table_name VARCHAR(100),
    record_id INT,
    action_description TEXT,
    ip_address VARCHAR(45),
    action_time DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE SET NULL
);


/* ============================================================
   23. LOGIN HISTORY
   ============================================================ */

CREATE TABLE login_history (
    login_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    login_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    logout_time DATETIME,
    ip_address VARCHAR(45),

    login_status ENUM(
        'SUCCESS',
        'FAILED',
        'BLOCKED'
    ) NOT NULL,

    device_info VARCHAR(255),

    FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);


/* ============================================================
   24. NOTIFICATIONS
   ============================================================ */

CREATE TABLE notifications (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,

    notification_type ENUM(
        'DUE_REMINDER',
        'OVERDUE',
        'RESERVATION',
        'FINE',
        'SYSTEM',
        'GENERAL'
    ) DEFAULT 'GENERAL',

    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);


/* ============================================================
   25. LIBRARY SETTINGS
   ============================================================ */

CREATE TABLE library_settings (
    setting_id INT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);


/* ============================================================
   26. HOLIDAYS
   ============================================================ */

CREATE TABLE holidays (
    holiday_id INT AUTO_INCREMENT PRIMARY KEY,
    holiday_date DATE NOT NULL UNIQUE,
    holiday_name VARCHAR(150) NOT NULL,
    description VARCHAR(255),
    is_library_closed BOOLEAN DEFAULT TRUE
);


/* ============================================================
   INDEXES
   ============================================================ */

CREATE INDEX idx_books_title
    ON books(title);

CREATE INDEX idx_books_category
    ON books(category_id);

CREATE INDEX idx_books_publisher
    ON books(publisher_id);

CREATE INDEX idx_copies_book
    ON book_copies(book_id);

CREATE INDEX idx_copies_status
    ON book_copies(availability_status);

CREATE INDEX idx_loans_member
    ON loans(member_id);

CREATE INDEX idx_loans_copy
    ON loans(copy_id);

CREATE INDEX idx_loans_status
    ON loans(loan_status);

CREATE INDEX idx_loans_due_date
    ON loans(due_date);

CREATE INDEX idx_reservations_book
    ON reservations(book_id);

CREATE INDEX idx_reservations_member
    ON reservations(member_id);

CREATE INDEX idx_fines_member
    ON fines(member_id);

CREATE INDEX idx_payments_member
    ON payments(member_id);

CREATE INDEX idx_audit_user_time
    ON audit_logs(user_id, action_time);

CREATE INDEX idx_login_user_time
    ON login_history(user_id, login_time);


/* ============================================================
   DEMO DATA
   ============================================================ */


/* ---------- USERS ---------- */

INSERT INTO users
(username, password_hash, full_name, email, phone, role)
VALUES
('admin', 'admin123', 'System Administrator',
 'admin@library.com', '9000000001', 'ADMIN'),

('librarian1', 'lib123', 'Anita Sharma',
 'anita@library.com', '9000000002', 'LIBRARIAN'),

('librarian2', 'lib123', 'Rahul Verma',
 'rahul@library.com', '9000000003', 'LIBRARIAN'),

('harkesh', 'member123', 'Harkesh Kumar',
 'harkesh@library.com', '9000000004', 'MEMBER'),

('member2', 'member123', 'Aarav Singh',
 'aarav@library.com', '9000000005', 'MEMBER'),

('member3', 'member123', 'Priya Sharma',
 'priya@library.com', '9000000006', 'MEMBER'),

('member4', 'member123', 'Rohan Patel',
 'rohan@library.com', '9000000007', 'MEMBER'),

('member5', 'member123', 'Sneha Gupta',
 'sneha@library.com', '9000000008', 'MEMBER'),

('member6', 'member123', 'Vikram Joshi',
 'vikram@library.com', '9000000009', 'MEMBER'),

('member7', 'member123', 'Neha Mehta',
 'neha@library.com', '9000000010', 'MEMBER');


/* ---------- MEMBERSHIP PLANS ---------- */

INSERT INTO membership_plans
(plan_name, duration_months, max_books, loan_period_days,
 fine_per_day, membership_fee)
VALUES
('Basic', 6, 3, 14, 2.00, 300.00),
('Standard', 12, 5, 21, 1.50, 500.00),
('Premium', 12, 10, 30, 1.00, 1000.00);


/* ---------- MEMBERS ---------- */

INSERT INTO members
(user_id, plan_id, membership_number,
 registration_date, expiry_date, occupation, address,
 date_of_birth)
VALUES
(4, 2, 'MEM1001', '2026-01-10', '2027-01-10',
 'Student', 'Bhopal, Madhya Pradesh', '2005-05-12'),

(5, 1, 'MEM1002', '2026-02-15', '2026-08-15',
 'Student', 'Indore, Madhya Pradesh', '2004-08-20'),

(6, 2, 'MEM1003', '2026-03-01', '2027-03-01',
 'Student', 'Delhi, India', '2005-02-11'),

(7, 3, 'MEM1004', '2026-01-20', '2027-01-20',
 'Engineer', 'Pune, Maharashtra', '1999-10-19'),

(8, 2, 'MEM1005', '2026-04-10', '2027-04-10',
 'Student', 'Mumbai, Maharashtra', '2005-07-14'),

(9, 1, 'MEM1006', '2026-05-01', '2026-11-01',
 'Student', 'Jaipur, Rajasthan', '2004-12-03'),

(10, 2, 'MEM1007', '2026-05-10', '2027-05-10',
 'Researcher', 'Chennai, Tamil Nadu', '1998-04-25');


/* ---------- LIBRARIANS ---------- */

INSERT INTO librarians
(user_id, employee_code, department, joining_date, shift)
VALUES
(2, 'LIB001', 'Circulation', '2024-06-01', 'MORNING'),
(3, 'LIB002', 'Technical Services', '2025-01-15', 'FULL_DAY');


/* ---------- MEMBERSHIP PAYMENTS ---------- */

INSERT INTO membership_payments
(member_id, plan_id, amount, payment_method,
 transaction_reference)
VALUES
(1, 2, 500.00, 'UPI', 'MEMTXN001'),
(2, 1, 300.00, 'CARD', 'MEMTXN002'),
(3, 2, 500.00, 'UPI', 'MEMTXN003'),
(4, 3, 1000.00, 'NET_BANKING', 'MEMTXN004'),
(5, 2, 500.00, 'CASH', 'MEMTXN005'),
(6, 1, 300.00, 'UPI', 'MEMTXN006'),
(7, 2, 500.00, 'CARD', 'MEMTXN007');


/* ---------- CATEGORIES ---------- */

INSERT INTO categories
(category_name, description)
VALUES
('Programming', 'Programming and software development'),
('Database', 'Database management and SQL'),
('Cybersecurity', 'Information and cybersecurity'),
('Computer Networks', 'Networking and communication'),
('Operating Systems', 'Operating systems and system software'),
('Artificial Intelligence', 'AI and machine learning'),
('Data Science', 'Data analysis and data science'),
('Web Development', 'Web and application development'),
('Mathematics', 'Mathematics and statistics'),
('Fiction', 'Fiction and general reading');


/* ---------- PUBLISHERS ---------- */

INSERT INTO publishers
(publisher_name, email, phone, city, country)
VALUES
('Pearson', 'contact@pearson.com', '1111111111',
 'London', 'UK'),

('OReilly Media', 'info@oreilly.com', '2222222222',
 'Sebastopol', 'USA'),

('McGraw Hill', 'contact@mcgrawhill.com', '3333333333',
 'New York', 'USA'),

('Wiley', 'info@wiley.com', '4444444444',
 'Hoboken', 'USA'),

('Springer', 'contact@springer.com', '5555555555',
 'Berlin', 'Germany'),

('Packt Publishing', 'info@packtpub.com', '6666666666',
 'Birmingham', 'UK'),

('MIT Press', 'info@mit.edu', '7777777777',
 'Cambridge', 'USA'),

('Oxford University Press', 'info@oup.com', '8888888888',
 'Oxford', 'UK');


/* ---------- AUTHORS ---------- */

INSERT INTO authors
(author_name, biography, nationality)
VALUES
('Herbert Schildt', 'Programming author and educator.', 'American'),
('Robert C. Martin', 'Software engineering author.', 'American'),
('Joshua Bloch', 'Java developer and author.', 'American'),
('Martin Fowler', 'Software development author.', 'British'),
('Thomas H. Cormen', 'Computer science researcher and author.', 'American'),
('Andrew S. Tanenbaum', 'Computer science professor and author.', 'American'),
('Kurose', 'Networking researcher and author.', 'American'),
('Stuart Russell', 'Artificial intelligence researcher.', 'British'),
('Eric Matthes', 'Python programming author.', 'American'),
('Abraham Silberschatz', 'Database and operating systems author.', 'American'),
('Ramez Elmasri', 'Database systems author.', 'American'),
('Kevin Mitnick', 'Cybersecurity author.', 'American');


/* ---------- SHELVES ---------- */

INSERT INTO book_shelves
(shelf_code, floor_number, section_name, capacity)
VALUES
('S-A01', 1, 'Programming', 100),
('S-A02', 1, 'Programming', 100),
('S-B01', 1, 'Database', 80),
('S-B02', 1, 'Cybersecurity', 80),
('S-C01', 2, 'Networking', 90),
('S-C02', 2, 'Operating Systems', 90),
('S-D01', 2, 'Artificial Intelligence', 100),
('S-D02', 2, 'Data Science', 100),
('S-E01', 3, 'Web Development', 80),
('S-F01', 3, 'Mathematics', 80),
('S-G01', 3, 'Fiction', 150);


/* ============================================================
   BOOKS
   ============================================================ */

INSERT INTO books
(isbn, title, subtitle, category_id, publisher_id,
 publication_year, edition, language, pages,
 description, price)
VALUES

('9780134685991',
 'Effective Java',
 'Best Practices for the Java Platform',
 1, 1, 2018, '3rd Edition', 'English', 416,
 'Best practices for modern Java programming.', 850.00),

('9780132350884',
 'Clean Code',
 'A Handbook of Agile Software Craftsmanship',
 1, 1, 2008, '1st Edition', 'English', 464,
 'Principles and practices for writing clean software.', 750.00),

('9780321356680',
 'Effective Java Programming',
 'Object-Oriented Programming Concepts',
 1, 1, 2020, '2nd Edition', 'English', 500,
 'Advanced Java programming concepts.', 900.00),

('9780262046305',
 'Introduction to Algorithms',
 'Algorithms and Data Structures',
 1, 7, 2022, '4th Edition', 'English', 1312,
 'Comprehensive algorithms reference.', 1200.00),

('9780133594140',
 'Database System Concepts',
 'Database Management',
 2, 3, 2019, '7th Edition', 'English', 1376,
 'Database concepts, SQL and transaction management.', 1100.00),

('9780321751041',
 'Fundamentals of Database Systems',
 'Database Design and Implementation',
 2, 3, 2016, '7th Edition', 'English', 1272,
 'Complete database systems textbook.', 1050.00),

('9781292222449',
 'Computer Networks',
 'A Top-Down Approach',
 4, 1, 2021, '8th Edition', 'English', 864,
 'Computer networking fundamentals.', 950.00),

('9780132126953',
 'Modern Operating Systems',
 'Operating System Concepts',
 5, 2, 2015, '4th Edition', 'English', 1136,
 'Operating systems concepts and implementation.', 1000.00),

('9780134610993',
 'Artificial Intelligence',
 'A Modern Approach',
 6, 7, 2020, '4th Edition', 'English', 1152,
 'Introduction to artificial intelligence.', 1300.00),

('9781617294433',
 'Python Crash Course',
 'A Hands-On Introduction to Programming',
 1, 6, 2019, '2nd Edition', 'English', 544,
 'Practical Python programming guide.', 700.00),

('9781491957660',
 'Hands-On Machine Learning',
 'Machine Learning with Scikit-Learn',
 6, 2, 2022, '3rd Edition', 'English', 850,
 'Practical machine learning techniques.', 1150.00),

('9781119456339',
 'Data Science from Scratch',
 'First Principles with Python',
 7, 4, 2019, '2nd Edition', 'English', 400,
 'Introduction to data science using Python.', 800.00),

('9780131103627',
 'The C Programming Language',
 'Programming Fundamentals',
 1, 1, 1988, '2nd Edition', 'English', 272,
 'Classic C programming reference.', 600.00),

('9781492078005',
 'Flask Web Development',
 'Developing Web Applications with Python',
 8, 2, 2023, '3rd Edition', 'English', 350,
 'Web development using Flask.', 900.00),

/* FIXED DUPLICATE ISBN */
('9780132350885',
 'Software Engineering Principles',
 'Professional Software Development',
 1, 1, 2010, '1st Edition', 'English', 500,
 'Software engineering principles.', 780.00),

('9780073523323',
 'Operating System Concepts',
 'Core OS Concepts',
 5, 3, 2018, '10th Edition', 'English', 1000,
 'Fundamentals of operating systems.', 950.00),

('9780262035613',
 'Deep Learning',
 'Foundations of Deep Learning',
 6, 7, 2016, '1st Edition', 'English', 800,
 'Deep learning theory and applications.', 1250.00),

('9780134431604',
 'Computer Security',
 'Principles and Practice',
 3, 1, 2018, '4th Edition', 'English', 900,
 'Computer security fundamentals.', 1050.00),

('9781119714674',
 'Cybersecurity Essentials',
 'Security Fundamentals',
 3, 4, 2021, '2nd Edition', 'English', 500,
 'Introduction to cybersecurity.', 850.00),

('9781593279929',
 'Linux Basics',
 'Command Line and System Administration',
 5, 6, 2020, '1st Edition', 'English', 450,
 'Linux fundamentals for beginners.', 650.00);


/* ============================================================
   BOOK AUTHORS
   ============================================================ */

INSERT INTO book_authors
(book_id, author_id)
VALUES
(1, 3),
(2, 2),
(3, 1),
(4, 5),
(5, 10),
(6, 11),
(7, 7),
(8, 6),
(9, 8),
(10, 9),
(11, 9),
(12, 9),
(13, 1),
(14, 9),
(15, 4),
(16, 6),
(17, 8),
(18, 12),
(19, 12),
(20, 6);


/* ============================================================
   TAGS
   ============================================================ */

INSERT INTO tags (tag_name)
VALUES
('Beginner'),
('Advanced'),
('Java'),
('Python'),
('SQL'),
('Security'),
('Algorithms'),
('AI'),
('Networking'),
('Linux'),
('Web'),
('Database'),
('Programming'),
('Machine Learning'),
('Academic');


/* ============================================================
   BOOK TAGS
   ============================================================ */

INSERT INTO book_tags
(book_id, tag_id)
VALUES
(1, 3),
(1, 2),
(2, 13),
(2, 2),
(3, 3),
(4, 7),
(4, 15),
(5, 5),
(5, 12),
(6, 5),
(6, 12),
(7, 9),
(8, 10),
(9, 8),
(10, 4),
(10, 1),
(11, 14),
(12, 4),
(13, 3),
(14, 11),
(16, 10),
(17, 8),
(18, 6),
(19, 6),
(20, 10);


/* ============================================================
   BOOK COPIES
   ============================================================ */

INSERT INTO book_copies
(book_id, shelf_id, accession_number, barcode,
 purchase_date, purchase_price)
VALUES

(1, 1, 'ACC0001', 'BC000001', '2025-01-10', 700),
(1, 1, 'ACC0002', 'BC000002', '2025-01-10', 700),
(1, 1, 'ACC0003', 'BC000003', '2025-02-15', 700),

(2, 1, 'ACC0004', 'BC000004', '2025-02-10', 600),
(2, 1, 'ACC0005', 'BC000005', '2025-02-10', 600),
(2, 1, 'ACC0006', 'BC000006', '2025-03-12', 600),

(3, 2, 'ACC0007', 'BC000007', '2025-03-10', 750),
(3, 2, 'ACC0008', 'BC000008', '2025-03-10', 750),

(4, 2, 'ACC0009', 'BC000009', '2025-04-01', 1000),
(4, 2, 'ACC0010', 'BC000010', '2025-04-01', 1000),

(5, 3, 'ACC0011', 'BC000011', '2025-04-15', 900),
(5, 3, 'ACC0012', 'BC000012', '2025-04-15', 900),
(5, 3, 'ACC0013', 'BC000013', '2025-05-10', 900),

(6, 3, 'ACC0014', 'BC000014', '2025-05-10', 850),
(6, 3, 'ACC0015', 'BC000015', '2025-05-10', 850),

(7, 5, 'ACC0016', 'BC000016', '2025-05-20', 800),
(7, 5, 'ACC0017', 'BC000017', '2025-05-20', 800),

(8, 6, 'ACC0018', 'BC000018', '2025-06-01', 850),
(8, 6, 'ACC0019', 'BC000019', '2025-06-01', 850),

(9, 7, 'ACC0020', 'BC000020', '2025-06-10', 1100),
(9, 7, 'ACC0021', 'BC000021', '2025-06-10', 1100),

(10, 1, 'ACC0022', 'BC000022', '2025-06-20', 550),
(10, 1, 'ACC0023', 'BC000023', '2025-06-20', 550),

(11, 7, 'ACC0024', 'BC000024', '2025-07-01', 950),
(11, 7, 'ACC0025', 'BC000025', '2025-07-01', 950),

(12, 8, 'ACC0026', 'BC000026', '2025-07-05', 650),
(12, 8, 'ACC0027', 'BC000027', '2025-07-05', 650),

(13, 1, 'ACC0028', 'BC000028', '2025-07-10', 450),

(14, 9, 'ACC0029', 'BC000029', '2025-07-15', 700),

(15, 1, 'ACC0030', 'BC000030', '2025-07-20', 600),

(16, 6, 'ACC0031', 'BC000031', '2025-07-25', 800),

(17, 7, 'ACC0032', 'BC000032', '2025-07-30', 1000),

(18, 4, 'ACC0033', 'BC000033', '2025-08-01', 850),

(19, 4, 'ACC0034', 'BC000034', '2025-08-01', 700),

(20, 6, 'ACC0035', 'BC000035', '2025-08-05', 550);


/* ============================================================
   TRIGGERS
   ============================================================ */

DELIMITER $$


/* ---------- Loan creation -> copy becomes ISSUED ---------- */

CREATE TRIGGER trg_loan_after_insert
AFTER INSERT ON loans
FOR EACH ROW
BEGIN
    UPDATE book_copies
    SET availability_status = 'ISSUED'
    WHERE copy_id = NEW.copy_id;
END$$


/* ---------- Loan return -> status becomes RETURNED ---------- */
/* FIXED: uses BEFORE UPDATE instead of updating loans from
   its own AFTER UPDATE trigger. */

CREATE TRIGGER trg_loan_before_update
BEFORE UPDATE ON loans
FOR EACH ROW
BEGIN
    IF NEW.return_date IS NOT NULL
       AND OLD.return_date IS NULL THEN

        SET NEW.loan_status = 'RETURNED';

    END IF;
END$$


/* ---------- Loan return -> copy becomes AVAILABLE ---------- */

CREATE TRIGGER trg_loan_after_update
AFTER UPDATE ON loans
FOR EACH ROW
BEGIN
    IF NEW.return_date IS NOT NULL
       AND OLD.return_date IS NULL THEN

        UPDATE book_copies
        SET availability_status = 'AVAILABLE'
        WHERE copy_id = NEW.copy_id;

    END IF;
END$$


/* ---------- Fine payment status ---------- */

CREATE TRIGGER trg_payment_after_insert
AFTER INSERT ON payments
FOR EACH ROW
BEGIN

    UPDATE fines
    SET paid_amount = paid_amount + NEW.amount
    WHERE fine_id = NEW.fine_id;

    UPDATE fines
    SET fine_status =
        CASE
            WHEN paid_amount >= amount THEN 'PAID'
            WHEN paid_amount > 0 THEN 'PARTIAL'
            ELSE 'UNPAID'
        END
    WHERE fine_id = NEW.fine_id;

END$$


/* ---------- Review validation ---------- */

CREATE TRIGGER trg_review_before_insert
BEFORE INSERT ON book_reviews
FOR EACH ROW
BEGIN

    IF NEW.rating < 1 OR NEW.rating > 5 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
        'Rating must be between 1 and 5';

    END IF;

END$$

DELIMITER ;


/* ============================================================
   LOANS
   ============================================================ */

INSERT INTO loans
(copy_id, member_id, librarian_id,
 issue_date, due_date, loan_status)
VALUES

(1, 1, 1,
 '2026-09-20 10:00:00',
 '2026-10-11 23:59:59',
 'ISSUED'),

(4, 2, 1,
 '2026-09-15 11:00:00',
 '2026-09-29 23:59:59',
 'OVERDUE'),

(7, 3, 2,
 '2026-09-22 12:00:00',
 '2026-10-13 23:59:59',
 'ISSUED'),

(11, 4, 1,
 '2026-09-25 14:00:00',
 '2026-10-25 23:59:59',
 'ISSUED'),

(16, 5, 2,
 '2026-08-01 10:00:00',
 '2026-08-22 23:59:59',
 'RETURNED'),

(20, 6, 1,
 '2026-07-10 09:00:00',
 '2026-07-31 23:59:59',
 'RETURNED'),

(22, 1, 1,
 '2026-06-01 10:00:00',
 '2026-06-22 23:59:59',
 'RETURNED'),

(24, 7, 2,
 '2026-08-15 15:00:00',
 '2026-09-05 23:59:59',
 'OVERDUE');


/* ---------- Returned dates ---------- */

UPDATE loans
SET return_date = '2026-08-20 16:00:00'
WHERE loan_id = 5;

UPDATE loans
SET return_date = '2026-07-25 12:00:00'
WHERE loan_id = 6;

UPDATE loans
SET return_date = '2026-06-20 14:00:00'
WHERE loan_id = 7;


/* ============================================================
   RESERVATIONS
   ============================================================ */

INSERT INTO reservations
(book_id, member_id, reservation_date, expiry_date,
 reservation_status, priority_number)
VALUES

(1, 2,
 '2026-09-20 10:00:00',
 '2026-10-05 23:59:59',
 'ACTIVE', 1),

(5, 3,
 '2026-09-21 11:00:00',
 '2026-10-06 23:59:59',
 'ACTIVE', 1),

(9, 4,
 '2026-09-22 12:00:00',
 '2026-10-07 23:59:59',
 'FULFILLED', 1),

(10, 5,
 '2026-09-10 10:00:00',
 '2026-09-20 23:59:59',
 'CANCELLED', 1),

(18, 6,
 '2026-09-15 09:00:00',
 '2026-10-01 23:59:59',
 'ACTIVE', 1);


/* ============================================================
   FINES
   ============================================================ */

INSERT INTO fines
(loan_id, member_id, fine_type, amount, paid_amount,
 fine_status, description)
VALUES

(2, 2, 'OVERDUE', 40.00, 0.00,
 'UNPAID', 'Overdue library book'),

(8, 7, 'OVERDUE', 30.00, 10.00,
 'PARTIAL', 'Overdue library book');


/* ============================================================
   PAYMENTS
   ============================================================ */

INSERT INTO payments
(fine_id, member_id, amount,
 payment_method, transaction_reference)
VALUES

(2, 7, 10.00,
 'UPI', 'FINTXN001');


/* ============================================================
   BOOK REVIEWS
   ============================================================ */

INSERT INTO book_reviews
(book_id, member_id, rating, review_text)
VALUES

(1, 1, 5,
 'Excellent book for Java programming.'),

(2, 2, 5,
 'Very useful for software development.'),

(4, 3, 5,
 'Excellent algorithms reference.'),

(5, 4, 4,
 'Good database textbook.'),

(7, 5, 4,
 'Very useful networking concepts.'),

(9, 6, 5,
 'Great introduction to AI.'),

(10, 7, 5,
 'Very practical Python book.'),

(18, 1, 4,
 'Useful cybersecurity concepts.');


/* ============================================================
   BOOK REQUESTS
   ============================================================ */

INSERT INTO book_requests
(member_id, title, author_name, isbn, reason, processed_by)
VALUES

(1,
 'Learning SQL',
 'Alan Beaulieu',
 '9781492057611',
 'Needed for database course',
 1),

(2,
 'Java Concurrency in Practice',
 'Brian Goetz',
 '9780321349606',
 'Needed for advanced Java',
 2),

(3,
 'Computer Security Basics',
 'Charles Brooks',
 NULL,
 'Cybersecurity learning',
 NULL),

(4,
 'Design Patterns',
 'Erich Gamma',
 '9780201633610',
 'Software engineering project',
 1);


/* ============================================================
   DAMAGE REPORTS
   ============================================================ */

INSERT INTO damage_reports
(copy_id, reported_by, damage_type,
 description, estimated_cost)
VALUES

(5, 2, 'COVER_DAMAGE',
 'Book cover is partially damaged.',
 150.00),

(14, 3, 'PAGE_DAMAGE',
 'Several pages are torn.',
 200.00),

(18, 2, 'WATER_DAMAGE',
 'Water damage on lower pages.',
 300.00);


/* ============================================================
   AUDIT LOGS
   ============================================================ */

INSERT INTO audit_logs
(user_id, action_type, table_name, record_id,
 action_description, ip_address)
VALUES

(1, 'LOGIN', 'users', 1,
 'Administrator logged into system',
 '127.0.0.1'),

(2, 'ISSUE_BOOK', 'loans', 1,
 'Book issued to member',
 '127.0.0.1'),

(2, 'ISSUE_BOOK', 'loans', 2,
 'Book issued to member',
 '127.0.0.1'),

(3, 'ADD_BOOK', 'books', 15,
 'New book added',
 '127.0.0.1'),

(1, 'UPDATE_MEMBER', 'members', 1,
 'Member information updated',
 '127.0.0.1');


/* ============================================================
   LOGIN HISTORY
   ============================================================ */

INSERT INTO login_history
(user_id, login_time, logout_time, ip_address,
 login_status, device_info)
VALUES

(1,
 '2026-09-29 09:00:00',
 '2026-09-29 12:00:00',
 '127.0.0.1',
 'SUCCESS',
 'Windows Desktop'),

(2,
 '2026-09-29 09:15:00',
 '2026-09-29 17:00:00',
 '127.0.0.1',
 'SUCCESS',
 'Windows Desktop'),

(4,
 '2026-09-29 10:30:00',
 NULL,
 '127.0.0.1',
 'SUCCESS',
 'Windows Laptop'),

(5,
 '2026-09-29 11:00:00',
 NULL,
 '127.0.0.1',
 'SUCCESS',
 'Android Mobile'),

(6,
 '2026-09-29 12:00:00',
 NULL,
 '127.0.0.1',
 'FAILED',
 'Windows Desktop');


/* ============================================================
   NOTIFICATIONS
   ============================================================ */

INSERT INTO notifications
(user_id, title, message, notification_type)
VALUES

(4,
 'Due Date Reminder',
 'Your borrowed book is due soon.',
 'DUE_REMINDER'),

(5,
 'Overdue Book',
 'Please return your overdue book.',
 'OVERDUE'),

(7,
 'Fine Reminder',
 'You have an outstanding library fine.',
 'FINE'),

(6,
 'Reservation Available',
 'Your reserved book is now available.',
 'RESERVATION'),

(4,
 'Welcome',
 'Welcome to the Advanced Library Management System.',
 'GENERAL');


/* ============================================================
   LIBRARY SETTINGS
   ============================================================ */

INSERT INTO library_settings
(setting_key, setting_value, description)
VALUES

('library_name',
 'Advanced Central Library',
 'Name of the library'),

('max_renewals',
 '2',
 'Maximum number of renewals'),

('default_fine_per_day',
 '2.00',
 'Default overdue fine per day'),

('reservation_days',
 '7',
 'Number of days reservation remains active'),

('max_login_attempts',
 '5',
 'Maximum login attempts'),

('email_notifications',
 'true',
 'Enable email notifications'),

('sms_notifications',
 'false',
 'Enable SMS notifications');


/* ============================================================
   HOLIDAYS
   ============================================================ */

INSERT INTO holidays
(holiday_date, holiday_name, description)
VALUES

('2026-10-02',
 'Gandhi Jayanti',
 'National holiday'),

('2026-10-20',
 'Diwali',
 'Festival holiday'),

('2026-11-14',
 'Children Day',
 'Library holiday'),

('2026-12-25',
 'Christmas',
 'Christmas holiday');


/* ============================================================
   VIEWS
   ============================================================ */


/* ---------- Complete Book Catalog ---------- */

CREATE VIEW v_book_catalog AS
SELECT
    b.book_id,
    b.isbn,
    b.title,
    c.category_name,
    p.publisher_name,
    b.publication_year,
    b.edition,
    b.language,
    b.pages,

    COUNT(bc.copy_id) AS total_copies,

    SUM(
        CASE
            WHEN bc.availability_status = 'AVAILABLE'
            THEN 1
            ELSE 0
        END
    ) AS available_copies

FROM books b

JOIN categories c
    ON b.category_id = c.category_id

LEFT JOIN publishers p
    ON b.publisher_id = p.publisher_id

LEFT JOIN book_copies bc
    ON b.book_id = bc.book_id

GROUP BY
    b.book_id,
    b.isbn,
    b.title,
    c.category_name,
    p.publisher_name,
    b.publication_year,
    b.edition,
    b.language,
    b.pages;


/* ---------- Current Loans ---------- */

CREATE VIEW v_current_loans AS
SELECT
    l.loan_id,
    m.membership_number,
    u.full_name AS member_name,
    b.title,
    bc.accession_number,
    l.issue_date,
    l.due_date,
    l.loan_status

FROM loans l

JOIN members m
    ON l.member_id = m.member_id

JOIN users u
    ON m.user_id = u.user_id

JOIN book_copies bc
    ON l.copy_id = bc.copy_id

JOIN books b
    ON bc.book_id = b.book_id

WHERE l.return_date IS NULL;


/* ---------- Fine Summary ---------- */

CREATE VIEW v_fine_summary AS
SELECT
    m.member_id,
    m.membership_number,
    u.full_name AS member_name,

    COALESCE(SUM(f.amount), 0) AS total_fine,

    COALESCE(SUM(f.paid_amount), 0) AS total_paid,

    COALESCE(
        SUM(f.amount - f.paid_amount),
        0
    ) AS outstanding_fine

FROM members m

JOIN users u
    ON m.user_id = u.user_id

LEFT JOIN fines f
    ON m.member_id = f.member_id

GROUP BY
    m.member_id,
    m.membership_number,
    u.full_name;


/* ---------- Popular Books ---------- */

CREATE VIEW v_popular_books AS
SELECT
    b.book_id,
    b.title,
    COUNT(l.loan_id) AS times_borrowed

FROM books b

LEFT JOIN book_copies bc
    ON b.book_id = bc.book_id

LEFT JOIN loans l
    ON bc.copy_id = l.copy_id

GROUP BY
    b.book_id,
    b.title;


/* ---------- Member Activity ---------- */

CREATE VIEW v_member_activity AS
SELECT
    m.member_id,
    m.membership_number,
    u.full_name,

    COUNT(DISTINCT l.loan_id) AS total_loans,

    COUNT(DISTINCT r.reservation_id)
        AS total_reservations,

    COUNT(DISTINCT br.review_id)
        AS total_reviews

FROM members m

JOIN users u
    ON m.user_id = u.user_id

LEFT JOIN loans l
    ON m.member_id = l.member_id

LEFT JOIN reservations r
    ON m.member_id = r.member_id

LEFT JOIN book_reviews br
    ON m.member_id = br.member_id

GROUP BY
    m.member_id,
    m.membership_number,
    u.full_name;


/* ---------- Inventory Summary ---------- */

CREATE VIEW v_inventory_summary AS
SELECT
    c.category_name,

    COUNT(DISTINCT b.book_id)
        AS different_books,

    COUNT(bc.copy_id)
        AS total_copies,

    SUM(
        CASE
            WHEN bc.availability_status = 'AVAILABLE'
            THEN 1
            ELSE 0
        END
    ) AS available_copies,

    SUM(
        CASE
            WHEN bc.availability_status = 'ISSUED'
            THEN 1
            ELSE 0
        END
    ) AS issued_copies

FROM categories c

LEFT JOIN books b
    ON c.category_id = b.category_id

LEFT JOIN book_copies bc
    ON b.book_id = bc.book_id

GROUP BY
    c.category_id,
    c.category_name;


/* ---------- Book Ratings ---------- */

CREATE VIEW v_book_ratings AS
SELECT
    b.book_id,
    b.title,
    COUNT(br.review_id) AS review_count,
    ROUND(AVG(br.rating), 2) AS average_rating

FROM books b

LEFT JOIN book_reviews br
    ON b.book_id = br.book_id

GROUP BY
    b.book_id,
    b.title;


/* ============================================================
   STORED PROCEDURES
   ============================================================ */

DELIMITER $$


/* ---------- Search Books ---------- */

CREATE PROCEDURE sp_search_books(
    IN search_text VARCHAR(255)
)
BEGIN

    SELECT
        b.book_id,
        b.title,
        b.isbn,
        c.category_name,
        p.publisher_name

    FROM books b

    JOIN categories c
        ON b.category_id = c.category_id

    LEFT JOIN publishers p
        ON b.publisher_id = p.publisher_id

    WHERE
        b.title LIKE CONCAT('%', search_text, '%')

        OR b.isbn LIKE CONCAT('%', search_text, '%')

        OR c.category_name
            LIKE CONCAT('%', search_text, '%')

        OR p.publisher_name
            LIKE CONCAT('%', search_text, '%');

END$$


/* ---------- Member Loan History ---------- */

CREATE PROCEDURE sp_member_loan_history(
    IN input_member_id INT
)
BEGIN

    SELECT
        l.loan_id,
        b.title,
        bc.accession_number,
        l.issue_date,
        l.due_date,
        l.return_date,
        l.loan_status

    FROM loans l

    JOIN book_copies bc
        ON l.copy_id = bc.copy_id

    JOIN books b
        ON bc.book_id = b.book_id

    WHERE l.member_id = input_member_id

    ORDER BY l.issue_date DESC;

END$$


/* ---------- Outstanding Fine ---------- */

CREATE PROCEDURE sp_member_outstanding_fine(
    IN input_member_id INT
)
BEGIN

    SELECT
        m.membership_number,
        u.full_name,

        COALESCE(
            SUM(f.amount - f.paid_amount),
            0
        ) AS outstanding_amount

    FROM members m

    JOIN users u
        ON m.user_id = u.user_id

    LEFT JOIN fines f
        ON m.member_id = f.member_id

    WHERE m.member_id = input_member_id

    GROUP BY
        m.membership_number,
        u.full_name;

END$$


/* ---------- Inventory Report ---------- */

CREATE PROCEDURE sp_inventory_report()
BEGIN

    SELECT
        c.category_name,

        COUNT(DISTINCT b.book_id)
            AS books,

        COUNT(bc.copy_id)
            AS copies,

        SUM(
            CASE
                WHEN bc.availability_status = 'AVAILABLE'
                THEN 1
                ELSE 0
            END
        ) AS available,

        SUM(
            CASE
                WHEN bc.availability_status = 'ISSUED'
                THEN 1
                ELSE 0
            END
        ) AS issued,

        SUM(
            CASE
                WHEN bc.condition_status = 'DAMAGED'
                THEN 1
                ELSE 0
            END
        ) AS damaged

    FROM categories c

    LEFT JOIN books b
        ON c.category_id = b.category_id

    LEFT JOIN book_copies bc
        ON b.book_id = bc.book_id

    GROUP BY
        c.category_id,
        c.category_name

    ORDER BY
        c.category_name;

END$$

DELIMITER ;


/* ============================================================
   FINAL VERIFICATION
   ============================================================ */

SELECT 'DATABASE CREATED SUCCESSFULLY' AS message;

SELECT COUNT(*) AS total_users
FROM users;

SELECT COUNT(*) AS total_members
FROM members;

SELECT COUNT(*) AS total_books
FROM books;

SELECT COUNT(*) AS total_book_copies
FROM book_copies;

SELECT COUNT(*) AS total_loans
FROM loans;

SELECT COUNT(*) AS total_reservations
FROM reservations;

SELECT COUNT(*) AS total_fines
FROM fines;

SELECT COUNT(*) AS total_reviews
FROM book_reviews;

SELECT * FROM v_inventory_summary;

SELECT * FROM v_book_catalog
ORDER BY title;

SELECT * FROM v_current_loans;

SELECT * FROM v_fine_summary;