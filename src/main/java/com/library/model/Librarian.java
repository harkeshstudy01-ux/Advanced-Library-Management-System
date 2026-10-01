package com.library.model;

import java.time.LocalDate;

public class Librarian extends User {

    private int librarianId;
    private String employeeCode;
    private String department;
    private LocalDate joiningDate;
    private String shift;

    public Librarian() {
        super();
    }

    public Librarian(int userId,
                     String username,
                     String passwordHash,
                     String fullName,
                     String email,
                     String phone,
                     String role,
                     String accountStatus,
                     int librarianId,
                     String employeeCode,
                     String department,
                     LocalDate joiningDate,
                     String shift) {

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

        this.librarianId = librarianId;
        this.employeeCode = employeeCode;
        this.department = department;
        this.joiningDate = joiningDate;
        this.shift = shift;
    }

    public int getLibrarianId() {
        return librarianId;
    }

    public void setLibrarianId(int librarianId) {
        this.librarianId = librarianId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    @Override
    public String toString() {

        return "Librarian{" +
                "librarianId=" + librarianId +
                ", userId=" + getUserId() +
                ", username='" + getUsername() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", employeeCode='" + employeeCode + '\'' +
                ", department='" + department + '\'' +
                ", joiningDate=" + joiningDate +
                ", shift='" + shift + '\'' +
                '}';
    }
}

