package com.library.repository;

import com.library.model.Payment;
import com.library.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentRepository {

    private static final String BASE_SELECT =
            "SELECT payment_id, fine_id, member_id, amount, " +
            "payment_method, transaction_reference, " +
            "payment_date, payment_status " +
            "FROM payments ";

    /**
     * Create a payment using a new database connection.
     *
     * Kept for compatibility with existing code.
     */
    public int createPayment(
            int fineId,
            int memberId,
            BigDecimal amount,
            String paymentMethod,
            String transactionReference) {

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            return createPayment(
                    connection,
                    fineId,
                    memberId,
                    amount,
                    paymentMethod,
                    transactionReference
            );

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to create payment.",
                    e
            );
        }
    }

    /**
     * Transaction-aware payment creation.
     *
     * The supplied connection is NOT closed here.
     * The caller controls COMMIT/ROLLBACK.
     */
    public int createPayment(
            Connection connection,
            int fineId,
            int memberId,
            BigDecimal amount,
            String paymentMethod,
            String transactionReference)
            throws SQLException {

        String sql =
                "INSERT INTO payments " +
                "(fine_id, member_id, amount, payment_method, " +
                "transaction_reference, payment_status) " +
                "VALUES (?, ?, ?, ?, ?, 'SUCCESS')";

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, fineId);
            statement.setInt(2, memberId);
            statement.setBigDecimal(3, amount);
            statement.setString(4, paymentMethod);

            if (transactionReference == null
                    || transactionReference.isBlank()) {

                statement.setNull(
                        5,
                        Types.VARCHAR
                );

            } else {

                statement.setString(
                        5,
                        transactionReference
                );
            }

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException(
                        "Payment record was not created."
                );
            }

            try (ResultSet keys =
                         statement.getGeneratedKeys()) {

                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException(
                "Payment was created but payment ID "
                        + "could not be retrieved."
        );
    }

    public Optional<Payment> findById(int paymentId) {

        String sql =
                BASE_SELECT +
                "WHERE payment_id = ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, paymentId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapRow(resultSet)
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to find payment: "
                            + paymentId,
                    e
            );
        }

        return Optional.empty();
    }

    public List<Payment> findByMemberId(int memberId) {

        String sql =
                BASE_SELECT +
                "WHERE member_id = ? " +
                "ORDER BY payment_date DESC";

        List<Payment> payments =
                new ArrayList<>();

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, memberId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    payments.add(
                            mapRow(resultSet)
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to find payments for member: "
                            + memberId,
                    e
            );
        }

        return payments;
    }

    public List<Payment> findByFineId(int fineId) {

        String sql =
                BASE_SELECT +
                "WHERE fine_id = ? " +
                "ORDER BY payment_date DESC";

        List<Payment> payments =
                new ArrayList<>();

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, fineId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    payments.add(
                            mapRow(resultSet)
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to find payments for fine: "
                            + fineId,
                    e
            );
        }

        return payments;
    }

    private Payment mapRow(ResultSet resultSet)
            throws SQLException {

        Payment payment = new Payment();

        payment.setPaymentId(
                resultSet.getInt("payment_id")
        );

        payment.setFineId(
                resultSet.getInt("fine_id")
        );

        payment.setMemberId(
                resultSet.getInt("member_id")
        );

        payment.setAmount(
                resultSet.getBigDecimal("amount")
        );

        payment.setPaymentMethod(
                resultSet.getString("payment_method")
        );

        payment.setTransactionReference(
                resultSet.getString(
                        "transaction_reference"
                )
        );

        Timestamp timestamp =
                resultSet.getTimestamp(
                        "payment_date"
                );

        if (timestamp != null) {
            payment.setPaymentDate(
                    timestamp.toLocalDateTime()
            );
        }

        payment.setPaymentStatus(
                resultSet.getString(
                        "payment_status"
                )
        );

        return payment;
    }
}

