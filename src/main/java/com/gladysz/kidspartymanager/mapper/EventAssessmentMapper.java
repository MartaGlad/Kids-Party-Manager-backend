package com.gladysz.kidspartymanager.mapper;

import com.gladysz.kidspartymanager.domain.EventAssessment;
import com.gladysz.kidspartymanager.dto.eventassessment.EventAssessmentResponseDto;

import org.springframework.stereotype.Component;


@Component
public class EventAssessmentMapper {

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




