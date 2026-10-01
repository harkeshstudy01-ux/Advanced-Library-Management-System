package com.library.repository;

import com.library.model.Member;
import com.library.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class MemberRepository {

    private static final String BASE_SELECT =
            "SELECT m.member_id, m.user_id, m.plan_id, " +
            "m.membership_number, m.registration_date, m.expiry_date, " +
            "m.occupation, m.address, m.date_of_birth, " +
            "m.membership_status, " +
            "u.username, u.password_hash, u.full_name, u.email, " +
            "u.phone, u.role, u.account_status " +
            "FROM members m " +
            "JOIN users u ON m.user_id = u.user_id ";

    private static final String FIND_BY_ID =
            BASE_SELECT +
            "WHERE m.member_id = ?";

    private static final String FIND_BY_USER_ID =
            BASE_SELECT +
            "WHERE m.user_id = ?";

    public Optional<Member> findById(int memberId) {

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(FIND_BY_ID)) {

            statement.setInt(1, memberId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to find member: " + memberId,
                    e
            );
        }

        return Optional.empty();
    }

    public Optional<Member> findByUserId(int userId) {

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(FIND_BY_USER_ID)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to find member for user: " + userId,
                    e
            );
        }

        return Optional.empty();
    }

    private Member mapRow(ResultSet resultSet)
            throws SQLException {

        Member member = new Member();

        member.setMemberId(
                resultSet.getInt("member_id")
        );

        member.setUserId(
                resultSet.getInt("user_id")
        );

        member.setMembershipPlanId(
                resultSet.getInt("plan_id")
        );

        member.setMembershipNumber(
                resultSet.getString("membership_number")
        );

        if (resultSet.getDate("registration_date") != null) {
            member.setRegistrationDate(
                    resultSet.getDate("registration_date")
                            .toLocalDate()
            );
        }

        if (resultSet.getDate("expiry_date") != null) {
            member.setExpiryDate(
                    resultSet.getDate("expiry_date")
                            .toLocalDate()
            );
        }

        member.setOccupation(
                resultSet.getString("occupation")
        );

        member.setAddress(
                resultSet.getString("address")
        );

        if (resultSet.getDate("date_of_birth") != null) {
            member.setDateOfBirth(
                    resultSet.getDate("date_of_birth")
                            .toLocalDate()
            );
        }

        member.setMembershipStatus(
                resultSet.getString("membership_status")
        );

        member.setUsername(
                resultSet.getString("username")
        );

        member.setPasswordHash(
                resultSet.getString("password_hash")
        );

        member.setFullName(
                resultSet.getString("full_name")
        );

        member.setEmail(
                resultSet.getString("email")
        );

        member.setPhone(
                resultSet.getString("phone")
        );

        member.setRole(
                resultSet.getString("role")
        );

        member.setAccountStatus(
                resultSet.getString("account_status")
        );

        return member;
    }
}

