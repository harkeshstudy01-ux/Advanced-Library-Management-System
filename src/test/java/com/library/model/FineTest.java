
package com.library.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class FineTest {

    @Test
    void shouldCalculateRemainingFineAmount() {
        Fine fine = new Fine();

        fine.setAmount(new BigDecimal("40.00"));
        fine.setPaidAmount(new BigDecimal("10.00"));

        assertEquals(
                new BigDecimal("30.00"),
                fine.getRemainingAmount()
        );
    }

    @Test
    void shouldReturnTrueWhenFineIsFullyPaid() {
        Fine fine = new Fine();

        fine.setAmount(new BigDecimal("40.00"));
        fine.setPaidAmount(new BigDecimal("40.00"));

        assertTrue(fine.isPaid());
    }

    @Test
    void shouldReturnFalseWhenFineIsNotFullyPaid() {
        Fine fine = new Fine();

        fine.setAmount(new BigDecimal("40.00"));
        fine.setPaidAmount(new BigDecimal("20.00"));

        assertFalse(fine.isPaid());
    }

    @Test
    void shouldSupportFineAmountCompatibilityMethods() {
        Fine fine = new Fine();

        fine.setFineAmount(new BigDecimal("25.00"));

        assertEquals(
                new BigDecimal("25.00"),
                fine.getFineAmount()
        );
    }
}
