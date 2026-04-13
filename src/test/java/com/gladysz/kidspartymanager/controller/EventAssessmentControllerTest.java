package com.gladysz.kidspartymanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gladysz.kidspartymanager.domain.EventAssessment;
import com.gladysz.kidspartymanager.dto.eventassessment.EventAssessmentCreateDto;
import com.gladysz.kidspartymanager.dto.eventassessment.EventAssessmentResponseDto;
import com.gladysz.kidspartymanager.exception.eventassessment.EventAssessmentNotFoundException;
import com.gladysz.kidspartymanager.mapper.EventAssessmentMapper;
import com.gladysz.kidspartymanager.service.EventAssessmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;


@WebMvcTest(EventAssessmentController.class)
public class EventAssessmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventAssessmentService eventAssessmentService;

    @MockitoBean
    private EventAssessmentMapper eventAssessmentMapper;


    @Test
    void shouldAddEventAssessmentSuccessfully() throws Exception {

        //Given
        LocalDateTime createdAt = LocalDateTime.of(2026, 2, 10, 12, 0, 0);
        EventAssessmentCreateDto createDto = new EventAssessmentCreateDto(5, "Super");
        EventAssessment eventAssessmentCreated = new EventAssessment();
        EventAssessmentResponseDto responseDto = new EventAssessmentResponseDto(
                14L, 6L, 5, "Super", createdAt);

        when(eventAssessmentService.createNewEventAssessment(
                6L, createDto)).thenReturn(eventAssessmentCreated);
        when(eventAssessmentMapper.mapToEventAssessmentResponseDto(eventAssessmentCreated)).thenReturn(responseDto);

        String jsonContent = objectMapper.writeValueAsString(createDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/reservations/6/assessment")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", containsString("api/v1/reservations/6/assessment")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(14))
                .andExpect(MockMvcResultMatchers.jsonPath("$.reservationId").value(6))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rating").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.comment").value("Super"));

        verify(eventAssessmentService).createNewEventAssessment(6L, createDto);
        verify(eventAssessmentMapper).mapToEventAssessmentResponseDto(eventAssessmentCreated);
    }


    @Test
    void shouldReturnBadRequestWhenCreateDtoIsInvalid() throws Exception {

        //Given
        EventAssessmentCreateDto createDto = new EventAssessmentCreateDto(0, "Super");

        String jsonContent = objectMapper.writeValueAsString(createDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/reservations/6/assessment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(containsString("must be greater than or equal to 1")));

        verifyNoInteractions(eventAssessmentService);
        verifyNoInteractions(eventAssessmentMapper);
    }


    @Test
    void shouldGetEventAssessmentByReservationIdSuccessfully() throws Exception {

        //Given
        LocalDateTime createdAt = LocalDateTime.of(2026, 2, 10, 12, 0, 0);
        EventAssessment eventAssessment = new EventAssessment();
        EventAssessmentResponseDto responseDto = new EventAssessmentResponseDto(
                14L, 6L, 5, "Super", createdAt);

        when(eventAssessmentService.getByReservationId(6L)).thenReturn(eventAssessment);
        when(eventAssessmentMapper.mapToEventAssessmentResponseDto(eventAssessment)).thenReturn(responseDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                    .get("/api/v1/reservations/6/assessment")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(14))
                .andExpect(MockMvcResultMatchers.jsonPath("$.reservationId").value(6))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rating").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.comment").value("Super"));

        verify(eventAssessmentService).getByReservationId(6L);
        verify(eventAssessmentMapper).mapToEventAssessmentResponseDto(eventAssessment);
    }


    @Test
    void shouldReturnNotFoundWhenEventAssessmentDoesNotExist() throws Exception {

        //Given
        Long reservationId = 6L;
        doThrow(new EventAssessmentNotFoundException(reservationId))
                .when(eventAssessmentService).getByReservationId(reservationId);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/reservations/6/assessment")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(eventAssessmentService).getByReservationId(reservationId);
        verifyNoInteractions(eventAssessmentMapper);
    }


    @Test
    void shouldDeleteEventAssessmentByReservationIdSuccessfully() throws Exception {

        //Given, When & Then
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/reservations/6/assessment")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(eventAssessmentService).deleteByReservationId(6L);
    }
}
