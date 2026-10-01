package com.library.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class LoanTest {


@Test
void shouldIdentifyReturnedLoan() {
    Loan loan = new Loan();

    loan.setLoanStatus("RETURNED");

    assertTrue(loan.isReturned());
}

@Test
void shouldIdentifyNotReturnedLoan() {
    Loan loan = new Loan();

    loan.setLoanStatus("ISSUED");

    assertFalse(loan.isReturned());
}

@Test
void shouldIdentifyOverdueLoan() {
    Loan loan = new Loan();

    loan.setDueDate(LocalDateTime.now().minusDays(5));
    loan.setLoanStatus("ISSUED");

    assertTrue(loan.isOverdue());
}

@Test
void shouldIdentifyNonOverdueLoan() {
    Loan loan = new Loan();

    loan.setDueDate(LocalDateTime.now().plusDays(5));
    loan.setLoanStatus("ISSUED");

    assertFalse(loan.isOverdue());
}

@Test
void shouldCalculateOverdueDays() {
    Loan loan = new Loan();

    loan.setDueDate(LocalDateTime.now().minusDays(5));
    loan.setLoanStatus("ISSUED");

    assertTrue(loan.getOverdueDays() >= 5);
}


}
