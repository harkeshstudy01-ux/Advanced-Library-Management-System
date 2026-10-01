package com.library.repository;

import com.library.model.Loan;
import com.library.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoanRepository {

    private static final String FIND_BY_ID =
            "SELECT loan_id, copy_id, member_id, librarian_id, " +
            "issue_date, due_date, return_date, loan_status, renewal_count " +
            "FROM loans WHERE loan_id = ?";

    private static final String FIND_ACTIVE_BY_MEMBER =
            "SELECT loan_id, copy_id, member_id, librarian_id, " +
            "issue_date, due_date, return_date, loan_status, renewal_count " +
            "FROM loans " +
            "WHERE member_id = ? AND loan_status = 'ISSUED' " +
            "ORDER BY due_date";

    private static final String FIND_ALL_BY_MEMBER =
            "SELECT loan_id, copy_id, member_id, librarian_id, " +
            "issue_date, due_date, return_date, loan_status, renewal_count " +
            "FROM loans " +
            "WHERE member_id = ? " +
            "ORDER BY issue_date DESC";

    private static final String INSERT_LOAN =
            "INSERT INTO loans " +
            "(copy_id, member_id, librarian_id, issue_date, " +
            "due_date, loan_status, renewal_count) " +
            "VALUES (?, ?, ?, ?, ?, 'ISSUED', 0)";

    private static final String RETURN_LOAN =
            "UPDATE loans " +
            "SET return_date = ?, loan_status = 'RETURNED' " +
            "WHERE loan_id = ? AND return_date IS NULL";

    private static final String RENEW_LOAN =
            "UPDATE loans " +
            "SET due_date = ?, renewal_count = renewal_count + 1 " +
            "WHERE loan_id = ? " +
            "AND loan_status = 'ISSUED' " +
            "AND renewal_count < ?";

    private static final String COUNT_ACTIVE_BY_MEMBER =
            "SELECT COUNT(*) FROM loans " +
            "WHERE member_id = ? AND loan_status = 'ISSUED'";

    private static final String FIND_OVERDUE =
            "SELECT loan_id, copy_id, member_id, librarian_id, " +
            "issue_date, due_date, return_date, loan_status, renewal_count " +
            "FROM loans " +
            "WHERE loan_status = 'ISSUED' " +
            "AND due_date < CURRENT_TIMESTAMP " +
            "ORDER BY due_date";

    public Optional<Loan> findById(int loanId) {

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(FIND_BY_ID)) {

            statement.setInt(1, loanId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to find loan: " + loanId,
                    e
            );
        }

        return Optional.empty();
    }

    public List<Loan> findActiveByMemberId(int memberId) {
        return executeMemberQuery(
                FIND_ACTIVE_BY_MEMBER,
                memberId
        );
    }

    public List<Loan> findAllByMemberId(int memberId) {
        return executeMemberQuery(
                FIND_ALL_BY_MEMBER,
                memberId
        );
    }

    public List<Loan> findOverdueLoans() {

        List<Loan> loans = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(FIND_OVERDUE);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                loans.add(mapRow(resultSet));
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to retrieve overdue loans.",
                    e
            );
        }

        return loans;
    }

    public int countActiveLoans(int memberId) {

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(COUNT_ACTIVE_BY_MEMBER)) {

            statement.setInt(1, memberId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to count active loans.",
                    e
            );
        }

        return 0;
    }

    public int createLoan(
            int memberId,
            int copyId,
            LocalDateTime issueDate,
            LocalDateTime dueDate) {

        return createLoan(
                memberId,
                copyId,
                null,
                issueDate,
                dueDate
        );
    }

    public int createLoan(
            int memberId,
            int copyId,
            Integer librarianId,
            LocalDateTime issueDate,
            LocalDateTime dueDate) {

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             INSERT_LOAN,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, copyId);
            statement.setInt(2, memberId);

            if (librarianId == null) {
                statement.setNull(3, java.sql.Types.INTEGER);
            } else {
                statement.setInt(3, librarianId);
            }

            statement.setTimestamp(
                    4,
                    Timestamp.valueOf(issueDate)
            );

            statement.setTimestamp(
                    5,
                    Timestamp.valueOf(dueDate)
            );

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating loan failed.");
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {

                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

            throw new SQLException(
                    "Creating loan failed: no generated ID."
            );

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to create loan.",
                    e
            );
        }
    }

    /*
     * Compatibility method for existing service code
     * that still supplies LocalDate.
     */
    public int createLoan(
            int memberId,
            int copyId,
            LocalDate issueDate,
            LocalDate dueDate) {

        return createLoan(
                memberId,
                copyId,
                null,
                issueDate.atStartOfDay(),
                dueDate.atStartOfDay()
        );
    }

    public boolean returnLoan(
            int loanId,
            LocalDateTime returnDate) {

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(RETURN_LOAN)) {

            statement.setTimestamp(
                    1,
                    Timestamp.valueOf(returnDate)
            );

            statement.setInt(2, loanId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to return loan: " + loanId,
                    e
            );
        }
    }

    public boolean returnLoan(
            int loanId,
            LocalDate returnDate) {

        return returnLoan(
                loanId,
                returnDate.atStartOfDay()
        );
    }

    public boolean renewLoan(
            int loanId,
            LocalDateTime newDueDate,
            int maximumRenewals) {

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(RENEW_LOAN)) {

            statement.setTimestamp(
                    1,
                    Timestamp.valueOf(newDueDate)
            );

            statement.setInt(2, loanId);
            statement.setInt(3, maximumRenewals);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to renew loan: " + loanId,
                    e
            );
        }
    }

    public boolean renewLoan(
            int loanId,
            LocalDate newDueDate,
            int maximumRenewals) {

        return renewLoan(
                loanId,
                newDueDate.atStartOfDay(),
                maximumRenewals
        );
    }

    private List<Loan> executeMemberQuery(
            String sql,
            int memberId) {

        List<Loan> loans = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, memberId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    loans.add(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to retrieve member loans.",
                    e
            );
        }

        return loans;
    }

    private Loan mapRow(ResultSet resultSet)
            throws SQLException {

        Loan loan = new Loan();

        loan.setLoanId(
                resultSet.getInt("loan_id")
        );

        loan.setCopyId(
                resultSet.getInt("copy_id")
        );

        loan.setMemberId(
                resultSet.getInt("member_id")
        );

        int librarianId =
                resultSet.getInt("librarian_id");

        if (!resultSet.wasNull()) {
            loan.setLibrarianId(librarianId);
        }

        Timestamp issueDate =
                resultSet.getTimestamp("issue_date");

        if (issueDate != null) {
            loan.setIssueDate(
                    issueDate.toLocalDateTime()
            );
        }

        Timestamp dueDate =
                resultSet.getTimestamp("due_date");

        if (dueDate != null) {
            loan.setDueDate(
                    dueDate.toLocalDateTime()
            );
        }

        Timestamp returnDate =
                resultSet.getTimestamp("return_date");

        if (returnDate != null) {
            loan.setReturnDate(
                    returnDate.toLocalDateTime()
            );
        }

        loan.setLoanStatus(
                resultSet.getString("loan_status")
        );

        int renewalCount =
                resultSet.getInt("renewal_count");

        if (!resultSet.wasNull()) {
            loan.setRenewalCount(renewalCount);
        }

        return loan;
    }
}

