package com.library.repository;

import com.library.model.BookCopy;
import com.library.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookCopyRepository {

    private static final String BASE_SELECT =
            "SELECT copy_id, book_id, shelf_id, accession_number, " +
            "barcode, purchase_date, purchase_price, condition_status, " +
            "availability_status " +
            "FROM book_copies ";

    private static final String FIND_BY_ID =
            BASE_SELECT +
            "WHERE copy_id = ?";

    private static final String FIND_AVAILABLE_BY_BOOK =
            BASE_SELECT +
            "WHERE book_id = ? " +
            "AND availability_status = 'AVAILABLE' " +
            "ORDER BY copy_id";

    private static final String FIND_BY_BOOK =
            BASE_SELECT +
            "WHERE book_id = ? " +
            "ORDER BY copy_id";

    private static final String UPDATE_STATUS =
            "UPDATE book_copies " +
            "SET availability_status = ? " +
            "WHERE copy_id = ?";

    public Optional<BookCopy> findById(int copyId) {

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(FIND_BY_ID)) {

            statement.setInt(1, copyId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to find book copy: " + copyId,
                    e
            );
        }

        return Optional.empty();
    }

    public List<BookCopy> findAvailableByBookId(int bookId) {

        return executeBookQuery(
                FIND_AVAILABLE_BY_BOOK,
                bookId,
                "Unable to find available copies for book: "
        );
    }

    public List<BookCopy> findByBookId(int bookId) {

        return executeBookQuery(
                FIND_BY_BOOK,
                bookId,
                "Unable to retrieve copies for book: "
        );
    }

    public boolean updateAvailability(int copyId, String status) {

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(UPDATE_STATUS)) {

            statement.setString(1, status);
            statement.setInt(2, copyId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to update availability for copy: " + copyId,
                    e
            );
        }
    }

    private List<BookCopy> executeBookQuery(
            String sql,
            int bookId,
            String errorMessage) {

        List<BookCopy> copies = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, bookId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    copies.add(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    errorMessage + bookId,
                    e
            );
        }

        return copies;
    }

    private BookCopy mapRow(ResultSet resultSet)
            throws SQLException {

        BookCopy copy = new BookCopy();

        copy.setCopyId(
                resultSet.getInt("copy_id")
        );

        copy.setBookId(
                resultSet.getInt("book_id")
        );

        int shelfId = resultSet.getInt("shelf_id");

        if (!resultSet.wasNull()) {
            copy.setShelfId(shelfId);
        }

        copy.setAccessionNumber(
                resultSet.getString("accession_number")
        );

        copy.setBarcode(
                resultSet.getString("barcode")
        );

        if (resultSet.getDate("purchase_date") != null) {
            copy.setPurchaseDate(
                    resultSet.getDate("purchase_date")
                            .toLocalDate()
            );
        }

        copy.setPurchasePrice(
                resultSet.getBigDecimal("purchase_price")
        );

        copy.setConditionStatus(
                resultSet.getString("condition_status")
        );

        copy.setAvailabilityStatus(
                resultSet.getString("availability_status")
        );

        return copy;
    }
}

