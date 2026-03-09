package com.gladysz.kidspartymanager.controller;

import com.gladysz.kidspartymanager.domain.Reservation;
import com.gladysz.kidspartymanager.dto.reservation.ReservationChangeStatusDto;
import com.gladysz.kidspartymanager.dto.reservation.ReservationCreateDto;
import com.gladysz.kidspartymanager.dto.reservation.ReservationResponseDto;
import com.gladysz.kidspartymanager.dto.reservation.ReservationUpdateDto;
import com.gladysz.kidspartymanager.mapper.ReservationMapper;
import com.gladysz.kidspartymanager.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;


    @PostMapping
    public ResponseEntity<ReservationResponseDto> addReservation(
            @Valid @RequestBody ReservationCreateDto reservationCreateDto) {

        Reservation reservation = reservationService.createNewReservation(reservationCreateDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(reservation.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(reservationMapper.mapToReservationResponseDto(reservation));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDto> getReservation(@PathVariable Long id) {

        return ResponseEntity
                .ok(reservationMapper.mapToReservationResponseDto(reservationService.getReservationById(id)));
    }


    @GetMapping
    public ResponseEntity<List<ReservationResponseDto>> getAllReservations() {

        return ResponseEntity
                .ok(reservationMapper.mapToReservationResponseDtoList(reservationService.getAllReservations()));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<ReservationResponseDto> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationUpdateDto reservationUpdateDto) {

        Reservation reservationUpdated = reservationService.updateReservation(id, reservationUpdateDto);

        return ResponseEntity
                .ok(reservationMapper.mapToReservationResponseDto(reservationUpdated));
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<ReservationResponseDto> changeReservationStatus(
            @PathVariable Long id,
            @Valid @RequestBody ReservationChangeStatusDto reservationChangeStatusDto) {

        Reservation reservationUpdated = reservationService
                .changeReservationStatusById(id, reservationChangeStatusDto.status());

        return ResponseEntity
                .ok(reservationMapper.mapToReservationResponseDto(reservationUpdated));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {

        reservationService.deleteReservationById(id);

        return ResponseEntity
                .noContent().build();
    }
}
