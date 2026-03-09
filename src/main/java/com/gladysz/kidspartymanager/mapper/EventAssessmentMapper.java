package com.gladysz.kidspartymanager.mapper;

import com.gladysz.kidspartymanager.domain.EventAssessment;

import com.gladysz.kidspartymanager.dto.eventassessment.EventAssessmentPatchDto;
import com.gladysz.kidspartymanager.dto.eventassessment.EventAssessmentPutDto;
import com.gladysz.kidspartymanager.dto.eventassessment.EventAssessmentResponseDto;

import org.springframework.stereotype.Component;


@Component
public class EventAssessmentMapper {

    private boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }


    public void applyUpdatePatch(
            final EventAssessment eventAssessment,
            final EventAssessmentPatchDto eventAssessmentPatchDto) {

        if (eventAssessmentPatchDto.rating() != null) {
            eventAssessment.setRating(eventAssessmentPatchDto.rating());
        }

        if (hasText(eventAssessmentPatchDto.comment())) {
            eventAssessment.setComment(eventAssessmentPatchDto.comment().trim());
        }
    }


    public void applyUpdatePut(
            final EventAssessment eventAssessment,
            final EventAssessmentPutDto eventAssessmentPutDto) {

        eventAssessment.setRating(eventAssessmentPutDto.rating());

        String newComment =  eventAssessmentPutDto.comment();
        eventAssessment.setComment(newComment == null ? null : newComment.trim());
    }


    public EventAssessmentResponseDto mapToEventAssessmentResponseDto(
            final EventAssessment eventAssessment) {

        return new EventAssessmentResponseDto(
                eventAssessment.getId(),
                eventAssessment.getReservation().getId(),
                eventAssessment.getRating(),
                eventAssessment.getComment(),
                eventAssessment.getCreatedAt()
        );
    }
}




