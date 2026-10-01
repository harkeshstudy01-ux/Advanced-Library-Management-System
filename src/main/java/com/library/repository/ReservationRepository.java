package com.library.repository;

import com.library.model.Reservation;
import com.library.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationRepository {

    private static final String BASE_SELECT =
            "SELECT reservation_id, book_id, member_id, " +
            "reservation_date, expiry_date, reservation_status, " +
            "priority_number " +
            "FROM reservations ";

    // ============================================================
    // FIND RESERVATION BY ID
    // ============================================================

    public Optional<Reservation> findById(int reservationId) {

        String sql =
                BASE_SELECT +
                "WHERE reservation_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, reservationId);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to find reservation: " + reservationId,
                    e
            );
        }

        return Optional.empty();
    }

    // ============================================================
    // FIND ALL RESERVATIONS OF A MEMBER
    // ============================================================

    public List<Reservation> findByMemberId(int memberId) {

        String sql =
                BASE_SELECT +
                "WHERE member_id = ? " +
                "ORDER BY reservation_date DESC";

        List<Reservation> reservations = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, memberId);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    reservations.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to find member reservations.",
                    e
            );
        }

        return reservations;
    }

    // ============================================================
    // FIND ACTIVE RESERVATIONS FOR A BOOK
    // ============================================================

    public List<Reservation> findActiveByBookId(int bookId) {

        String sql =
                BASE_SELECT +
                "WHERE book_id = ? " +
                "AND reservation_status = 'ACTIVE' " +
                "AND (expiry_date IS NULL " +
                "OR expiry_date >= CURRENT_TIMESTAMP) " +
                "ORDER BY priority_number ASC, reservation_date ASC";

        List<Reservation> reservations = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, bookId);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    reservations.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to find active reservations.",
                    e
            );
        }

        return reservations;
    }

    // ============================================================
    // CREATE RESERVATION - LOCALDATETIME VERSION
    // ============================================================

    public int createReservation(
            int memberId,
            int bookId,
            LocalDateTime reservationDate,
            LocalDateTime expiryDate) {

        /*
         * Automatically calculate the next priority number
         * for this particular book.
         */
        int priorityNumber = getNextPriorityNumber(bookId);

        return createReservation(
                memberId,
                bookId,
                reservationDate,
                expiryDate,
                priorityNumber
        );
    }

    // ============================================================
    // CREATE RESERVATION - FULL VERSION
    // ============================================================

    public int createReservation(
            int memberId,
            int bookId,
            LocalDateTime reservationDate,
            LocalDateTime expiryDate,
            Integer priorityNumber) {

        String sql =
                "INSERT INTO reservations " +
                "(book_id, member_id, reservation_date, " +
                "expiry_date, reservation_status, priority_number) " +
                "VALUES (?, ?, ?, ?, 'ACTIVE', ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, bookId);
            statement.setInt(2, memberId);

            statement.setTimestamp(
                    3,
                    Timestamp.valueOf(reservationDate)
            );

            if (expiryDate != null) {

                statement.setTimestamp(
                        4,
                        Timestamp.valueOf(expiryDate)
                );

            } else {

                statement.setNull(
                        4,
                        Types.TIMESTAMP
                );
            }

            if (priorityNumber != null) {

                statement.setInt(
                        5,
                        priorityNumber
                );

            } else {

                statement.setNull(
                        5,
                        Types.INTEGER
                );
            }

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {

                throw new RuntimeException(
                        "Reservation creation failed."
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
                    "Failed to create reservation.",
                    e
            );
        }

        throw new RuntimeException(
                "Reservation ID could not be generated."
        );
    }

    // ============================================================
    // COMPATIBILITY VERSION - LOCALDATE
    // ============================================================

    public int createReservation(
            int memberId,
            int bookId,
            LocalDate reservationDate,
            LocalDate expiryDate) {

        return createReservation(
                memberId,
                bookId,
                reservationDate.atStartOfDay(),
                expiryDate == null
                        ? null
                        : expiryDate.atStartOfDay()
        );
    }

    // ============================================================
    // GET NEXT PRIORITY NUMBER
    // ============================================================

    private int getNextPriorityNumber(int bookId) {

        String sql =
                "SELECT COALESCE(MAX(priority_number), 0) + 1 " +
                "FROM reservations " +
                "WHERE book_id = ? " +
                "AND reservation_status = 'ACTIVE' " +
                "AND (expiry_date IS NULL " +
                "OR expiry_date >= CURRENT_TIMESTAMP)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, bookId);

            try (ResultSet rs =
                         statement.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to calculate reservation priority.",
                    e
            );
        }

        return 1;
    }

    // ============================================================
    // CANCEL RESERVATION
    // ============================================================

    public boolean cancelReservation(int reservationId) {

        String sql =
                "UPDATE reservations " +
                "SET reservation_status = 'CANCELLED' " +
                "WHERE reservation_id = ? " +
                "AND reservation_status = 'ACTIVE'";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, reservationId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to cancel reservation.",
                    e
            );
        }
    }

    // ============================================================
    // EXPIRE RESERVATION
    // ============================================================

    public boolean expireReservation(int reservationId) {

        String sql =
                "UPDATE reservations " +
                "SET reservation_status = 'EXPIRED' " +
                "WHERE reservation_id = ? " +
                "AND reservation_status = 'ACTIVE'";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, reservationId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to expire reservation.",
                    e
            );
        }
    }

    // ============================================================
    // COUNT ACTIVE RESERVATIONS OF MEMBER
    // ============================================================

    public int countActiveReservations(int memberId) {

        String sql =
                "SELECT COUNT(*) " +
                "FROM reservations " +
                "WHERE member_id = ? " +
                "AND reservation_status = 'ACTIVE' " +
                "AND (expiry_date IS NULL " +
                "OR expiry_date >= CURRENT_TIMESTAMP)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, memberId);

            try (ResultSet rs =
                         statement.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to count reservations.",
                    e
            );
        }

        return 0;
    }

    // ============================================================
    // MAP DATABASE ROW TO RESERVATION OBJECT
    // ============================================================

    private Reservation mapRow(ResultSet rs)
            throws SQLException {

        Reservation reservation =
                new Reservation();

        reservation.setReservationId(
                rs.getInt("reservation_id")
        );

        reservation.setBookId(
                rs.getInt("book_id")
        );

        reservation.setMemberId(
                rs.getInt("member_id")
        );

        Timestamp reservationDate =
                rs.getTimestamp("reservation_date");

        if (reservationDate != null) {

            reservation.setReservationDate(
                    reservationDate.toLocalDateTime()
            );
        }

        Timestamp expiryDate =
                rs.getTimestamp("expiry_date");

        if (expiryDate != null) {

            reservation.setExpiryDate(
                    expiryDate.toLocalDateTime()
            );
        }

        reservation.setReservationStatus(
                rs.getString("reservation_status")
        );

        int priorityNumber =
                rs.getInt("priority_number");

        if (!rs.wasNull()) {

            reservation.setPriorityNumber(
                    priorityNumber
            );
        }

        return reservation;
    }
}


