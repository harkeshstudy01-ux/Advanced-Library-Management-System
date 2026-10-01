package com.library.controller;

import com.library.model.Fine;
import com.library.model.Loan;
import com.library.service.FineService;

import java.math.BigDecimal;
import java.util.List;

public class FineController {

    private final FineService fineService;

    public FineController() {
        this.fineService = new FineService();
    }

    public BigDecimal calculateFine(Loan loan) {
        return fineService.calculateFine(loan);
    }

    public Fine generateFine(int loanId) {
        return fineService.generateFineForLoan(loanId);
    }

    public Fine getFine(int fineId) {
        return fineService.getFine(fineId);
    }

    public List<Fine> getMemberFines(int memberId) {
        return fineService.getMemberFines(memberId);
    }

    public List<Fine> getUnpaidFines() {
        return fineService.getUnpaidFines();
    }

    public BigDecimal getOutstandingAmount(int memberId) {
        return fineService.getOutstandingAmount(memberId);
    }

    public void payFine(int fineId, BigDecimal amount) {
        fineService.payFine(fineId, amount);
    }
}