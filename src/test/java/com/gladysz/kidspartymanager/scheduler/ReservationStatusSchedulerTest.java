package com.gladysz.kidspartymanager.scheduler;

import com.gladysz.kidspartymanager.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationStatusSchedulerTest {

    @InjectMocks
    private ReservationStatusScheduler reservationStatusScheduler;

    @Mock
    private ReservationService reservationService;


    @Test
    void shouldUpdateReservationStatusesSuccessfully() {

        //When
        reservationStatusScheduler.updateReservationStatuses();

        //Then
        verify(reservationService).cancelExpiredReservations();
        verify(reservationService).completeFinishedReservations();
    }


    @Test
    void shouldHandleExceptionDuringReservationStatusesUpdate() {

        //Given
        doThrow(new RuntimeException()).when(reservationService).cancelExpiredReservations();

        //When
        reservationStatusScheduler.updateReservationStatuses();

        //Then
        verify(reservationService).cancelExpiredReservations();
        verify(reservationService, never()).completeFinishedReservations();
    }
}
