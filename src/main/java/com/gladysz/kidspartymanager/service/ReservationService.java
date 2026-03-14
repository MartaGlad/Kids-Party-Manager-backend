package com.gladysz.kidspartymanager.service;


import com.gladysz.kidspartymanager.domain.*;
import com.gladysz.kidspartymanager.domain.pricing.PricingResult;
import com.gladysz.kidspartymanager.dto.reservation.ReservationCreateDto;
import com.gladysz.kidspartymanager.dto.reservation.ReservationUpdateDto;
import com.gladysz.kidspartymanager.exception.animator.AnimatorInactiveException;
import com.gladysz.kidspartymanager.exception.reservation.ReservationNotFoundException;
import com.gladysz.kidspartymanager.exception.reservation.ReservationOverlapException;
import com.gladysz.kidspartymanager.exception.reservation.ReservationUpdateException;
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
    private final PricingService pricingService;


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

        if (!animator.isActive()) {
            throw new AnimatorInactiveException(animator.getId());
        }

        Orderer orderer = ordererService
                .getOrdererById(reservationCreateDto.ordererId());

        PricingResult pricingResult = pricingService.getPricingResult(
                eventPackage,
                reservationCreateDto.childrenCount(),
                reservationCreateDto.eventDateTime().toLocalDate());

        reservation.setEventPackage(eventPackage);
        reservation.setAnimator(animator);
        reservation.setOrderer(orderer);
        reservation.setEventDateTime(reservationCreateDto.eventDateTime());
        reservation.setChildrenCount(reservationCreateDto.childrenCount());
        reservation.setBirthdayChildAge(reservationCreateDto.birthdayChildAge());
        reservation.setHolidayFlag(pricingResult.holiday());
        reservation.setPriceSnapshot(pricingResult.finalPricePln());

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

        if (!fetchedReservation.canBeUpdated()) {
            throw new ReservationUpdateException();
        }

        EventPackage newEventPackage = reservationUpdateDto.eventPackageId() != null
                ? eventPackageService.getEventPackageById(reservationUpdateDto.eventPackageId())
                : fetchedReservation.getEventPackage();

        LocalDateTime newReservationStart = reservationUpdateDto.eventDateTime() != null
                ? reservationUpdateDto.eventDateTime()
                : fetchedReservation.getEventDateTime();

        validateReservationOverlap(newReservationStart, newEventPackage, id);

        if (reservationUpdateDto.eventPackageId() != null) {
            fetchedReservation.setEventPackage(newEventPackage);
        }

        reservationMapper.applyUpdate(fetchedReservation, reservationUpdateDto);

        PricingResult pricingResult = pricingService.getPricingResult(
                fetchedReservation.getEventPackage(),
                fetchedReservation.getChildrenCount(),
                fetchedReservation.getEventDateTime().toLocalDate());

        fetchedReservation.setHolidayFlag(pricingResult.holiday());
        fetchedReservation.setPriceSnapshot(pricingResult.finalPricePln());

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

