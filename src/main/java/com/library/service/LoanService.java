package com.library.service;

import com.library.model.Loan;
import com.library.model.Member;
import com.library.model.MembershipPlan;
import com.library.repository.BookCopyRepository;
import com.library.repository.LoanRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for library loan operations.
 *
 * Responsibilities:
 * - Issue books
 * - Return books
 * - Renew loans
 * - Check borrowing limits
 * - Validate memberships
 * - Check book-copy availability
 * - Retrieve member and overdue loans
 * - Apply membership-plan-based loan rules
 *
 * Architecture:
 *
 * Main
 *   ↓
 * LoanController
 *   ↓
 * LoanService
 *   ├── MemberService
 *   │      ↓
 *   │   MembershipPlanService
 *   │      ↓
 *   │   MembershipPlanRepository
 *   │
 *   ├── LoanRepository
 *   └── BookCopyRepository
 *          ↓
 *        MySQL
 */
public class LoanService {

    /*
     * Maximum renewals are currently a system-wide rule because
     * the membership_plans table does not contain a renewal-limit
     * column.
     */
    private static final int MAX_RENEWALS = 2;

    private final LoanRepository loanRepository;
    private final BookCopyRepository bookCopyRepository;
    private final MemberService memberService;
    private final MembershipPlanService membershipPlanService;

    /**
     * Creates the loan service.
     */
    public LoanService() {
        this.loanRepository = new LoanRepository();
        this.bookCopyRepository = new BookCopyRepository();
        this.memberService = new MemberService();
        this.membershipPlanService = new MembershipPlanService();
    }

    /**
     * Issues a book copy to a member.
     *
     * Kept for compatibility with existing Main.java.
     */
    public Loan issueBook(int memberId, int copyId) {

        return issueBook(memberId, copyId, null);
    }

    /**
     * Issues a book copy to a member and optionally records
     * the librarian who performed the transaction.
     *
     * The member's membership plan determines:
     *
     * - Maximum number of active books
     * - Loan duration
     */
    public Loan issueBook(
            int memberId,
            int copyId,
            Integer librarianId) {

        Member member = memberService.getMemberById(memberId);

        validateMembership(member);

        /*
         * Retrieve the member's current membership plan.
         */
        MembershipPlan plan =
                memberService.getMembershipPlan(memberId);

        int maxBooks = plan.getMaxBooks();
        int loanPeriodDays = plan.getLoanPeriodDays();

        /*
         * Check borrowing limit according to the
         * member's membership plan.
         */
        int activeLoanCount =
                loanRepository.countActiveLoans(memberId);

        if (activeLoanCount >= maxBooks) {

            throw new IllegalStateException(
                    "Borrowing limit exceeded for "
                            + plan.getPlanName()
                            + " plan. Maximum active loans: "
                            + maxBooks
            );
        }

        /*
         * Validate copy ID.
         */
        if (copyId <= 0) {

            throw new IllegalArgumentException(
                    "Book copy ID must be greater than zero."
            );
        }

        /*
         * Check whether the physical copy exists
         * and is available.
         */
        if (!isCopyAvailable(copyId)) {

            throw new IllegalStateException(
                    "Book copy is currently unavailable."
            );
        }

        /*
         * Validate librarian ID if supplied.
         */
        if (librarianId != null && librarianId <= 0) {

            throw new IllegalArgumentException(
                    "Librarian ID must be greater than zero."
            );
        }

        /*
         * Create issue and due dates.
         *
         * Due date comes from the member's plan.
         */
        LocalDateTime issueDate =
                LocalDateTime.now();

        LocalDateTime dueDate =
                issueDate.plusDays(loanPeriodDays);

        int loanId;

        /*
         * Use the appropriate repository overload.
         */
        if (librarianId == null) {

            loanId = loanRepository.createLoan(
                    memberId,
                    copyId,
                    issueDate,
                    dueDate
            );

        } else {

            loanId = loanRepository.createLoan(
                    memberId,
                    copyId,
                    librarianId,
                    issueDate,
                    dueDate
            );
        }

        if (loanId <= 0) {

            throw new IllegalStateException(
                    "Unable to create the loan."
            );
        }

        /*
         * Mark the physical copy as ISSUED.
         */
        boolean updated =
                bookCopyRepository.updateAvailability(
                        copyId,
                        "ISSUED"
                );

        if (!updated) {

            throw new IllegalStateException(
                    "Loan created but book copy could not be "
                            + "marked as issued."
            );
        }

        /*
         * Retrieve the newly created loan.
         */
        return loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Loan was created but could not "
                                        + "be retrieved."
                        ));
    }

    /**
     * Returns a borrowed book.
     */
    public Loan returnBook(int loanId) {

        Loan loan = getLoan(loanId);

        if (loan.isReturned()) {

            throw new IllegalStateException(
                    "This loan has already been returned."
            );
        }

        LocalDateTime returnDate =
                LocalDateTime.now();

        boolean updated =
                loanRepository.returnLoan(
                        loanId,
                        returnDate
                );

        if (!updated) {

            throw new IllegalStateException(
                    "Unable to return the book."
            );
        }

        /*
         * Make the physical copy available again.
         */
        boolean copyUpdated =
                bookCopyRepository.updateAvailability(
                        loan.getCopyId(),
                        "AVAILABLE"
                );

        if (!copyUpdated) {

            throw new IllegalStateException(
                    "Book returned but copy availability "
                            + "could not be updated."
            );
        }

        return loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Returned loan could not "
                                        + "be retrieved."
                        ));
    }

    /**
     * Renews an active loan.
     *
     * Renewal duration is based on the member's
     * current membership plan.
     */
    public Loan renewLoan(int loanId) {

        Loan loan = getLoan(loanId);

        if (loan.isReturned()) {

            throw new IllegalStateException(
                    "A returned loan cannot be renewed."
            );
        }

        if (loan.getRenewalCount() >= MAX_RENEWALS) {

            throw new IllegalStateException(
                    "Maximum renewal limit reached: "
                            + MAX_RENEWALS
            );
        }

        if (loan.getDueDate() == null) {

            throw new IllegalStateException(
                    "Loan does not have a valid due date."
            );
        }

        /*
         * Retrieve the member's current plan.
         */
        MembershipPlan plan =
                memberService.getMembershipPlan(
                        loan.getMemberId()
                );

        int loanPeriodDays =
                plan.getLoanPeriodDays();

        /*
         * Extend according to the membership plan.
         */
        LocalDateTime newDueDate =
                loan.getDueDate()
                        .plusDays(loanPeriodDays);

        boolean updated =
                loanRepository.renewLoan(
                        loanId,
                        newDueDate,
                        MAX_RENEWALS
                );

        if (!updated) {

            throw new IllegalStateException(
                    "Unable to renew the loan."
            );
        }

        return loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Renewed loan could not "
                                        + "be retrieved."
                        ));
    }

    /**
     * Retrieves a loan by ID.
     */
    public Loan getLoan(int loanId) {

        if (loanId <= 0) {

            throw new IllegalArgumentException(
                    "Loan ID must be greater than zero."
            );
        }

        Optional<Loan> loan =
                loanRepository.findById(loanId);

        if (loan.isEmpty()) {

            throw new IllegalArgumentException(
                    "Loan not found with ID: " + loanId
            );
        }

        return loan.get();
    }

    /**
     * Returns all loans belonging to a member.
     */
    public List<Loan> getMemberLoans(int memberId) {

        memberService.getMemberById(memberId);

        return loanRepository.findAllByMemberId(memberId);
    }

    /**
     * Returns currently active loans belonging to a member.
     */
    public List<Loan> getActiveMemberLoans(int memberId) {

        memberService.getMemberById(memberId);

        return loanRepository.findActiveByMemberId(memberId);
    }

    /**
     * Returns all overdue loans.
     */
    public List<Loan> getOverdueLoans() {

        return loanRepository.findOverdueLoans();
    }

    /**
     * Checks whether a book copy is available.
     */
    public boolean isCopyAvailable(int copyId) {

        if (copyId <= 0) {
            return false;
        }

        return bookCopyRepository.findById(copyId)
                .map(LoanService::checkAvailability)
                .orElse(false);
    }

    /**
     * Checks the availability state of a BookCopy.
     */
    private static boolean checkAvailability(
            com.library.model.BookCopy copy) {

        return copy != null && copy.isAvailable();
    }

    /**
     * Returns the number of active loans for a member.
     */
    public int getActiveLoanCount(int memberId) {

        memberService.getMemberById(memberId);

        return loanRepository.countActiveLoans(memberId);
    }

    /**
     * Retrieves a member after validating the ID.
     */
    private Member getMember(int memberId) {

        return memberService.getMemberById(memberId);
    }

    /**
     * Checks whether the member is allowed to borrow books.
     */
    private void validateMembership(Member member) {

        if (member == null) {

            throw new IllegalArgumentException(
                    "Member cannot be null."
            );
        }

        if (!member.isMembershipActive()) {

            throw new IllegalStateException(
                    "Member membership is expired or inactive."
            );
        }
    }

    // =========================================================
    // MEMBERSHIP PLAN INFORMATION
    // =========================================================

    /**
     * Returns the maximum number of active books allowed
     * for a specific member according to their plan.
     */
    public int getMaxActiveLoans(int memberId) {

        return memberService.getMaxBooksAllowed(memberId);
    }

    /**
     * Returns the maximum renewal limit.
     *
     * This remains a system-wide rule because the current
     * membership_plans table does not contain a renewal-limit
     * column.
     */
    public int getMaxRenewals() {

        return MAX_RENEWALS;
    }

    /**
     * Returns the loan duration for a specific member
     * according to their membership plan.
     */
    public int getStandardLoanDays(int memberId) {

        return memberService.getLoanPeriodDays(memberId);
    }

    /**
     * Compatibility method for existing controller code.
     *
     * Returns the largest maximum-book value among
     * the configured membership plans.
     *
     * Member-specific operations should use:
     * getMaxActiveLoans(memberId)
     */
    public int getMaxActiveLoans() {

        return membershipPlanService.getAllPlans()
                .stream()
                .mapToInt(MembershipPlan::getMaxBooks)
                .max()
                .orElse(0);
    }

    /**
     * Compatibility method for existing controller code.
     *
     * Returns the largest loan period among
     * the configured membership plans.
     *
     * Member-specific operations should use:
     * getStandardLoanDays(memberId)
     */
    public int getStandardLoanDays() {

        return membershipPlanService.getAllPlans()
                .stream()
                .mapToInt(MembershipPlan::getLoanPeriodDays)
                .max()
                .orElse(0);
    }
}

