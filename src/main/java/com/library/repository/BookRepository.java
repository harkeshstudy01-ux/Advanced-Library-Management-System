
package com.library.repository;

import com.library.model.Book;
import com.library.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepository {

    private static final String FIND_BY_ID =
            "SELECT book_id, isbn, title, subtitle, category_id, " +
            "publisher_id, publication_year, edition, language, pages, " +
            "description, price, added_date, book_status " +
            "FROM books WHERE book_id = ?";

    private static final String SEARCH_BOOKS =
            "SELECT book_id, isbn, title, subtitle, category_id, " +
            "publisher_id, publication_year, edition, language, pages, " +
            "description, price, added_date, book_status " +
            "FROM books " +
            "WHERE (title LIKE ? OR isbn LIKE ?) " +
            "AND book_status <> 'ARCHIVED' " +
            "ORDER BY title";

    private static final String FIND_ALL =
            "SELECT book_id, isbn, title, subtitle, category_id, " +
            "publisher_id, publication_year, edition, language, pages, " +
            "description, price, added_date, book_status " +
            "FROM books " +
            "ORDER BY title";

    public Optional<Book> findById(int bookId) {

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(FIND_BY_ID)) {

            statement.setInt(1, bookId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to find book with ID: " + bookId,
                    e
            );
        }

        return Optional.empty();
    }

    public List<Book> search(String keyword) {

        List<Book> books = new ArrayList<>();

        if (keyword == null || keyword.isBlank()) {
            return books;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(SEARCH_BOOKS)) {

            String searchPattern = "%" + keyword.trim() + "%";

            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    books.add(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to search books.",
                    e
            );
        }

        return books;
    }

    public List<Book> findAll() {

        List<Book> books = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(FIND_ALL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                books.add(mapRow(resultSet));
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to retrieve books.",
                    e
            );
        }

        return books;
    }

    private Book mapRow(ResultSet resultSet) throws SQLException {

        Book book = new Book();

        book.setBookId(resultSet.getInt("book_id"));
        book.setIsbn(resultSet.getString("isbn"));
        book.setTitle(resultSet.getString("title"));
        book.setSubtitle(resultSet.getString("subtitle"));

        book.setCategoryId(
                resultSet.getInt("category_id")
        );

        int publisherId = resultSet.getInt("publisher_id");
        if (!resultSet.wasNull()) {
            book.setPublisherId(publisherId);
        }

        int publicationYear = resultSet.getInt("publication_year");
        if (!resultSet.wasNull()) {
            book.setPublicationYear(publicationYear);
        }

        book.setEdition(
                resultSet.getString("edition")
        );

        book.setLanguage(
                resultSet.getString("language")
        );

        int pages = resultSet.getInt("pages");
        if (!resultSet.wasNull()) {
            book.setPages(pages);
        }

        book.setDescription(
                resultSet.getString("description")
        );

        book.setPrice(
                resultSet.getBigDecimal("price")
        );

        if (resultSet.getDate("added_date") != null) {
            book.setAddedDate(
                    resultSet.getDate("added_date").toLocalDate()
            );
        }

        book.setBookStatus(
                resultSet.getString("book_status")
        );

        return book;
    }
}

