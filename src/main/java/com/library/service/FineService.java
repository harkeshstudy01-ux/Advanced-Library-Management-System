package com.library.service;

import com.library.model.Fine;
import com.library.model.Loan;
import com.library.model.MembershipPlan;
import com.library.repository.FineRepository;
import com.library.repository.LoanRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class FineService {

    private final FineRepository fineRepository;
    private final LoanRepository loanRepository;
    private final MemberService memberService;

    public FineService() {
        this.fineRepository = new FineRepository();
        this.loanRepository = new LoanRepository();
        this.memberService = new MemberService();
    }

    /**
     * Calculates the fine using the member's membership plan.
     *
     * Example:
     * Standard Plan = ₹1.50/day
     * 10 overdue days = ₹15.00
     */
    public BigDecimal calculateFine(Loan loan) {

        if (loan == null) {
            throw new IllegalArgumentException(
                    "Loan cannot be null."
            );
        }

        long overdueDays = loan.getOverdueDays();

        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal dailyFine =
                memberService.getFinePerDay(
                        loan.getMemberId()
                );

        if (dailyFine == null
                || dailyFine.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalStateException(
                    "Invalid fine-per-day value for member: "
                            + loan.getMemberId()
            );
        }

        return dailyFine
                .multiply(BigDecimal.valueOf(overdueDays))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Generates an overdue fine for a loan.
     */
    public Fine generateFineForLoan(int loanId) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Loan not found with ID: " + loanId
                        ));

        BigDecimal fineAmount =
                calculateFine(loan);

        if (fineAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException(
                    "No fine is applicable for this loan."
            );
        }

        if (fineRepository.findByLoanId(loanId).isPresent()) {
            throw new IllegalStateException(
                    "A fine already exists for this loan."
            );
        }

        int fineId =
                fineRepository.createFine(
                        loanId,
                        loan.getMemberId(),
                        "OVERDUE",
                        fineAmount,
                        "Overdue fine calculated according to membership plan."
                );

        return fineRepository.findById(fineId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Fine was created but could not be retrieved."
                        ));
    }

    public Fine getFine(int fineId) {

        if (fineId <= 0) {
            throw new IllegalArgumentException(
                    "Fine ID must be greater than zero."
            );
        }

        return fineRepository.findById(fineId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Fine not found with ID: " + fineId
                        ));
    }

    public List<Fine> getMemberFines(int memberId) {

        validateMember(memberId);

        return fineRepository.findByMemberId(memberId);
    }

    public List<Fine> getUnpaidFines() {

        return fineRepository.findUnpaidFines();
    }

    public BigDecimal getOutstandingAmount(int memberId) {

        validateMember(memberId);

        return fineRepository.getOutstandingAmount(
                memberId
        );
    }

    /**
     * Returns the daily fine applicable to a member.
     */
    public BigDecimal getDailyFine(int memberId) {

        validateMember(memberId);

        return memberService.getFinePerDay(memberId);
    }

    /**
     * Returns the membership plan used for fine calculation.
     */
    public MembershipPlan getMembershipPlan(int memberId) {

        validateMember(memberId);

        return memberService.getMembershipPlan(memberId);
    }

    public void payFine(
            int fineId,
            BigDecimal amount) {

        if (fineId <= 0) {
            throw new IllegalArgumentException(
                    "Fine ID must be greater than zero."
            );
        }

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero."
            );
        }

        Fine fine = getFine(fineId);

        if (fine.isPaid()) {
            throw new IllegalStateException(
                    "This fine has already been paid."
            );
        }

        BigDecimal remaining =
                fine.getRemainingAmount();

        if (amount.compareTo(remaining) > 0) {
            throw new IllegalArgumentException(
                    "Payment cannot exceed remaining fine amount."
            );
        }

        boolean updated =
                fineRepository.updatePayment(
                        fineId,
                        amount
                );

        if (!updated) {
            throw new IllegalStateException(
                    "Unable to process fine payment."
            );
        }
    }

    private void validateMember(int memberId) {

        if (memberId <= 0) {
            throw new IllegalArgumentException(
                    "Member ID must be greater than zero."
            );
        }

        memberService.getMemberById(memberId);
    }
}

