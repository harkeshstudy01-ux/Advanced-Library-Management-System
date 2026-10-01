package com.library.repository;

import com.library.model.Fine;
import com.library.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FineRepository {

    private static final String BASE_SELECT =
            "SELECT fine_id, loan_id, member_id, fine_type, " +
            "amount, paid_amount, fine_status, description, created_at " +
            "FROM fines ";

    public Optional<Fine> findById(int fineId) {

        String sql =
                BASE_SELECT +
                "WHERE fine_id = ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, fineId);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to find fine: " + fineId,
                    e
            );
        }

        return Optional.empty();
    }

    public List<Fine> findByMemberId(int memberId) {

        String sql =
                BASE_SELECT +
                "WHERE member_id = ? " +
                "ORDER BY created_at DESC";

        List<Fine> fines = new ArrayList<>();

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, memberId);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    fines.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to find member fines.",
                    e
            );
        }

        return fines;
    }

    public Optional<Fine> findByLoanId(int loanId) {

        String sql =
                BASE_SELECT +
                "WHERE loan_id = ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, loanId);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to find loan fine.",
                    e
            );
        }

        return Optional.empty();
    }

    public int createFine(
            int loanId,
            int memberId,
            String fineType,
            BigDecimal amount,
            String description) {

        String sql =
                "INSERT INTO fines " +
                "(loan_id, member_id, fine_type, amount, " +
                "paid_amount, fine_status, description, created_at) " +
                "VALUES (?, ?, ?, ?, 0.00, 'UNPAID', ?, CURRENT_TIMESTAMP)";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, loanId);
            statement.setInt(2, memberId);
            statement.setString(3, fineType);
            statement.setBigDecimal(4, amount);
            statement.setString(5, description);

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException(
                        "Fine creation failed."
                );
            }

            try (ResultSet keys =
                         statement.getGeneratedKeys()) {

                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to create fine.",
                    e
            );
        }

        throw new RuntimeException(
                "Fine ID could not be generated."
        );
    }

    /*
     * Compatibility method for existing service code.
     */
    public int createFine(
            int loanId,
            int memberId,
            BigDecimal amount) {

        return createFine(
                loanId,
                memberId,
                "OVERDUE",
                amount,
                null
        );
    }

    /**
     * Existing payment-update method.
     *
     * Kept for compatibility with existing code.
     */
    public boolean updatePayment(
            int fineId,
            BigDecimal paymentAmount) {

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            return updatePayment(
                    connection,
                    fineId,
                    paymentAmount
            );

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to update fine payment.",
                    e
            );
        }
    }

    /**
     * Transaction-aware fine payment update.
     *
     * The supplied connection is NOT closed here.
     * The caller controls COMMIT/ROLLBACK.
     */
    public boolean updatePayment(
            Connection connection,
            int fineId,
            BigDecimal paymentAmount)
            throws SQLException {

        String sql =
                "UPDATE fines " +
                "SET paid_amount = paid_amount + ?, " +
                "fine_status = CASE " +
                "WHEN paid_amount + ? >= amount " +
                "THEN 'PAID' " +
                "WHEN paid_amount + ? > 0 " +
                "THEN 'PARTIAL' " +
                "ELSE 'UNPAID' " +
                "END " +
                "WHERE fine_id = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setBigDecimal(1, paymentAmount);
            statement.setBigDecimal(2, paymentAmount);
            statement.setBigDecimal(3, paymentAmount);
            statement.setInt(4, fineId);

            return statement.executeUpdate() > 0;
        }
    }

    public BigDecimal getOutstandingAmount(int memberId) {

        String sql =
                "SELECT COALESCE(" +
                "SUM(GREATEST(amount - paid_amount, 0)), 0.00) " +
                "FROM fines " +
                "WHERE member_id = ? " +
                "AND fine_status NOT IN ('PAID', 'WAIVED')";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, memberId);

            try (ResultSet rs =
                         statement.executeQuery()) {

                if (rs.next()) {

                    BigDecimal result =
                            rs.getBigDecimal(1);

                    return result == null
                            ? BigDecimal.ZERO
                            : result;
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to calculate outstanding fine.",
                    e
            );
        }

        return BigDecimal.ZERO;
    }

    public List<Fine> findUnpaidFines() {

        String sql =
                BASE_SELECT +
                "WHERE fine_status IN ('UNPAID', 'PARTIAL') " +
                "ORDER BY created_at ASC";

        List<Fine> fines = new ArrayList<>();

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs =
                     statement.executeQuery()) {

            while (rs.next()) {
                fines.add(mapRow(rs));
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to find unpaid fines.",
                    e
            );
        }

        return fines;
    }

    private Fine mapRow(ResultSet rs)
            throws SQLException {

        Fine fine = new Fine();

        fine.setFineId(
                rs.getInt("fine_id")
        );

        fine.setLoanId(
                rs.getInt("loan_id")
        );

        fine.setMemberId(
                rs.getInt("member_id")
        );

        fine.setFineType(
                rs.getString("fine_type")
        );

        fine.setAmount(
                rs.getBigDecimal("amount")
        );

        fine.setPaidAmount(
                rs.getBigDecimal("paid_amount")
        );

        fine.setFineStatus(
                rs.getString("fine_status")
        );

        fine.setDescription(
                rs.getString("description")
        );

        Timestamp createdAt =
                rs.getTimestamp("created_at");

        if (createdAt != null) {
            fine.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        return fine;
    }
}



