
package com.library.controller;

import com.library.model.Member;
import com.library.service.MemberService;

import java.util.Optional;

/**
 * Controller layer for member-related operations.
 *
 * Architecture:
 * Main -> MemberController -> MemberService -> MemberRepository -> MySQL
 */
public class MemberController {

    private final MemberService memberService;

    public MemberController() {
        this.memberService = new MemberService();
    }

    /**
     * Find a member using the member ID.
     */
    public Optional<Member> getMember(int memberId) {
        return memberService.findById(memberId);
    }

    /**
     * Find the member associated with a logged-in user.
     *
     * This is the main method we will use in Main.java
     * to automatically identify the logged-in member.
     */
    public Optional<Member> getMemberByUserId(int userId) {
        return memberService.findByUserId(userId);
    }

    /**
     * Get a member by ID.
     *
     * Throws an exception if the member does not exist.
     */
    public Member getMemberById(int memberId) {
        return memberService.getMemberById(memberId);
    }

    /**
     * Get the member associated with the logged-in user.
     */
    public Member getMemberForUser(int userId) {
        return memberService.getMemberByUserId(userId);
    }

    /**
     * Get the member ID belonging to the logged-in user.
     *
     * Example:
     *
     * int memberId = memberController.getMemberIdForUser(
     *         user.getUserId()
     * );
     */
    public int getMemberIdForUser(int userId) {
        return memberService.getMemberIdForUser(userId);
    }

    /**
     * Check whether a member exists.
     */
    public boolean exists(int memberId) {
        return memberService.exists(memberId);
    }

    /**
     * Check whether a member has an active membership.
     */
    public boolean isMembershipActive(int memberId) {
        return memberService.isMembershipActive(memberId);
    }

    /**
     * Validate that the member exists and has an active membership.
     */
    public Member validateActiveMember(int memberId) {
        return memberService.validateActiveMember(memberId);
    }

    /**
     * Check whether the supplied member ID belongs to the logged-in user.
     *
     * This helps prevent one member from accessing another
     * member's account data.
     */
    public boolean belongsToUser(int userId, int memberId) {
        return memberService.belongsToUser(userId, memberId);
    }

    /**
     * Validate member ownership.
     *
     * Throws SecurityException if the member does not belong
     * to the logged-in user.
     */
    public void validateOwnership(int userId, int memberId) {
        memberService.validateOwnership(userId, memberId);
    }
}

