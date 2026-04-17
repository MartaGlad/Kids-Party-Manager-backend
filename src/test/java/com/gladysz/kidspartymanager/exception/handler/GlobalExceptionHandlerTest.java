package com.gladysz.kidspartymanager.exception.handler;

import com.gladysz.kidspartymanager.exception.externalapi.ExternalApiException;
import com.gladysz.kidspartymanager.exception.reservation.ReservationOverlapException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerTest {

    @Test
    void shouldHandleConflictException() {

        //Given
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        ReservationOverlapException exception = new ReservationOverlapException();

        //When
        ResponseEntity<String> response = globalExceptionHandler.handleConflictException(exception);

        //Then
        assertEquals(409, response.getStatusCode().value());
        assertEquals("This reservation time conflicts with already scheduled event.", response.getBody());
    }


    @Test
    void shouldHandleExternalApiException() {

        //Given
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        ExternalApiException exception = new ExternalApiException("External API error");

        //When
        ResponseEntity<String> response = globalExceptionHandler.handleExternalApiException(exception);

        //Then
        assertEquals(502, response.getStatusCode().value());
        assertEquals("External API error", response.getBody());
    }


    @Test
    void shouldHandleInternalServerErrorException() {

        //Given
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        RuntimeException exception = new RuntimeException("Error");

        //When
        ResponseEntity<String> e = globalExceptionHandler.handleInternalServerErrorException(exception);

        //Then
        assertEquals(500, e.getStatusCode().value());
        assertEquals("Internal server error", e.getBody());
    }
}
