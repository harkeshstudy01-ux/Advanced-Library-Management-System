package com.library.service;

import com.library.exception.PaymentException;
import com.library.exception.ValidationException;
import com.library.model.Fine;
import com.library.model.Payment;
import com.library.repository.FineRepository;
import com.library.repository.PaymentRepository;
import com.library.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Service layer for payment operations.
 *
 * Uses JDBC transactions so that updating the fine
 * and creating the payment record succeed or fail
 * together.
 *
 * Architecture:
 * Controller -> Service -> Repository -> MySQL
 */
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final FineRepository fineRepository;
    private final MemberService memberService;

    public PaymentService() {
        this.paymentRepository = new PaymentRepository();
        this.fineRepository = new FineRepository();
        this.memberService = new MemberService();
    }

    /**
     * Records a payment against a fine.
     *
     * Fine update and payment insertion are performed
     * inside one database transaction.
     */
    public int makePayment(
            int fineId,
            BigDecimal amount,
            String paymentMethod,
            String transactionReference) {

        validatePaymentInput(
                fineId,
                amount,
                paymentMethod
        );

        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() ->
                        new PaymentException(
                                "Fine not found with ID: "
                                        + fineId
                        )
                );

        if (fine.isPaid()) {
            throw new PaymentException(
                    "This fine has already been paid."
            );
        }

        BigDecimal remaining =
                fine.getRemainingAmount();

        if (remaining == null
                || remaining.compareTo(BigDecimal.ZERO) <= 0) {

            throw new PaymentException(
                    "This fine has no outstanding amount."
            );
        }

        if (amount.compareTo(remaining) > 0) {
            throw new PaymentException(
                    "Payment cannot exceed remaining fine amount. "
                            + "Remaining amount: "
                            + remaining
            );
        }

        try {
            memberService.getMemberById(
                    fine.getMemberId()
            );
        } catch (RuntimeException e) {

            throw new PaymentException(
                    "Unable to verify the member associated "
                            + "with this fine.",
                    e
            );
        }

        String normalizedReference =
                normalizeTransactionReference(
                        transactionReference
                );

        String normalizedMethod =
                paymentMethod.trim().toUpperCase();

        Connection connection = null;

        try {

            connection =
                    DatabaseConnection.getConnection();

            /*
             * Disable auto-commit so the fine update
             * and payment insertion form one transaction.
             */
            connection.setAutoCommit(false);

            /*
             * STEP 1:
             * Update the fine.
             */
            boolean fineUpdated =
                    fineRepository.updatePayment(
                            connection,
                            fineId,
                            amount
                    );

            if (!fineUpdated) {
                throw new PaymentException(
                        "Unable to update fine payment."
                );
            }

            /*
             * STEP 2:
             * Insert payment record.
             */
            int paymentId =
                    paymentRepository.createPayment(
                            connection,
                            fineId,
                            fine.getMemberId(),
                            amount,
                            normalizedMethod,
                            normalizedReference
                    );

            /*
             * Both operations succeeded.
             */
            connection.commit();

            return paymentId;

        } catch (PaymentException e) {

            rollbackQuietly(connection);
            throw e;

        } catch (SQLException e) {

            rollbackQuietly(connection);

            throw new PaymentException(
                    "Payment transaction failed. "
                            + "All changes have been rolled back.",
                    e
            );

        } catch (RuntimeException e) {

            rollbackQuietly(connection);

            throw new PaymentException(
                    "Payment could not be completed. "
                            + "All changes have been rolled back.",
                    e
            );

        } finally {

            restoreAutoCommitAndClose(
                    connection
            );
        }
    }

    /**
     * Retrieve a payment by ID.
     */
    public Payment getPayment(int paymentId) {

        if (paymentId <= 0) {
            throw new ValidationException(
                    "Payment ID must be greater than zero."
            );
        }

        return paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new PaymentException(
                                "Payment not found with ID: "
                                        + paymentId
                        )
                );
    }

    /**
     * Retrieve all payments made by a member.
     */
    public List<Payment> getMemberPayments(
            int memberId) {

        validateMember(memberId);

        try {
            return paymentRepository.findByMemberId(
                    memberId
            );
        } catch (RuntimeException e) {

            throw new PaymentException(
                    "Unable to retrieve payment history "
                            + "for member: "
                            + memberId,
                    e
            );
        }
    }

    /**
     * Retrieve all payments associated with a fine.
     */
    public List<Payment> getFinePayments(
            int fineId) {

        if (fineId <= 0) {
            throw new ValidationException(
                    "Fine ID must be greater than zero."
            );
        }

        fineRepository.findById(fineId)
                .orElseThrow(() ->
                        new PaymentException(
                                "Fine not found with ID: "
                                        + fineId
                        )
                );

        try {
            return paymentRepository.findByFineId(
                    fineId
            );
        } catch (RuntimeException e) {

            throw new PaymentException(
                    "Unable to retrieve payments "
                            + "for fine: "
                            + fineId,
                    e
            );
        }
    }

    /**
     * Calculate the total outstanding fine amount.
     */
    public BigDecimal getMemberOutstandingAmount(
            int memberId) {

        validateMember(memberId);

        try {
            return fineRepository.getOutstandingAmount(
                    memberId
            );
        } catch (RuntimeException e) {

            throw new PaymentException(
                    "Unable to calculate outstanding "
                            + "amount for member: "
                            + memberId,
                    e
            );
        }
    }

    /**
     * Validate payment input.
     */
    private void validatePaymentInput(
            int fineId,
            BigDecimal amount,
            String paymentMethod) {

        if (fineId <= 0) {
            throw new ValidationException(
                    "Fine ID must be greater than zero."
            );
        }

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new ValidationException(
                    "Payment amount must be greater than zero."
            );
        }

        if (amount.scale() > 2) {
            throw new ValidationException(
                    "Payment amount cannot have more than "
                            + "two decimal places."
            );
        }

        if (paymentMethod == null
                || paymentMethod.isBlank()) {

            throw new ValidationException(
                    "Payment method cannot be empty."
            );
        }

        String method =
                paymentMethod.trim().toUpperCase();

        if (!method.equals("CASH")
                && !method.equals("CARD")
                && !method.equals("UPI")
                && !method.equals("NET_BANKING")) {

            throw new ValidationException(
                    "Invalid payment method. Use CASH, CARD, "
                            + "UPI or NET_BANKING."
            );
        }
    }

    /**
     * Validate member existence.
     */
    private void validateMember(int memberId) {

        if (memberId <= 0) {
            throw new ValidationException(
                    "Member ID must be greater than zero."
            );
        }

        try {
            memberService.getMemberById(memberId);
        } catch (RuntimeException e) {

            throw new PaymentException(
                    "Unable to verify member: "
                            + memberId,
                    e
            );
        }
    }

    /**
     * Normalize optional transaction reference.
     */
    private String normalizeTransactionReference(
            String transactionReference) {

        if (transactionReference == null) {
            return null;
        }

        String reference =
                transactionReference.trim();

        if (reference.isEmpty()) {
            return null;
        }

        if (reference.length() > 100) {
            throw new ValidationException(
                    "Transaction reference cannot exceed "
                            + "100 characters."
            );
        }

        return reference;
    }

    /**
     * Roll back a transaction without hiding
     * the original exception.
     */
    private void rollbackQuietly(
            Connection connection) {

        if (connection == null) {
            return;
        }

        try {
            connection.rollback();
        } catch (SQLException ignored) {
            // Original exception is more important.
        }
    }

    /**
     * Restore connection state and close it.
     */
    private void restoreAutoCommitAndClose(
            Connection connection) {

        if (connection == null) {
            return;
        }

        try {
            connection.setAutoCommit(true);
        } catch (SQLException ignored) {
            // Connection is being closed.
        }

        try {
            connection.close();
        } catch (SQLException ignored) {
            // Nothing else can be done here.
        }
    }
}

