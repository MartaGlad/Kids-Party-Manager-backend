package com.gladysz.kidspartymanager.service;

import com.gladysz.kidspartymanager.domain.Orderer;
import com.gladysz.kidspartymanager.dto.orderer.OrdererUpdateDto;
import com.gladysz.kidspartymanager.exception.orderer.OrdererDeleteException;
import com.gladysz.kidspartymanager.exception.orderer.OrdererNotFoundException;
import com.gladysz.kidspartymanager.mapper.OrdererMapper;
import com.gladysz.kidspartymanager.repository.OrdererRepository;
import com.gladysz.kidspartymanager.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrdererServiceTest {

    @InjectMocks
    private OrdererService ordererService;

    @Mock
    private OrdererRepository ordererRepository;

    @Mock
    private OrdererMapper ordererMapper;

    @Mock
    private ReservationRepository reservationRepository;


    @Test
    void shouldCreateOrdererSuccessfully() {

        //Given
        Orderer orderer = new Orderer();
        when(ordererRepository.save(orderer)).thenReturn(orderer);

        //When
        Orderer ordererSaved = ordererService.createOrderer(orderer);

        //Then
        assertSame(orderer, ordererSaved);
        verify(ordererRepository).save(orderer);
    }


    @Test
    void getOrdererByIdShouldReturnOrdererWhenFound() {

        //Given
        Orderer orderer = new Orderer();
        orderer.setFirstName("Nancy");

        when(ordererRepository.findById(1L)).thenReturn(Optional.of(orderer));

        //When
        Orderer ordererFetched = ordererService.getOrdererById(1L);

        //Then
        assertSame(orderer, ordererFetched);
        assertEquals("Nancy", ordererFetched.getFirstName());
        verify(ordererRepository).findById(1L);
    }


    @Test
    void getOrdererByIdShouldThrowExceptionWhenOrdererNotFound() {

        //Given
        when(ordererRepository.findById(1L)).thenReturn(Optional.empty());

        //When & Then
        assertThrows(OrdererNotFoundException.class, () -> ordererService.getOrdererById(1L));
    }


    @Test
    void shouldReturnAllOrderers() {

        //Given
        Orderer orderer1 = new Orderer();
        Orderer orderer2 = new Orderer();

        when(ordererRepository.findAll()).thenReturn(List.of(orderer1, orderer2));

        //When
        List<Orderer> orderers = ordererService.getAllOrderers();

        //Then
        assertNotNull(orderers);
        assertEquals(2, orderers.size());
        verify(ordererRepository).findAll();
    }


    @Test
    void shouldUpdateOrdererSuccessfully() {

        //Given
        Orderer o = new Orderer();
        OrdererUpdateDto dto = new OrdererUpdateDto("Nancy", "Thompson",
                "nancyT@gmail.com", "3453123");

        when(ordererRepository.findById(1L)).thenReturn(Optional.of(o));

        doAnswer(i -> {
            Orderer orderer = i.getArgument(0);
            OrdererUpdateDto updateDto = i.getArgument(1);

            orderer.setFirstName(updateDto.firstName());
            orderer.setLastName(updateDto.lastName());
            orderer.setEmail(updateDto.email());
            orderer.setPhone(updateDto.phone());

            return null;
        }).when(ordererMapper).applyUpdate(o, dto);

        //When
        Orderer ordererUpdated = ordererService.updateOrderer(1L, dto);

        //Then
        assertSame(o, ordererUpdated);
        assertEquals("Nancy", ordererUpdated.getFirstName());
        assertEquals("Thompson", ordererUpdated.getLastName());
        verify(ordererRepository).findById(1L);
        verify(ordererMapper).applyUpdate(o, dto);
    }


    @Test
    void shouldThrowExceptionWhenOrdererNotFoundDuringUpdate() {

        //Given
        OrdererUpdateDto dto = new OrdererUpdateDto("Nancy", "Thompson",
                "nancyT@gmail.com", "3453123");

        when(ordererRepository.findById(1L)).thenReturn(Optional.empty());

        //When & Then
        assertThrows(OrdererNotFoundException.class,
                () -> ordererService.updateOrderer(1L, dto));
        verify(ordererMapper, never()).applyUpdate(any(), any());
    }


    @Test
    void shouldDeleteOrdererByIdSuccessfully() {

        //Given
        Orderer orderer = new Orderer();
        ReflectionTestUtils.setField(orderer, "id", 1L);

        when(ordererRepository.findById(1L)).thenReturn(Optional.of(orderer));
        when(reservationRepository.existsByOrdererId(1L)).thenReturn(false);

        //When
        ordererService.deleteOrdererById(1L);

        //Then
        verify(ordererRepository).findById(1L);
        verify(reservationRepository).existsByOrdererId(1L);
        verify(ordererRepository).delete(orderer);
    }


    @Test
    void shouldThrowExceptionWhenDeletingOrdererWithReservations() {

        //Given
        Orderer orderer = new Orderer();
        ReflectionTestUtils.setField(orderer, "id", 1L);

        when(ordererRepository.findById(1L)).thenReturn(Optional.of(orderer));
        when(reservationRepository.existsByOrdererId(1L)).thenReturn(true);

        //When & Then
        assertThrows(OrdererDeleteException.class, () -> ordererService.deleteOrdererById(1L));
        verify(ordererRepository).findById(1L);
        verify(reservationRepository).existsByOrdererId(1L);
        verify(ordererRepository, never()).delete(any());
    }
}




