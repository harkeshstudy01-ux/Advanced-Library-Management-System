package com.library.controller;

import com.library.model.Loan;
import com.library.service.LoanService;

import java.util.List;

/**
 * Controller for library loan operations.
 *
 * This class acts as the bridge between the CLI/application
 * layer and LoanService.
 */
public class LoanController {

    private final LoanService loanService;

    public LoanController() {
        this.loanService = new LoanService();
    }

    /**
     * Issues a book copy to a member.
 *
     * This method is kept for compatibility with existing code.
     */
    public Loan issueBook(int memberId, int copyId) {

        return loanService.issueBook(
                memberId,
                copyId
        );
    }

    /**
     * Issues a book copy while recording the librarian
     * responsible for the transaction.
     */
    public Loan issueBook(
            int memberId,
            int copyId,
            Integer librarianId) {

        return loanService.issueBook(
                memberId,
                copyId,
                librarianId
        );
    }

    /**
     * Returns a borrowed book.
     */
    public Loan returnBook(int loanId) {

        return loanService.returnBook(loanId);
    }

    /**
     * Renews an active loan.
     */
    public Loan renewLoan(int loanId) {

        return loanService.renewLoan(loanId);
    }

    /**
     * Retrieves a loan by ID.
     */
    public Loan getLoan(int loanId) {

        return loanService.getLoan(loanId);
    }

    /**
     * Retrieves all loans belonging to a member.
     */
    public List<Loan> getMemberLoans(int memberId) {

        return loanService.getMemberLoans(memberId);
    }

    /**
     * Retrieves active loans belonging to a member.
     */
    public List<Loan> getActiveMemberLoans(int memberId) {

        return loanService.getActiveMemberLoans(memberId);
    }

    /**
     * Retrieves all overdue loans.
     */
    public List<Loan> getOverdueLoans() {

        return loanService.getOverdueLoans();
    }

    /**
     * Checks whether a book copy is available.
     */
    public boolean isCopyAvailable(int copyId) {

        return loanService.isCopyAvailable(copyId);
    }

    /**
     * Returns the number of active loans for a member.
     */
    public int getActiveLoanCount(int memberId) {

        return loanService.getActiveLoanCount(memberId);
    }

    /**
     * Returns the maximum number of active loans allowed.
     */
    public int getMaxActiveLoans() {

        return loanService.getMaxActiveLoans();
    }

    /**
     * Returns the maximum number of renewals allowed.
     */
    public int getMaxRenewals() {

        return loanService.getMaxRenewals();
    }

    /**
     * Returns the standard loan duration in days.
     */
    public int getStandardLoanDays() {

        return loanService.getStandardLoanDays();
    }
}


