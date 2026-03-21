package com.gladysz.kidspartymanager.controller;

import com.gladysz.kidspartymanager.domain.EventPackage;
import com.gladysz.kidspartymanager.domain.Reservation;
import com.gladysz.kidspartymanager.domain.Status;
import com.gladysz.kidspartymanager.dto.reservation.*;
import com.gladysz.kidspartymanager.mapper.ReservationMapper;
import com.gladysz.kidspartymanager.service.EventPackageService;
import com.gladysz.kidspartymanager.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;
    private final EventPackageService eventPackageService;


    @PostMapping("/check-availability")
    public ResponseEntity<Boolean> checkAvailability(
            @Valid @RequestBody ReservationCheckAvailabilityDto reservationCheckAvailabilityDto) {

        EventPackage eventPackage = eventPackageService
                .getEventPackageById(reservationCheckAvailabilityDto.eventPackageId());

        return ResponseEntity.ok(reservationService.isReservationTermAvailable(
                reservationCheckAvailabilityDto.eventDateTime(),
                eventPackage
        ));
    }


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


    @GetMapping()
    public ResponseEntity<List<ReservationSummaryDto>> getReservations(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {

        return ResponseEntity
                    .ok(reservationMapper.mapToReservationSummaryDtoList(
                            reservationService.getReservations(status, from, to)));
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
}
