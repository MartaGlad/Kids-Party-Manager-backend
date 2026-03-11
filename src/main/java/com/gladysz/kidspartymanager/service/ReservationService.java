package com.gladysz.kidspartymanager.service;


import com.gladysz.kidspartymanager.domain.*;
import com.gladysz.kidspartymanager.dto.reservation.ReservationCreateDto;
import com.gladysz.kidspartymanager.dto.reservation.ReservationUpdateDto;
import com.gladysz.kidspartymanager.exception.reservation.ReservationNotFoundException;
import com.gladysz.kidspartymanager.exception.reservation.ReservationOverlapException;
import com.gladysz.kidspartymanager.mapper.ReservationMapper;
import com.gladysz.kidspartymanager.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final EventPackageService eventPackageService;
    private final AnimatorService animatorService;
    private final OrdererService ordererService;


    private void validateReservationOverlap(
            final LocalDateTime newReservationStart,
            final EventPackage eventPackage,
            final Long excludedReservationId) {

        LocalDateTime newReservationEnd = newReservationStart
                .plusHours(eventPackage.getDurationHr())
                .plusMinutes(Reservation.CLEANUP_TIME_MINUTES);

        List<Reservation> activeReservations = reservationRepository.findActiveReservations();

        for (Reservation reservation : activeReservations) {
            if (Objects.equals(reservation.getId(), excludedReservationId)) {
                continue;
            }
            
            if (reservation.overlaps(newReservationStart, newReservationEnd)) {
                throw new ReservationOverlapException();
            }
        }
    }


    public Reservation createNewReservation(final ReservationCreateDto reservationCreateDto) {

        Reservation reservation = new Reservation();

        EventPackage eventPackage = eventPackageService
                .getEventPackageById(reservationCreateDto.eventPackageId());

        validateReservationOverlap(reservationCreateDto.eventDateTime(), eventPackage, null);

        Animator animator = animatorService
                .getAnimatorById(reservationCreateDto.animatorId());

        Orderer orderer = ordererService
                .getOrdererById(reservationCreateDto.ordererId());

        reservation.setEventPackage(eventPackage);
        reservation.setAnimator(animator);
        reservation.setOrderer(orderer);
        reservation.setEventDateTime(reservationCreateDto.eventDateTime());
        reservation.setChildrenCount(reservationCreateDto.childrenCount());
        reservation.setBirthdayChildAge(reservationCreateDto.birthdayChildAge());
        reservation.setPriceSnapshot(eventPackage.getBasePrice());

        return reservationRepository.save(reservation);
    }


    @Transactional(readOnly = true)
    public Reservation getReservationById(final Long id) {

        return reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
    }


    @Transactional(readOnly = true)
    public List<Reservation> getAllReservations() {

        return reservationRepository.findAll();
    }


    public Reservation updateReservation(
            final Long id,
            final ReservationUpdateDto reservationUpdateDto) {

        Reservation fetchedReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        LocalDateTime newReservationStart = reservationUpdateDto.eventDateTime() != null
                ? reservationUpdateDto.eventDateTime() :
                fetchedReservation.getEventDateTime();

        validateReservationOverlap(
                newReservationStart,
                fetchedReservation.getEventPackage(),
                id
        );

        reservationMapper.applyUpdate(fetchedReservation, reservationUpdateDto);

        return fetchedReservation;
    }


    public void deleteReservationById(final Long id) {

        Reservation fetchedReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        reservationRepository.delete(fetchedReservation);
    }


    public Reservation changeReservationStatusById(final Long id, final Status newStatus) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        reservation.changeStatus(newStatus);

        return reservation;
    }


    public void cancelExpiredReservations() {

        LocalDateTime now = LocalDateTime.now();

        List<Reservation> reservationsToCancel = reservationRepository
                .findExpiredNewReservations(now.minusHours(48));

        for (Reservation r : reservationsToCancel) {
            if (r.shouldBeCancelled(now)) {
                r.changeStatus(Status.CANCELLED);
            }
        }
    }


    public void completeFinishedReservations() {

        LocalDateTime now = LocalDateTime.now();

        List<Reservation> reservationsToComplete = reservationRepository
                .findConfirmedReservationsStartedBefore(now);

        for (Reservation r : reservationsToComplete) {
            if (r.shouldBeCompleted(now)) {
                r.changeStatus(Status.COMPLETED);
            }
        }
    }
}

