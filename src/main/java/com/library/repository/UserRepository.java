package com.library.repository;

import com.library.model.User;
import com.library.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserRepository {

    public Optional<User> findByUsername(String username) {

        String sql =
                "SELECT user_id, username, password_hash, full_name, " +
                "email, phone, role, account_status " +
                "FROM users WHERE username = ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }

            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to retrieve user from database: "
                            + e.getMessage(),
                    e
            );
        }

        return Optional.empty();
    }

    public boolean existsByUsername(String username) {

        String sql =
                "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to check username in database: "
                            + e.getMessage(),
                    e
            );
        }

        return false;
    }

    private User mapRow(ResultSet resultSet) throws SQLException {

        User user = new User();

        user.setUserId(resultSet.getInt("user_id"));
        user.setUsername(resultSet.getString("username"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setFullName(resultSet.getString("full_name"));
        user.setEmail(resultSet.getString("email"));
        user.setPhone(resultSet.getString("phone"));
        user.setRole(resultSet.getString("role"));
        user.setAccountStatus(
                resultSet.getString("account_status")
        );

        return user;
    }
}

