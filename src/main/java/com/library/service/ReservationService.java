package com.library.service;

import com.library.model.Reservation;
import com.library.repository.BookRepository;
import com.library.repository.ReservationRepository;
import com.library.repository.MemberRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationService {

    private static final int MAX_ACTIVE_RESERVATIONS = 3;
    private static final int RESERVATION_VALIDITY_DAYS = 7;

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    public ReservationService() {
        this.reservationRepository = new ReservationRepository();
        this.memberRepository = new MemberRepository();
        this.bookRepository = new BookRepository();
    }

    public Reservation reserveBook(int memberId, int bookId) {

        validateMember(memberId);
        validateBook(bookId);

        int activeReservations =
                reservationRepository.countActiveReservations(memberId);

        if (activeReservations >= MAX_ACTIVE_RESERVATIONS) {

            throw new IllegalStateException(
                    "Maximum active reservations reached: "
                            + MAX_ACTIVE_RESERVATIONS
            );
        }

        List<Reservation> existingReservations =
                reservationRepository.findByMemberId(memberId);

        for (Reservation reservation : existingReservations) {

            if (reservation.getBookId() == bookId
                    && reservation.isActive()
                    && !reservation.isExpired()) {

                throw new IllegalStateException(
                        "You already have an active reservation for this book."
                );
            }
        }

        /*
         * Database columns:
         *
         * reservation_date -> DATETIME
         * expiry_date      -> DATETIME
         */
        LocalDateTime reservationDate =
                LocalDateTime.now();

        LocalDateTime expiryDate =
                reservationDate.plusDays(
                        RESERVATION_VALIDITY_DAYS
                );

        /*
         * Priority is handled by the ReservationRepository.
         * The repository can assign the next priority number
         * for the selected book.
         */
        int reservationId =
                reservationRepository.createReservation(
                        memberId,
                        bookId,
                        reservationDate,
                        expiryDate
                );

        return reservationRepository.findById(reservationId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Reservation was created but could not be retrieved."
                        ));
    }

    public boolean cancelReservation(int reservationId) {

        if (reservationId <= 0) {

            throw new IllegalArgumentException(
                    "Reservation ID must be greater than zero."
            );
        }

        return reservationRepository.cancelReservation(
                reservationId
        );
    }

    public List<Reservation> getMemberReservations(int memberId) {

        validateMember(memberId);

        return reservationRepository.findByMemberId(memberId);
    }

    public List<Reservation> getBookReservations(int bookId) {

        validateBook(bookId);

        return reservationRepository.findActiveByBookId(bookId);
    }

    public void expireReservation(int reservationId) {

        if (reservationId <= 0) {

            throw new IllegalArgumentException(
                    "Reservation ID must be greater than zero."
            );
        }

        reservationRepository.expireReservation(
                reservationId
        );
    }

    private void validateMember(int memberId) {

        if (memberId <= 0) {

            throw new IllegalArgumentException(
                    "Member ID must be greater than zero."
            );
        }

        memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Member not found with ID: " + memberId
                        ));
    }

    private void validateBook(int bookId) {

        if (bookId <= 0) {

            throw new IllegalArgumentException(
                    "Book ID must be greater than zero."
            );
        }

        bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Book not found with ID: " + bookId
                        ));
    }
}

