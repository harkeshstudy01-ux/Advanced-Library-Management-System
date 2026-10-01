package com.library.controller;

import com.library.model.Reservation;
import com.library.service.ReservationService;

import java.util.List;

public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController() {
        this.reservationService = new ReservationService();
    }

    public Reservation reserveBook(int memberId, int bookId) {
        return reservationService.reserveBook(memberId, bookId);
    }

    public void cancelReservation(int reservationId) {
        reservationService.cancelReservation(reservationId);
    }

    public void expireReservation(int reservationId) {
        reservationService.expireReservation(reservationId);
    }

    public List<Reservation> getMemberReservations(int memberId) {
        return reservationService.getMemberReservations(memberId);
    }

    public List<Reservation> getBookReservations(int bookId) {
        return reservationService.getBookReservations(bookId);
    }
}