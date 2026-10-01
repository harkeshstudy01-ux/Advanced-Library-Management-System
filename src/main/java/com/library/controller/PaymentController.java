
package com.library.controller;

import com.library.model.Payment;
import com.library.service.PaymentService;

import java.math.BigDecimal;
import java.util.List;

public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController() {
        this.paymentService = new PaymentService();
    }

    /**
     * Processes a fine payment.
     */
    public int makePayment(
            int fineId,
            BigDecimal amount,
            String paymentMethod,
            String transactionReference) {

        return paymentService.makePayment(
                fineId,
                amount,
                paymentMethod,
                transactionReference
        );
    }

    /**
     * Retrieves a payment by ID.
     */
    public Payment getPayment(int paymentId) {

        return paymentService.getPayment(
                paymentId
        );
    }

    /**
     * Retrieves all payments made by a member.
     */
    public List<Payment> getMemberPayments(
            int memberId) {

        return paymentService.getMemberPayments(
                memberId
        );
    }

    /**
     * Retrieves all payments associated
     * with a particular fine.
     */
    public List<Payment> getFinePayments(
            int fineId) {

        return paymentService.getFinePayments(
                fineId
        );
    }

    /**
     * Gets the outstanding fine amount
     * for a member.
     */
    public BigDecimal getMemberOutstandingAmount(
            int memberId) {

        return paymentService
                .getMemberOutstandingAmount(
                        memberId
                );
    }
}

