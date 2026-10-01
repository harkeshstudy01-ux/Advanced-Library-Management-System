package com.library.repository;

import com.library.exception.MembershipPlanException;
import com.library.model.MembershipPlan;
import com.library.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for membership_plans table.
 *
 * Handles database operations related to membership plans.
 */
public class MembershipPlanRepository {

    private static final String BASE_SELECT =
            "SELECT plan_id, plan_name, duration_months, max_books, " +
            "loan_period_days, fine_per_day, membership_fee " +
            "FROM membership_plans ";

    /**
     * Find a membership plan by its ID.
     */
    public Optional<MembershipPlan> findById(int planId) {

        String sql = BASE_SELECT + "WHERE plan_id = ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, planId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {

            throw new MembershipPlanException(
                    "Database error while finding membership plan: "
                            + planId,
                    e
            );
        }

        return Optional.empty();
    }

    /**
     * Find a membership plan by its name.
     */
    public Optional<MembershipPlan> findByName(
            String planName) {

        String sql = BASE_SELECT + "WHERE plan_name = ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, planName);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {

            throw new MembershipPlanException(
                    "Database error while finding membership plan: "
                            + planName,
                    e
            );
        }

        return Optional.empty();
    }

    /**
     * Return all membership plans.
     */
    public List<MembershipPlan> findAll() {

        String sql = BASE_SELECT + "ORDER BY plan_id";

        List<MembershipPlan> plans = new ArrayList<>();

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {
                plans.add(mapRow(resultSet));
            }

        } catch (SQLException e) {

            throw new MembershipPlanException(
                    "Database error while retrieving membership plans.",
                    e
            );
        }

        return plans;
    }

    /**
     * Convert one database row into a MembershipPlan object.
     */
    private MembershipPlan mapRow(ResultSet resultSet)
            throws SQLException {

        MembershipPlan plan = new MembershipPlan();

        plan.setPlanId(
                resultSet.getInt("plan_id")
        );

        plan.setPlanName(
                resultSet.getString("plan_name")
        );

        plan.setDurationMonths(
                resultSet.getInt("duration_months")
        );

        plan.setMaxBooks(
                resultSet.getInt("max_books")
        );

        plan.setLoanPeriodDays(
                resultSet.getInt("loan_period_days")
        );

        BigDecimal finePerDay =
                resultSet.getBigDecimal("fine_per_day");

        BigDecimal membershipFee =
                resultSet.getBigDecimal("membership_fee");

        plan.setFinePerDay(finePerDay);
        plan.setMembershipFee(membershipFee);

        return plan;
    }
}

