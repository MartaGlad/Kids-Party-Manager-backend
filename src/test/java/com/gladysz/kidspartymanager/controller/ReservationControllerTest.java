package com.gladysz.kidspartymanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gladysz.kidspartymanager.domain.*;
import com.gladysz.kidspartymanager.dto.reservation.ReservationCheckAvailabilityDto;
import com.gladysz.kidspartymanager.dto.reservation.ReservationCreateDto;
import com.gladysz.kidspartymanager.dto.reservation.ReservationResponseDto;
import com.gladysz.kidspartymanager.dto.reservation.ReservationSummaryDto;
import com.gladysz.kidspartymanager.exception.reservation.ReservationNotFoundException;
import com.gladysz.kidspartymanager.exception.reservation.ReservationTimeException;
import com.gladysz.kidspartymanager.mapper.ReservationMapper;
import com.gladysz.kidspartymanager.service.EventPackageService;
import com.gladysz.kidspartymanager.service.ReservationService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;


@WebMvcTest(ReservationController.class)
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private ReservationMapper reservationMapper;

    @MockitoBean
    private EventPackageService eventPackageService;


    @Test
    void shouldReturnTrueWhenReservationTermIsAvailable() throws Exception {

        //Given
        LocalDateTime eventDateTime = LocalDateTime.of(2026, 1, 10, 12, 0, 0);

        EventPackage eventPackage = new EventPackage();
        eventPackage.setDurationHr(3);

        ReservationCheckAvailabilityDto dto = new ReservationCheckAvailabilityDto( 1L, eventDateTime);

        when(eventPackageService.getEventPackageById(1L)).thenReturn(eventPackage);
        when(reservationService.isReservationTermAvailable(dto.eventDateTime(), eventPackage)).thenReturn(true);
        String jsonContent = objectMapper.writeValueAsString(dto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/reservations/check-availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("true"));

        verify(eventPackageService).getEventPackageById(1L);
        verify(reservationService).isReservationTermAvailable(dto.eventDateTime(), eventPackage);
    }


    @Test
    void shouldAddReservationSuccessfully() throws Exception {

        //Given
        LocalDateTime eventDateTime = LocalDateTime.of(2026, 1, 10, 12, 0, 0);
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 5, 12, 0, 0);

        ReservationCreateDto createDto = new ReservationCreateDto(
                1L, 1L, 1L, eventDateTime, 5, 6);

        Reservation savedReservation = new Reservation();
        ReflectionTestUtils.setField(savedReservation, "id", 5L);

        ReservationResponseDto responseDto = new ReservationResponseDto(
                5L, 1L, 1L, 1L,
                eventDateTime, false, 5, 6, Status.NEW, new BigDecimal("1000.00"), createdAt);

        when(reservationService.createNewReservation(any(ReservationCreateDto.class))).thenReturn(savedReservation);
        when(reservationMapper.mapToReservationResponseDto(savedReservation)).thenReturn(responseDto);

        String jsonContent = objectMapper.writeValueAsString(createDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", containsString("/api/v1/reservations/" + savedReservation.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(5L));

        verify(reservationService).createNewReservation(any(ReservationCreateDto.class));
        verify(reservationMapper).mapToReservationResponseDto(savedReservation);
    }


    @Test
    void shouldReturnBadRequestWhenCreateDtoIsInvalid() throws Exception {

        //Given
        LocalDateTime eventDateTime = LocalDateTime.of(2026, 1, 10, 12, 0, 0);

        ReservationCreateDto createDto = new ReservationCreateDto(
                null, 1L, 1L, eventDateTime, 5, 6);

        String jsonContent = objectMapper.writeValueAsString(createDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verifyNoInteractions(reservationService);
        verifyNoInteractions(reservationMapper);
    }


    @Test
    void shouldReturnBadRequestWhenReservationTimeIsInvalid() throws Exception {

        //Given
        LocalDateTime eventDateTime = LocalDateTime.of(2026, 1, 10, 6, 0, 0);

        ReservationCreateDto createDto = new ReservationCreateDto(
                1L, 1L, 1L, eventDateTime, 5, 6);

        String jsonContent = objectMapper.writeValueAsString(createDto);

        doThrow(new ReservationTimeException()).when(reservationService)
                .createNewReservation(any(ReservationCreateDto.class));

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(reservationService).createNewReservation(any(ReservationCreateDto.class));
        verifyNoInteractions(reservationMapper);
    }


    @Test
    void shouldGetReservationByIdSuccessfully() throws Exception {

            //Given
            LocalDateTime eventDateTime = LocalDateTime.of(2026, 1, 10, 12, 0, 0);
            LocalDateTime createdAt = LocalDateTime.of(2026, 1, 5, 12, 0, 0);

            Reservation r = new Reservation();
            ReflectionTestUtils.setField(r, "id", 1L);
            r.setEventDateTime(eventDateTime);

            ReservationResponseDto responseDto = new ReservationResponseDto(
                1L, 1L, 1L, 1L, eventDateTime,
                    false, 5, 6, Status.NEW, new BigDecimal("1000.00"), createdAt);

            when(reservationService.getReservationById(1L)).thenReturn(r);
            when(reservationMapper.mapToReservationResponseDto(r)).thenReturn(responseDto);

            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/v1/reservations/1")
                            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.eventDateTime").value(containsString(eventDateTime.toString())));

        verify(reservationService).getReservationById(1L);
        verify(reservationMapper).mapToReservationResponseDto(r);
    }


    @Test
    void shouldReturnNotFoundWhenReservationDoesNotExist() throws Exception {

        //Given
        doThrow(new ReservationNotFoundException(1L)).when(reservationService).getReservationById(1L);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/reservations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(reservationService).getReservationById(1L);
        verifyNoInteractions(reservationMapper);
    }


    @Test
    void shouldGetAllReservationsSuccessfully() throws Exception {

        //Given
        LocalDateTime eventDateTime = LocalDateTime.of(2026, 1, 10, 12, 0, 0);
        Reservation r = new Reservation();

        ReservationSummaryDto reservationSummaryDto = new ReservationSummaryDto(
                1L, "Magic Show", "Oleg Dance", eventDateTime,
                5, Status.NEW, new BigDecimal("1000.00"));

        when(reservationService.getReservations(null, null, null)).thenReturn(List.of(r));
        when(reservationMapper.mapToReservationSummaryDtoList(anyList())).thenReturn(List.of(reservationSummaryDto));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(1)));

        verify(reservationService).getReservations(null, null, null);
        verify(reservationMapper).mapToReservationSummaryDtoList(anyList());
    }
}





