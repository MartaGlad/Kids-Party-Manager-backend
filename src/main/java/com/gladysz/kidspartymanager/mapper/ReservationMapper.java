package com.gladysz.kidspartymanager.mapper;

import com.gladysz.kidspartymanager.domain.Reservation;
import com.gladysz.kidspartymanager.dto.ReservationResponseDto;
import com.gladysz.kidspartymanager.dto.ReservationUpdateDto;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReservationMapper {

    public void applyUpdate(
            final Reservation reservation,
            final ReservationUpdateDto reservationUpdateDto) {

        if (reservationUpdateDto.eventDateTime() != null) {
            reservation.setEventDateTime(reservationUpdateDto.eventDateTime());
        }

        if (reservationUpdateDto.childrenCount() != null) {
            reservation.setChildrenCount(reservationUpdateDto.childrenCount());
        }

        if (reservationUpdateDto.birthdayChildAge() != null) {
            reservation.setBirthdayChildAge(reservationUpdateDto.birthdayChildAge());
        }
    }


    public ReservationResponseDto mapToReservationResponseDto(final Reservation reservation) {

        return new ReservationResponseDto (
                reservation.getId(),
                reservation.getEventPackage().getId(),
                reservation.getAnimator().getId(),
                reservation.getOrderer().getId(),
                reservation.getEventDateTime(),
                reservation.getChildrenCount(),
                reservation.getBirthdayChildAge(),
                reservation.getPriceSnapshot(),
                reservation.getCreatedAt()
        );
    }


    public List<ReservationResponseDto> mapToReservationResponseDtoList(
            final List<Reservation> reservations) {

        return reservations.stream()
                .map(this::mapToReservationResponseDto)
                .collect(Collectors.toList());
    }
}
