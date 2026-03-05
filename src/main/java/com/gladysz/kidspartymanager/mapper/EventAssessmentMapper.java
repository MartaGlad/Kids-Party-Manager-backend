package com.gladysz.kidspartymanager.mapper;

import com.gladysz.kidspartymanager.domain.EventAssessment;

import com.gladysz.kidspartymanager.dto.EventAssessmentResponseDto;
import com.gladysz.kidspartymanager.dto.EventAssessmentUpdateDto;
import org.springframework.stereotype.Component;


@Component
public class EventAssessmentMapper {

    private boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }


    public void applyUpdate(
            final EventAssessment eventAssessment,
            final EventAssessmentUpdateDto eventAssessmentUpdateDto) {

        if (eventAssessmentUpdateDto.rating() != null) {
            eventAssessment.setRating(eventAssessmentUpdateDto.rating());
        }

        if (hasText(eventAssessmentUpdateDto.comment())) {
            eventAssessment.setComment(eventAssessmentUpdateDto.comment().trim());
        }
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




