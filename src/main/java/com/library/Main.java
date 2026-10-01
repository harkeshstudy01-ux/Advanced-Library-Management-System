package com.library;

import com.library.controller.BookController;
import com.library.controller.FineController;
import com.library.controller.LoginController;
import com.library.controller.LoanController;
import com.library.controller.MemberController;
import com.library.controller.ReservationController;
import com.library.model.Book;
import com.library.model.Fine;
import com.library.model.Loan;
import com.library.model.Member;
import com.library.model.Reservation;
import com.library.model.User;
import com.library.util.DatabaseConnection;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * Main command-line application for the Advanced Library
 * Management System.
 *
 * Provides:
 * - Database configuration
 * - Authentication
 * - Role-based menus
 * - Book search
 * - Loan management
 * - Reservation management
 * - Fine management
 * - Member ownership/security validation
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final LoginController loginController =
            new LoginController();

    private static final BookController bookController =
            new BookController();

    private static final LoanController loanController =
            new LoanController();

    private static final ReservationController reservationController =
            new ReservationController();

    private static final FineController fineController =
            new FineController();

    private static final MemberController memberController =
            new MemberController();

    public static void main(String[] args) {

        printHeader();

        // ---------------------------------------------------------
        // DATABASE CONFIGURATION
        // ---------------------------------------------------------

        try {

            DatabaseConnection.configureFromConsole();

            if (!DatabaseConnection.testConnection()) {

                System.out.println();
                System.out.println(
                        "Application cannot continue because the "
                                + "database connection failed."
                );

                return;
            }

        } catch (Exception e) {

            System.out.println();
            System.out.println(
                    "Database configuration failed: "
                            + e.getMessage()
            );

            return;
        }

        // ---------------------------------------------------------
        // MAIN APPLICATION LOOP
        // ---------------------------------------------------------

        boolean running = true;

        while (running) {

            printMainMenu();

            int choice = readInt("Enter choice: ");

            switch (choice) {

                case 1 -> login();

                case 2 -> {
                    running = false;

                    System.out.println();
                    System.out.println(
                            "Thank you for using the Library "
                                    + "Management System."
                    );
                }

                default -> System.out.println(
                        "Invalid choice."
                );
            }
        }

        scanner.close();
    }

    // =============================================================
    // HEADER
    // =============================================================

    private static void printHeader() {

        System.out.println(
                "=============================================="
        );

        System.out.println(
                "       ADVANCED LIBRARY MANAGEMENT SYSTEM"
        );

        System.out.println(
                "=============================================="
        );

        System.out.println(
                "          Java 17 + Maven + MySQL"
        );

        System.out.println(
                "=============================================="
        );
    }

    // =============================================================
    // MAIN MENU
    // =============================================================

    private static void printMainMenu() {

        System.out.println();
        System.out.println("========== MAIN MENU ==========");
        System.out.println("1. Login");
        System.out.println("2. Exit");
    }

    // =============================================================
    // LOGIN
    // =============================================================

    private static void login() {

        System.out.println();
        System.out.println("========== LOGIN ==========");

        String username = readString("Username: ");
        String password = readString("Password: ");

        try {

            User user =
                    loginController.login(
                            username,
                            password
                    );

            System.out.println();
            System.out.println("Login successful.");
            System.out.println(
                    "Welcome, " + user.getFullName()
            );
            System.out.println(
                    "Role: " + user.getRole()
            );

            openRoleMenu(user);

        } catch (Exception e) {

            System.out.println();
            System.out.println(
                    "Login failed: " + e.getMessage()
            );
        }
    }

    // =============================================================
    // ROLE ROUTING
    // =============================================================

    private static void openRoleMenu(User user) {

        String role = user.getRole();

        if ("MEMBER".equalsIgnoreCase(role)) {

            memberMenu(user);

        } else if ("LIBRARIAN".equalsIgnoreCase(role)) {

            librarianMenu(user);

        } else if ("ADMIN".equalsIgnoreCase(role)) {

            adminMenu(user);

        } else {

            System.out.println(
                    "Unknown user role."
            );
        }
    }

    // =============================================================
    // MEMBER MENU
    // =============================================================

    private static void memberMenu(User user) {

        boolean loggedIn = true;

        /*
         * Resolve the logged-in user to the corresponding member.
         *
         * We do this once when the member menu starts.
         *
         * This prevents the member from manually entering another
         * member's ID for "My" operations.
         */
        Member currentMember =
                memberController.getMemberForUser(
                        user.getUserId()
                );

        int memberId = currentMember.getMemberId();

        while (loggedIn) {

            System.out.println();
            System.out.println(
                    "========== MEMBER MENU =========="
            );

            System.out.println("1. Search Books");
            System.out.println("2. View All Books");
            System.out.println("3. Check Book Copy Availability");
            System.out.println("4. View My Loans");
            System.out.println("5. View My Active Loans");
            System.out.println("6. Renew Loan");
            System.out.println("7. Return Book");
            System.out.println("8. Reserve Book");
            System.out.println("9. View My Reservations");
            System.out.println("10. Cancel Reservation");
            System.out.println("11. View My Fines");
            System.out.println("12. Pay Fine");
            System.out.println("13. Logout");

            int choice = readInt("Enter choice: ");

            try {

                switch (choice) {

                    case 1 -> searchBooks();

                    case 2 -> viewAllBooks();

                    case 3 -> checkCopyAvailability();

                    case 4 -> viewMyLoans(memberId);

                    case 5 -> viewActiveLoans(memberId);

                    case 6 -> renewMemberLoan(memberId);

                    case 7 -> returnMemberBook(memberId);

                    case 8 -> reserveBook(memberId);

                    case 9 -> viewMyReservations(memberId);

                    case 10 -> cancelMemberReservation(memberId);

                    case 11 -> viewMyFines(memberId);

                    case 12 -> payMemberFine(memberId);

                    case 13 -> {
                        loggedIn = false;

                        System.out.println(
                                "Logged out successfully."
                        );
                    }

                    default -> System.out.println(
                            "Invalid choice."
                    );
                }

            } catch (Exception e) {

                System.out.println(
                        "Operation failed: "
                                + e.getMessage()
                );
            }
        }
    }

    // =============================================================
    // LIBRARIAN MENU
    // =============================================================

    private static void librarianMenu(User user) {

        boolean loggedIn = true;

        while (loggedIn) {

            System.out.println();
            System.out.println(
                    "========== LIBRARIAN MENU =========="
            );

            System.out.println("1. Search Books");
            System.out.println("2. View All Books");
            System.out.println("3. Check Book Copy Availability");
            System.out.println("4. Issue Book");
            System.out.println("5. Return Book");
            System.out.println("6. Renew Loan");
            System.out.println("7. View Member Loans");
            System.out.println("8. View Active Member Loans");
            System.out.println("9. View Overdue Loans");
            System.out.println("10. View Unpaid Fines");
            System.out.println("11. View Book Reservations");
            System.out.println("12. Logout");

            int choice = readInt("Enter choice: ");

            try {

                switch (choice) {

                    case 1 -> searchBooks();

                    case 2 -> viewAllBooks();

                    case 3 -> checkCopyAvailability();

                    case 4 -> issueBook();

                    case 5 -> returnBook();

                    case 6 -> renewLoan();

                    case 7 -> viewMemberLoans();

                    case 8 -> viewActiveMemberLoans();

                    case 9 -> viewOverdueLoans();

                    case 10 -> viewUnpaidFines();

                    case 11 -> viewBookReservations();

                    case 12 -> {
                        loggedIn = false;

                        System.out.println(
                                "Logged out successfully."
                        );
                    }

                    default -> System.out.println(
                            "Invalid choice."
                    );
                }

            } catch (Exception e) {

                System.out.println(
                        "Operation failed: "
                                + e.getMessage()
                );
            }
        }
    }

    // =============================================================
    // ADMIN MENU
    // =============================================================

    private static void adminMenu(User user) {

        boolean loggedIn = true;

        while (loggedIn) {

            System.out.println();
            System.out.println(
                    "========== ADMIN MENU =========="
            );

            System.out.println("1. Search Books");
            System.out.println("2. View All Books");
            System.out.println("3. Check Book Copy Availability");
            System.out.println("4. View Overdue Loans");
            System.out.println("5. View Unpaid Fines");
            System.out.println("6. View Book Reservations");
            System.out.println("7. Logout");

            int choice = readInt("Enter choice: ");

            try {

                switch (choice) {

                    case 1 -> searchBooks();

                    case 2 -> viewAllBooks();

                    case 3 -> checkCopyAvailability();

                    case 4 -> viewOverdueLoans();

                    case 5 -> viewUnpaidFines();

                    case 6 -> viewBookReservations();

                    case 7 -> {
                        loggedIn = false;

                        System.out.println(
                                "Logged out successfully."
                        );
                    }

                    default -> System.out.println(
                            "Invalid choice."
                    );
                }

            } catch (Exception e) {

                System.out.println(
                        "Operation failed: "
                                + e.getMessage()
                );
            }
        }
    }

    // =============================================================
    // BOOK OPERATIONS
    // =============================================================

    private static void searchBooks() {

        String keyword =
                readString("Enter title or ISBN: ");

        List<Book> books =
                bookController.searchBooks(keyword);

        System.out.println();
        System.out.println(
                "========== SEARCH RESULTS =========="
        );

        if (books.isEmpty()) {

            System.out.println(
                    "No books found."
            );

            return;
        }

        printBooks(books);
    }

    private static void viewAllBooks() {

        List<Book> books =
                bookController.getAllBooks();

        System.out.println();
        System.out.println(
                "========== ALL BOOKS =========="
        );

        if (books.isEmpty()) {

            System.out.println(
                    "No books available."
            );

            return;
        }

        printBooks(books);
    }

    private static void printBooks(List<Book> books) {

        System.out.printf(
                "%-6s %-18s %-35s %-8s%n",
                "ID",
                "ISBN",
                "TITLE",
                "YEAR"
        );

        System.out.println(
                "--------------------------------------------------------------------------"
        );

        for (Book book : books) {

            String year =
                    book.getPublicationYear() == null
                            ? "-"
                            : book.getPublicationYear().toString();

            System.out.printf(
                    "%-6d %-18s %-35s %-8s%n",
                    book.getBookId(),
                    book.getIsbn(),
                    shorten(book.getTitle(), 35),
                    year
            );
        }
    }

    private static void checkCopyAvailability() {

        int copyId =
                readInt("Enter book copy ID: ");

        boolean available =
                loanController.isCopyAvailable(copyId);

        System.out.println();

        if (available) {

            System.out.println(
                    "Book copy is AVAILABLE."
            );

        } else {

            System.out.println(
                    "Book copy is NOT AVAILABLE."
            );
        }
    }

    // =============================================================
    // LOAN OPERATIONS
    // =============================================================

    /*
     * LIBRARIAN OPERATION
     */
    private static void issueBook() {

        int memberId =
                readInt("Enter member ID: ");

        int copyId =
                readInt("Enter book copy ID: ");

        Loan loan =
                loanController.issueBook(
                        memberId,
                        copyId
                );

        System.out.println();
        System.out.println(
                "Book issued successfully."
        );

        System.out.println(
                "Loan ID: " + loan.getLoanId()
        );

        System.out.println(
                "Issue Date: " + loan.getIssueDate()
        );

        System.out.println(
                "Due Date: " + loan.getDueDate()
        );
    }

    /*
     * LIBRARIAN OPERATION
     */
    private static void returnBook() {

        int loanId =
                readInt("Enter loan ID: ");

        Loan loan =
                loanController.returnBook(loanId);

        System.out.println();
        System.out.println(
                "Book returned successfully."
        );

        System.out.println(
                "Loan ID: " + loan.getLoanId()
        );

        System.out.println(
                "Return Date: " + loan.getReturnDate()
        );
    }

    /*
     * LIBRARIAN OPERATION
     */
    private static void renewLoan() {

        int loanId =
                readInt("Enter loan ID: ");

        Loan loan =
                loanController.renewLoan(loanId);

        System.out.println();
        System.out.println(
                "Loan renewed successfully."
        );

        System.out.println(
                "Loan ID: " + loan.getLoanId()
        );

        System.out.println(
                "New Due Date: " + loan.getDueDate()
        );

        System.out.println(
                "Renewal Count: "
                        + loan.getRenewalCount()
        );
    }

    /*
     * MEMBER OPERATION
     *
     * No member ID is requested from the user.
     */
    private static void viewMyLoans(int memberId) {

        List<Loan> loans =
                loanController.getMemberLoans(memberId);

        System.out.println();
        System.out.println(
                "========== MY LOANS =========="
        );

        if (loans.isEmpty()) {

            System.out.println(
                    "No loans found."
            );

            return;
        }

        printLoans(loans);
    }

    /*
     * MEMBER OPERATION
     */
    private static void viewActiveLoans(int memberId) {

        List<Loan> loans =
                loanController.getActiveMemberLoans(
                        memberId
                );

        System.out.println();
        System.out.println(
                "========== MY ACTIVE LOANS =========="
        );

        if (loans.isEmpty()) {

            System.out.println(
                    "No active loans found."
            );

            return;
        }

        printLoans(loans);
    }

    /*
     * MEMBER OPERATION
     *
     * A member can renew only their own loan.
     */
    private static void renewMemberLoan(int memberId) {

        int loanId =
                readInt("Enter your loan ID: ");

        Loan existingLoan =
                loanController.getLoan(loanId);

        if (existingLoan.getMemberId() != memberId) {

            throw new SecurityException(
                    "You are not authorized to renew this loan."
            );
        }

        Loan loan =
                loanController.renewLoan(loanId);

        System.out.println();
        System.out.println(
                "Loan renewed successfully."
        );

        System.out.println(
                "Loan ID: " + loan.getLoanId()
        );

        System.out.println(
                "New Due Date: " + loan.getDueDate()
        );

        System.out.println(
                "Renewal Count: "
                        + loan.getRenewalCount()
        );
    }

    /*
     * MEMBER OPERATION
     *
     * A member can return only their own loan.
     */
    private static void returnMemberBook(int memberId) {

        int loanId =
                readInt("Enter your loan ID: ");

        Loan existingLoan =
                loanController.getLoan(loanId);

        if (existingLoan.getMemberId() != memberId) {

            throw new SecurityException(
                    "You are not authorized to return this loan."
            );
        }

        Loan loan =
                loanController.returnBook(loanId);

        System.out.println();
        System.out.println(
                "Book returned successfully."
        );

        System.out.println(
                "Loan ID: " + loan.getLoanId()
        );

        System.out.println(
                "Return Date: " + loan.getReturnDate()
        );
    }

    /*
     * LIBRARIAN OPERATION
     */
    private static void viewMemberLoans() {

        int memberId =
                readInt("Enter member ID: ");

        List<Loan> loans =
                loanController.getMemberLoans(
                        memberId
                );

        System.out.println();
        System.out.println(
                "========== MEMBER LOANS =========="
        );

        if (loans.isEmpty()) {

            System.out.println(
                    "No loans found."
            );

            return;
        }

        printLoans(loans);
    }

    /*
     * LIBRARIAN OPERATION
     */
    private static void viewActiveMemberLoans() {

        int memberId =
                readInt("Enter member ID: ");

        List<Loan> loans =
                loanController.getActiveMemberLoans(
                        memberId
                );

        System.out.println();
        System.out.println(
                "========== ACTIVE MEMBER LOANS =========="
        );

        if (loans.isEmpty()) {

            System.out.println(
                    "No active loans found."
            );

            return;
        }

        printLoans(loans);
    }

    private static void viewOverdueLoans() {

        List<Loan> loans =
                loanController.getOverdueLoans();

        System.out.println();
        System.out.println(
                "========== OVERDUE LOANS =========="
        );

        if (loans.isEmpty()) {

            System.out.println(
                    "No overdue loans."
            );

            return;
        }

        printLoans(loans);
    }

    private static void printLoans(List<Loan> loans) {

        System.out.printf(
                "%-7s %-9s %-9s %-23s %-23s %-12s%n",
                "LOAN ID",
                "MEMBER",
                "COPY",
                "ISSUE DATE",
                "DUE DATE",
                "STATUS"
        );

        System.out.println(
                "--------------------------------------------------------------------------------"
        );

        for (Loan loan : loans) {

            System.out.printf(
                    "%-7d %-9d %-9d %-23s %-23s %-12s%n",
                    loan.getLoanId(),
                    loan.getMemberId(),
                    loan.getCopyId(),
                    loan.getIssueDate(),
                    loan.getDueDate(),
                    loan.getLoanStatus()
            );
        }
    }

    // =============================================================
    // RESERVATION OPERATIONS
    // =============================================================

    /*
     * MEMBER OPERATION
     *
     * Member ID is automatically obtained from the logged-in
     * account.
     */
    private static void reserveBook(int memberId) {

        int bookId =
                readInt("Enter book ID: ");

        Reservation reservation =
                reservationController.reserveBook(
                        memberId,
                        bookId
                );

        System.out.println();
        System.out.println(
                "Book reserved successfully."
        );

        System.out.println(
                "Reservation ID: "
                        + reservation.getReservationId()
        );

        System.out.println(
                "Book ID: "
                        + reservation.getBookId()
        );

        System.out.println(
                "Expiry Date: "
                        + reservation.getExpiryDate()
        );
    }

    /*
     * MEMBER OPERATION
     */
    private static void viewMyReservations(int memberId) {

        List<Reservation> reservations =
                reservationController
                        .getMemberReservations(memberId);

        System.out.println();
        System.out.println(
                "========== MY RESERVATIONS =========="
        );

        if (reservations.isEmpty()) {

            System.out.println(
                    "No reservations found."
            );

            return;
        }

        printReservations(reservations);
    }

    /*
     * MEMBER OPERATION
     *
     * The member can cancel only a reservation that appears
     * in their own reservation list.
     */
    private static void cancelMemberReservation(int memberId) {

        int reservationId =
                readInt("Enter your reservation ID: ");

        List<Reservation> reservations =
                reservationController
                        .getMemberReservations(memberId);

        boolean belongsToMember = false;

        for (Reservation reservation : reservations) {

            if (reservation.getReservationId()
                    == reservationId) {

                belongsToMember = true;
                break;
            }
        }

        if (!belongsToMember) {

            throw new SecurityException(
                    "You are not authorized to cancel this reservation."
            );
        }

        reservationController.cancelReservation(
                reservationId
        );

        System.out.println(
                "Reservation cancelled successfully."
        );
    }

    /*
     * LIBRARIAN / ADMIN OPERATION
     */
    private static void viewBookReservations() {

        int bookId =
                readInt("Enter book ID: ");

        List<Reservation> reservations =
                reservationController
                        .getBookReservations(bookId);

        System.out.println();
        System.out.println(
                "========== BOOK RESERVATIONS =========="
        );

        if (reservations.isEmpty()) {

            System.out.println(
                    "No reservations found."
            );

            return;
        }

        printReservations(reservations);
    }

    private static void printReservations(
            List<Reservation> reservations) {

        for (Reservation reservation : reservations) {

            System.out.println(
                    "Reservation ID: "
                            + reservation.getReservationId()
            );

            System.out.println(
                    "Book ID: "
                            + reservation.getBookId()
            );

            System.out.println(
                    "Member ID: "
                            + reservation.getMemberId()
            );

            System.out.println(
                    "Reservation Date: "
                            + reservation.getReservationDate()
            );

            System.out.println(
                    "Expiry Date: "
                            + reservation.getExpiryDate()
            );

            System.out.println(
                    "Priority: "
                            + reservation.getPriorityNumber()
            );

            System.out.println(
                    "Status: "
                            + reservation.getReservationStatus()
            );

            System.out.println(
                    "------------------------------------"
            );
        }
    }

    /*
     * LIBRARIAN OPERATION
     */
    private static void cancelReservation() {

        int reservationId =
                readInt("Enter reservation ID: ");

        reservationController.cancelReservation(
                reservationId
        );

        System.out.println(
                "Reservation cancelled successfully."
        );
    }

    // =============================================================
    // FINE OPERATIONS
    // =============================================================

    /*
     * MEMBER OPERATION
     */
    private static void viewMyFines(int memberId) {

        List<Fine> fines =
                fineController.getMemberFines(memberId);

        System.out.println();
        System.out.println(
                "========== MY FINES =========="
        );

        if (fines.isEmpty()) {

            System.out.println(
                    "No fines found."
            );

        } else {

            printFines(fines);
        }

        BigDecimal outstanding =
                fineController
                        .getOutstandingAmount(memberId);

        System.out.println(
                "Total Outstanding: INR "
                        + outstanding
        );
    }

    /*
     * LIBRARIAN / ADMIN OPERATION
     */
    private static void viewUnpaidFines() {

        List<Fine> fines =
                fineController.getUnpaidFines();

        System.out.println();
        System.out.println(
                "========== UNPAID FINES =========="
        );

        if (fines.isEmpty()) {

            System.out.println(
                    "No unpaid fines."
            );

            return;
        }

        printFines(fines);
    }

    private static void printFines(List<Fine> fines) {

        for (Fine fine : fines) {

            System.out.println(
                    "Fine ID: " + fine.getFineId()
            );

            System.out.println(
                    "Loan ID: " + fine.getLoanId()
            );

            System.out.println(
                    "Member ID: " + fine.getMemberId()
            );

            System.out.println(
                    "Fine Type: " + fine.getFineType()
            );

            System.out.println(
                    "Amount: INR " + fine.getFineAmount()
            );

            System.out.println(
                    "Paid: INR " + fine.getPaidAmount()
            );

            System.out.println(
                    "Remaining: INR "
                            + fine.getRemainingAmount()
            );

            System.out.println(
                    "Status: " + fine.getFineStatus()
            );

            System.out.println(
                    "------------------------------------"
            );
        }
    }

    /*
     * MEMBER OPERATION
     *
     * A member can pay only their own fine.
     */
    private static void payMemberFine(int memberId) {

        int fineId =
                readInt("Enter your fine ID: ");

        Fine fine =
                fineController.getFine(fineId);

        if (fine.getMemberId() != memberId) {

            throw new SecurityException(
                    "You are not authorized to pay this fine."
            );
        }

        BigDecimal amount =
                readBigDecimal(
                        "Enter payment amount (INR): "
                );

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {

            System.out.println(
                    "Payment amount must be greater than zero."
            );

            return;
        }

        fineController.payFine(
                fineId,
                amount
        );

        System.out.println();
        System.out.println(
                "Fine payment processed successfully."
        );
    }

    // =============================================================
    // INPUT HELPERS
    // =============================================================

    private static int readInt(String message) {

        while (true) {

            System.out.print(message);

            try {

                return Integer.parseInt(
                        scanner.nextLine().trim()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a valid number."
                );
            }
        }
    }

    private static BigDecimal readBigDecimal(
            String message) {

        while (true) {

            System.out.print(message);

            try {

                return new BigDecimal(
                        scanner.nextLine().trim()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a valid amount."
                );
            }
        }
    }

    private static String readString(String message) {

        System.out.print(message);

        return scanner.nextLine().trim();
    }

    private static String shorten(
            String text,
            int length) {

        if (text == null) {
            return "";
        }

        if (text.length() <= length) {
            return text;
        }

        return text.substring(0, length - 3)
                + "...";
    }
}

