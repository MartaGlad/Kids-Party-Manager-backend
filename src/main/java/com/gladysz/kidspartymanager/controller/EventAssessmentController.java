package com.gladysz.kidspartymanager.controller;

import com.gladysz.kidspartymanager.domain.EventAssessment;
import com.gladysz.kidspartymanager.dto.*;
import com.gladysz.kidspartymanager.mapper.EventAssessmentMapper;
import com.gladysz.kidspartymanager.service.EventAssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;



@RestController
@RequestMapping("/api/v1/reservations/{reservationId}/assessment")
@RequiredArgsConstructor
public class EventAssessmentController {

    private final EventAssessmentService eventAssessmentService;
    private final EventAssessmentMapper eventAssessmentMapper;


    @PostMapping
    public ResponseEntity<EventAssessmentResponseDto> addEventAssessment (
            @PathVariable Long reservationId,
            @Valid @RequestBody EventAssessmentCreateDto eventAssessmentCreateDto) {

        EventAssessment eventAssessment = eventAssessmentService
                .createNewEventAssessment(reservationId, eventAssessmentCreateDto);


        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri())
                .body(eventAssessmentMapper.mapToEventAssessmentResponseDto(eventAssessment));
    }


    @GetMapping()
    public ResponseEntity<EventAssessmentResponseDto> getEventAssessmentByReservationId (
            @PathVariable Long reservationId) {

        EventAssessment assessment = eventAssessmentService.getByReservationId(reservationId);

        return ResponseEntity
                .ok(eventAssessmentMapper.mapToEventAssessmentResponseDto(assessment));
    }


    @PatchMapping()
    public ResponseEntity<EventAssessmentResponseDto> updateEventAssessmentByReservationId (
            @PathVariable Long reservationId,
            @Valid @RequestBody EventAssessmentUpdateDto eventAssessmentUpdateDto) {

        EventAssessment eventAssessmentUpdated = eventAssessmentService
                .updateByReservationId(reservationId, eventAssessmentUpdateDto);

        return ResponseEntity
                .ok(eventAssessmentMapper.mapToEventAssessmentResponseDto(eventAssessmentUpdated));

    }


    @DeleteMapping()
    public ResponseEntity<Void> deleteEventAssessmentByReservationId (@PathVariable Long reservationId) {

        eventAssessmentService.deleteByReservationId(reservationId);

        return ResponseEntity
                .noContent().build();
    }
}


