package com.gladysz.kidspartymanager.service;

import com.gladysz.kidspartymanager.domain.EventPackage;
import com.gladysz.kidspartymanager.dto.eventpackage.EventPackagePatchDto;
import com.gladysz.kidspartymanager.dto.eventpackage.EventPackagePutDto;
import com.gladysz.kidspartymanager.exception.eventpackage.EventPackageDeleteException;
import com.gladysz.kidspartymanager.exception.eventpackage.EventPackageNotFoundException;
import com.gladysz.kidspartymanager.mapper.EventPackageMapper;
import com.gladysz.kidspartymanager.repository.EventPackageRepository;
import com.gladysz.kidspartymanager.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventPackageServiceTest {

    @InjectMocks
    private EventPackageService eventPackageService;

    @Mock
    private EventPackageRepository eventPackageRepository;

    @Mock
    private EventPackageMapper eventPackageMapper;

    @Mock
    private ReservationRepository reservationRepository;


    @Test
    void shouldCreateEventPackageSuccessfully() {

        //Given
        EventPackage eventPackage = new EventPackage();
        when(eventPackageRepository.save(eventPackage)).thenReturn(eventPackage);

        //When
        EventPackage eventPackageSaved = eventPackageService.createEventPackage(eventPackage);

        //Then
        assertSame(eventPackage, eventPackageSaved);
        verify(eventPackageRepository).save(eventPackage);
    }


    @Test
    void getEventPackageByIdShouldReturnEventPackageWhenFound() {

        //Given
        EventPackage eventPackage = new EventPackage();
        eventPackage.setName("Experimental Workshops");

        when(eventPackageRepository.findById(1L)).thenReturn(Optional.of(eventPackage));

        //When
        EventPackage eventPackageFetched = eventPackageService.getEventPackageById(1L);

        //Then
        assertSame(eventPackage, eventPackageFetched);
        assertEquals("Experimental Workshops", eventPackageFetched.getName());
        verify(eventPackageRepository).findById(1L);
    }


    @Test
    void getEventPackageByIdShouldThrowExceptionWhenNotFound() {

        //Given
        when(eventPackageRepository.findById(1L)).thenReturn(Optional.empty());

        //When & Then
        assertThrows(EventPackageNotFoundException.class,
                () ->  eventPackageService.getEventPackageById(1L));
    }


    @Test
    void shouldReturnAllEventPackages() {

        //Given
        EventPackage e1 = new EventPackage();
        e1.setName("Experimental Workshops");
        EventPackage e2 = new EventPackage();
        e2.setName("Underwater World");

        when(eventPackageRepository.findAll()).thenReturn(List.of(e1, e2));

        //When
        List<EventPackage> eventPackages = eventPackageService.getAllEventPackages();

        //Then
        assertNotNull(eventPackages);
        assertEquals(2, eventPackages.size());
        verify(eventPackageRepository).findAll();
    }


    @Test
    void shouldUpdatePatchEventPackageSuccessfully() {

        //Given
        EventPackage e = new EventPackage();
        EventPackagePatchDto dto = new EventPackagePatchDto("Underwater World",
                null, null, 13, null);

        when(eventPackageRepository.findById(1L)).thenReturn(Optional.of(e));

        doAnswer(i -> {
            EventPackage eventPackage = i.getArgument(0);
            EventPackagePatchDto patchDto = i.getArgument(1);

            eventPackage.setName(patchDto.name());
            eventPackage.setMaxChildrenCount(patchDto.maxChildrenCount());

            return null;
        }).when(eventPackageMapper).applyUpdatePatch(e, dto);

        //When
        EventPackage eventPackageUpdated = eventPackageService.updatePatchEventPackage(1L, dto);

        //Then
        assertSame(e, eventPackageUpdated);
        assertEquals("Underwater World", eventPackageUpdated.getName());
        assertEquals(13, eventPackageUpdated.getMaxChildrenCount());
        verify(eventPackageRepository).findById(1L);
        verify(eventPackageMapper).applyUpdatePatch(e, dto);
    }


    @Test
    void shouldThrowExceptionWhenEventPackageNotFoundDuringUpdatePatch() {

        //Given
        EventPackagePatchDto dto = new EventPackagePatchDto("Underwater World",
                null, null, 13, null);

        when(eventPackageRepository.findById(1L)).thenReturn(Optional.empty());

        //When & Then
        assertThrows(EventPackageNotFoundException.class,
                () ->  eventPackageService.updatePatchEventPackage(1L, dto));
        verify(eventPackageMapper, never()).applyUpdatePatch(any(), any());
    }


    @Test
    void shouldUpdatePutEventPackageSuccessfully() {

        //Given
        EventPackage e = new EventPackage();
        EventPackagePutDto dto = new EventPackagePutDto("Underwater World",
                "description", new BigDecimal("1500"), 13, 2);

        when(eventPackageRepository.findById(1L)).thenReturn(Optional.of(e));

        doAnswer(i -> {
            EventPackage eventPackage = i.getArgument(0);
            EventPackagePutDto putDto = i.getArgument(1);

            eventPackage.setName(putDto.name());
            eventPackage.setDescription(putDto.description());
            eventPackage.setBasePrice(putDto.basePrice());
            eventPackage.setMaxChildrenCount(putDto.maxChildrenCount());
            eventPackage.setDurationHr(putDto.durationHr());

            return null;
        }).when(eventPackageMapper).applyUpdatePut(e, dto);

        //When
        EventPackage eventPackageUpdated = eventPackageService.updatePutEventPackage(1L, dto);

        //Then
        assertSame(e, eventPackageUpdated);
        assertEquals("Underwater World", eventPackageUpdated.getName());
        assertEquals("description", eventPackageUpdated.getDescription());
        assertEquals(new BigDecimal("1500"), eventPackageUpdated.getBasePrice());
        assertEquals(13, eventPackageUpdated.getMaxChildrenCount());
        assertEquals(2, eventPackageUpdated.getDurationHr());
        verify(eventPackageRepository).findById(1L);
        verify(eventPackageMapper).applyUpdatePut(e, dto);
    }


    @Test
    void shouldThrowExceptionWhenEventPackageNotFoundDuringUpdatePut() {

        //Given
        EventPackagePutDto dto = new EventPackagePutDto("Underwater World",
                "description", new BigDecimal("1500"), 13, 2);

        when(eventPackageRepository.findById(1L)).thenReturn(Optional.empty());

        //When & Then
        assertThrows(EventPackageNotFoundException.class,
                () ->  eventPackageService.updatePutEventPackage(1L, dto));
        verify(eventPackageMapper, never()).applyUpdatePut(any(), any());
    }


    @Test
    void shouldDeleteEventPackageByIdSuccessfully() {

        //Given
        EventPackage eventPackage = new EventPackage();
        ReflectionTestUtils.setField(eventPackage, "id", 1L);

        when(eventPackageRepository.findById(1L)).thenReturn(Optional.of(eventPackage));
        when(reservationRepository.existsByEventPackageId(1L)).thenReturn(false);

        //When
        eventPackageService.deleteEventPackageById(1L);

        //Then
        verify(eventPackageRepository).findById(1L);
        verify(reservationRepository).existsByEventPackageId(1L);
        verify(eventPackageRepository).delete(eventPackage);
    }


    @Test
    void shouldThrowExceptionWhenDeletingEventPackageWithReservations() {

        //Given
        EventPackage eventPackage = new EventPackage();
        ReflectionTestUtils.setField(eventPackage, "id", 1L);

        when(eventPackageRepository.findById(1L)).thenReturn(Optional.of(eventPackage));
        when(reservationRepository.existsByEventPackageId(1L)).thenReturn(true);

        //When & Then
        assertThrows(EventPackageDeleteException.class,
                () -> eventPackageService.deleteEventPackageById(1L));
        verify(eventPackageRepository).findById(1L);
        verify(reservationRepository).existsByEventPackageId(1L);
        verify(eventPackageRepository, never()).delete(any());
    }
}
