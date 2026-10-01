package com.library.service;

import com.library.model.Member;
import com.library.model.MembershipPlan;
import com.library.repository.MemberRepository;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service layer for member-related operations.
 *
 * Responsibilities:
 * - Find members by member ID.
 * - Find members using the logged-in user's user ID.
 * - Validate that a member exists.
 * - Validate that a member's account and membership are active.
 * - Retrieve the membership plan assigned to a member.
 * - Provide plan-based loan and fine information.
 *
 * Architecture:
 *
 * Main
 *   ↓
 * MemberController
 *   ↓
 * MemberService
 *   ├── MemberRepository
 *   └── MembershipPlanService
 *          ↓
 *     MembershipPlanRepository
 *          ↓
 *        MySQL
 */
public class MemberService {

    private final MemberRepository memberRepository;
    private final MembershipPlanService membershipPlanService;

    /**
     * Create the member service.
     */
    public MemberService() {
        this.memberRepository = new MemberRepository();
        this.membershipPlanService = new MembershipPlanService();
    }

    /**
     * Find a member using the member ID.
     *
     * @param memberId member ID
     * @return Optional containing the member if found
     */
    public Optional<Member> findById(int memberId) {

        if (memberId <= 0) {
            throw new IllegalArgumentException(
                    "Member ID must be greater than zero."
            );
        }

        return memberRepository.findById(memberId);
    }

    /**
     * Find the member associated with a logged-in user.
     *
     * users.user_id
     *       ↓
     * members.user_id
     *       ↓
     * members.member_id
     *
     * @param userId logged-in user's ID
     * @return Optional containing the corresponding member
     */
    public Optional<Member> findByUserId(int userId) {

        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "User ID must be greater than zero."
            );
        }

        return memberRepository.findByUserId(userId);
    }

    /**
     * Get a member by member ID.
     *
     * @param memberId member ID
     * @return member
     */
    public Member getMemberById(int memberId) {

        return findById(memberId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Member not found with ID: " + memberId
                        )
                );
    }

    /**
     * Get the member associated with the logged-in user.
     *
     * @param userId logged-in user's ID
     * @return corresponding member
     */
    public Member getMemberByUserId(int userId) {

        return findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No member account is associated with user ID: "
                                        + userId
                        )
                );
    }

    /**
     * Check whether a member exists.
     *
     * @param memberId member ID
     * @return true if the member exists
     */
    public boolean exists(int memberId) {

        if (memberId <= 0) {
            return false;
        }

        return memberRepository.findById(memberId).isPresent();
    }

    /**
     * Check whether the member's library membership
     * is currently active.
     *
     * @param memberId member ID
     * @return true if membership is active
     */
    public boolean isMembershipActive(int memberId) {

        Member member = getMemberById(memberId);

        return member.isMembershipActive();
    }

    /**
     * Validate that a member exists and has
     * an active membership.
     *
     * @param memberId member ID
     * @return valid member
     */
    public Member validateActiveMember(int memberId) {

        Member member = getMemberById(memberId);

        if (!member.isMembershipActive()) {
            throw new IllegalStateException(
                    "Member membership is expired or inactive."
            );
        }

        return member;
    }

    /**
     * Get the member ID belonging to a logged-in user.
     *
     * @param userId logged-in user's ID
     * @return member ID
     */
    public int getMemberIdForUser(int userId) {

        Member member = getMemberByUserId(userId);

        return member.getMemberId();
    }

    /**
     * Verify that a member ID belongs to the specified
     * logged-in user.
     *
     * This prevents a member from supplying another
     * member's ID.
     *
     * @param userId logged-in user's ID
     * @param memberId member ID being checked
     * @return true if the member belongs to the user
     */
    public boolean belongsToUser(int userId, int memberId) {

        if (userId <= 0 || memberId <= 0) {
            return false;
        }

        Optional<Member> memberOptional =
                memberRepository.findByUserId(userId);

        if (memberOptional.isEmpty()) {
            return false;
        }

        return memberOptional.get().getMemberId() == memberId;
    }

    /**
     * Validate that a member ID belongs to
     * the logged-in user.
     *
     * @param userId logged-in user's ID
     * @param memberId member ID being checked
     */
    public void validateOwnership(int userId, int memberId) {

        if (!belongsToUser(userId, memberId)) {
            throw new SecurityException(
                    "You are not authorized to access this member account."
            );
        }
    }

    // =========================================================
    // MEMBERSHIP PLAN OPERATIONS
    // =========================================================

    /**
     * Get the membership plan assigned to a member.
     *
     * Database relationship:
     *
     * members.plan_id
     *       ↓
     * membership_plans.plan_id
     *
     * @param memberId member ID
     * @return membership plan
     */
    public MembershipPlan getMembershipPlan(int memberId) {

        Member member = getMemberById(memberId);

        return membershipPlanService.getPlanById(
                member.getMembershipPlanId()
        );
    }

    /**
     * Get the maximum number of books allowed
     * for the member's current membership plan.
     *
     * Examples:
     *
     * Basic    -> 3 books
     * Standard -> 5 books
     * Premium  -> 10 books
     *
     * @param memberId member ID
     * @return maximum number of active books
     */
    public int getMaxBooksAllowed(int memberId) {

        MembershipPlan plan = getMembershipPlan(memberId);

        return plan.getMaxBooks();
    }

    /**
     * Get the loan period in days according
     * to the member's membership plan.
     *
     * Examples:
     *
     * Basic    -> 14 days
     * Standard -> 21 days
     * Premium  -> 30 days
     *
     * @param memberId member ID
     * @return loan period in days
     */
    public int getLoanPeriodDays(int memberId) {

        MembershipPlan plan = getMembershipPlan(memberId);

        return plan.getLoanPeriodDays();
    }

    /**
     * Get the fine charged per overdue day
     * according to the member's membership plan.
     *
     * Examples:
     *
     * Basic    -> ₹2.00/day
     * Standard -> ₹1.50/day
     * Premium  -> ₹1.00/day
     *
     * @param memberId member ID
     * @return fine per overdue day
     */
    public BigDecimal getFinePerDay(int memberId) {

        MembershipPlan plan = getMembershipPlan(memberId);

        return plan.getFinePerDay();
    }
}

