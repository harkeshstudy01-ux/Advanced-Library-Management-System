package com.library.service;

import com.library.exception.MembershipException;
import com.library.exception.ValidationException;
import com.library.model.MembershipPlan;
import com.library.repository.MembershipPlanRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for membership-plan operations.
 *
 * Responsibilities:
 * - Validate membership-plan requests.
 * - Retrieve membership plans.
 * - Provide plan rules to other services.
 * - Convert repository failures into domain-specific exceptions.
 */
public class MembershipPlanService {

    private final MembershipPlanRepository membershipPlanRepository;

    public MembershipPlanService() {
        this.membershipPlanRepository =
                new MembershipPlanRepository();
    }

    public Optional<MembershipPlan> findById(int planId) {

        if (planId <= 0) {
            throw new ValidationException(
                    "Membership plan ID must be greater than zero."
            );
        }

        try {
            return membershipPlanRepository.findById(planId);
        } catch (RuntimeException e) {
            throw new MembershipException(
                    "Unable to retrieve membership plan: "
                            + planId,
                    e
            );
        }
    }

    public MembershipPlan getPlanById(int planId) {

        return findById(planId)
                .orElseThrow(() ->
                        new MembershipException(
                                "Membership plan not found with ID: "
                                        + planId
                        )
                );
    }

    public Optional<MembershipPlan> findByName(
            String planName) {

        if (planName == null || planName.isBlank()) {
            throw new ValidationException(
                    "Membership plan name cannot be empty."
            );
        }

        try {
            return membershipPlanRepository.findByName(
                    planName.trim()
            );
        } catch (RuntimeException e) {
            throw new MembershipException(
                    "Unable to retrieve membership plan: "
                            + planName,
                    e
            );
        }
    }

    public MembershipPlan getPlanByName(
            String planName) {

        return findByName(planName)
                .orElseThrow(() ->
                        new MembershipException(
                                "Membership plan not found: "
                                        + planName
                        )
                );
    }

    public List<MembershipPlan> getAllPlans() {

        try {
            return membershipPlanRepository.findAll();
        } catch (RuntimeException e) {
            throw new MembershipException(
                    "Unable to retrieve membership plans.",
                    e
            );
        }
    }

    public boolean exists(int planId) {

        if (planId <= 0) {
            return false;
        }

        try {
            return membershipPlanRepository
                    .findById(planId)
                    .isPresent();
        } catch (RuntimeException e) {
            throw new MembershipException(
                    "Unable to verify membership plan: "
                            + planId,
                    e
            );
        }
    }

    public int getMaxBooks(int planId) {

        MembershipPlan plan = getPlanById(planId);

        return plan.getMaxBooks();
    }

    public int getLoanPeriodDays(int planId) {

        MembershipPlan plan = getPlanById(planId);

        return plan.getLoanPeriodDays();
    }

    public BigDecimal getFinePerDay(int planId) {

        MembershipPlan plan = getPlanById(planId);

        return plan.getFinePerDay();
    }
}

