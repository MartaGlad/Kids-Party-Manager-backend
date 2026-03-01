package com.gladysz.kidspartymanager.service;


import com.gladysz.kidspartymanager.domain.*;
import com.gladysz.kidspartymanager.dto.ReservationCreateDto;
import com.gladysz.kidspartymanager.dto.ReservationUpdateDto;
import com.gladysz.kidspartymanager.exception.ReservationNotFoundException;
import com.gladysz.kidspartymanager.mapper.ReservationMapper;
import com.gladysz.kidspartymanager.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final EventPackageService eventPackageService;
    private final AnimatorService animatorService;
    private final OrdererService ordererService;


    public Reservation createNewReservation(final ReservationCreateDto reservationCreateDto) {

        Reservation reservation = new Reservation();

        EventPackage eventPackage = eventPackageService
                .getEventPackageById(reservationCreateDto.eventPackageId());

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


    public Reservation updateReservation(final Long id, final ReservationUpdateDto reservationUpdateDto) {

        Reservation fetchedReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

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
}

