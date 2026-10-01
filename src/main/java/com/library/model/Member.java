package com.library.model;

import java.time.LocalDate;

public class Member extends User {

    private int memberId;
    private int membershipPlanId;
    private String membershipNumber;
    private LocalDate registrationDate;
    private LocalDate expiryDate;
    private String occupation;
    private String address;
    private LocalDate dateOfBirth;
    private String membershipStatus;

    public Member() {
        super();
    }

    public Member(int userId,
                  String username,
                  String passwordHash,
                  String fullName,
                  String email,
                  String phone,
                  String role,
                  String accountStatus,
                  int memberId,
                  int membershipPlanId,
                  String membershipNumber,
                  LocalDate registrationDate,
                  LocalDate expiryDate,
                  String occupation,
                  String address,
                  LocalDate dateOfBirth,
                  String membershipStatus) {

        super(
                userId,
                username,
                passwordHash,
                fullName,
                email,
                phone,
                role,
                accountStatus
        );

        this.memberId = memberId;
        this.membershipPlanId = membershipPlanId;
        this.membershipNumber = membershipNumber;
        this.registrationDate = registrationDate;
        this.expiryDate = expiryDate;
        this.occupation = occupation;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.membershipStatus = membershipStatus;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getMembershipPlanId() {
        return membershipPlanId;
    }

    public void setMembershipPlanId(int membershipPlanId) {
        this.membershipPlanId = membershipPlanId;
    }

    public String getMembershipNumber() {
        return membershipNumber;
    }

    public void setMembershipNumber(String membershipNumber) {
        this.membershipNumber = membershipNumber;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(String membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public boolean isMembershipActive() {

        if (!"ACTIVE".equalsIgnoreCase(membershipStatus)) {
            return false;
        }

        if (expiryDate == null) {
            return false;
        }

        return !expiryDate.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {

        return "Member{" +
                "memberId=" + memberId +
                ", userId=" + getUserId() +
                ", username='" + getUsername() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", membershipPlanId=" + membershipPlanId +
                ", membershipNumber='" + membershipNumber + '\'' +
                ", registrationDate=" + registrationDate +
                ", expiryDate=" + expiryDate +
                ", occupation='" + occupation + '\'' +
                ", address='" + address + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", membershipStatus='" + membershipStatus + '\'' +
                '}';
    }
}

