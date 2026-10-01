package com.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/library_management"
                    + "?useSSL=false&serverTimezone=UTC";

    private static String username;
    private static String password;

    private static boolean configured = false;

    private DatabaseConnection() {
    }

    public static void configure(String dbUsername, String dbPassword) {

        if (dbUsername == null || dbUsername.isBlank()) {
            throw new IllegalArgumentException(
                    "Database username cannot be empty."
            );
        }

        username = dbUsername.trim();
        password = dbPassword == null ? "" : dbPassword;
        configured = true;
    }

    public static Connection getConnection() throws SQLException {

        if (!configured) {
            throw new IllegalStateException(
                    "Database is not configured. "
                            + "Configure database credentials first."
            );
        }

        return DriverManager.getConnection(
                URL,
                username,
                password
        );
    }

    public static void configureFromConsole() {

        Scanner scanner = new Scanner(System.in);

        System.out.println();
        System.out.println("========== DATABASE CONFIGURATION ==========");

        System.out.print("MySQL username: ");
        String dbUsername = scanner.nextLine().trim();

        System.out.print("MySQL password: ");
        String dbPassword = scanner.nextLine();

        configure(dbUsername, dbPassword);

        System.out.println("Database credentials configured.");
    }

    public static boolean testConnection() {

        try (Connection connection = getConnection()) {

            System.out.println("Database connection successful.");
            System.out.println(
                    "Connected to: library_management"
            );

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Database connection failed: "
                            + e.getMessage()
            );

            return false;
        }
    }
}