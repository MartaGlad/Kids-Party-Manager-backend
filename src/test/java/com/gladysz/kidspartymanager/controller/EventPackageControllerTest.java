package com.gladysz.kidspartymanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gladysz.kidspartymanager.domain.EventPackage;
import com.gladysz.kidspartymanager.dto.eventpackage.EventPackageCreateDto;
import com.gladysz.kidspartymanager.dto.eventpackage.EventPackagePatchDto;
import com.gladysz.kidspartymanager.dto.eventpackage.EventPackagePutDto;
import com.gladysz.kidspartymanager.dto.eventpackage.EventPackageResponseDto;
import com.gladysz.kidspartymanager.exception.eventpackage.EventPackageNotFoundException;
import com.gladysz.kidspartymanager.mapper.EventPackageMapper;
import com.gladysz.kidspartymanager.service.EventPackageService;
import jakarta.validation.constraints.*;
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
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

@WebMvcTest(EventPackageController.class)
public class EventPackageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventPackageService eventPackageService;

    @MockitoBean
    private EventPackageMapper eventPackageMapper;


    @Test
    void shouldAddEventPackageSuccessfully() throws Exception {

        //Given
        Long eventPackageId = 1L;
        EventPackageCreateDto createDto = new EventPackageCreateDto("Unicorn show",
                "Magic dancing unicorns", new BigDecimal("1000.00"), 15,3);
        EventPackage eventPackage =  new EventPackage();
        EventPackage eventPackageCreated = new EventPackage();
        ReflectionTestUtils.setField(eventPackageCreated, "id", eventPackageId);

        EventPackageResponseDto responseDto = new EventPackageResponseDto(eventPackageId,"Unicorn show",
                "Magic dancing unicorns", new BigDecimal("1000.00"), 15,3);

        when(eventPackageMapper.mapToEventPackage(any(EventPackageCreateDto.class))).thenReturn(eventPackage);
        when(eventPackageService.createEventPackage(eventPackage)).thenReturn(eventPackageCreated);
        when(eventPackageMapper.mapToEventPackageResponseDto(eventPackageCreated)).thenReturn(responseDto);

        String jsonContent = objectMapper.writeValueAsString(createDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/event-packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string(
                        "Location", containsString("/api/v1/event-packages/" + eventPackageId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(eventPackageId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Unicorn show"));

        verify(eventPackageMapper).mapToEventPackage(any(EventPackageCreateDto.class));
        verify(eventPackageService).createEventPackage(eventPackage);
        verify(eventPackageMapper).mapToEventPackageResponseDto(eventPackageCreated);
    }


    @Test
    void shouldReturnBadRequestWhenCreatingEventPackageWithInvalidData() throws Exception {

        //Given
        EventPackageCreateDto createDto = new EventPackageCreateDto("",
                "Magic dancing unicorns", new BigDecimal("1000.00"), 15,3);

        String jsonContent = objectMapper.writeValueAsString(createDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/event-packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verifyNoInteractions(eventPackageMapper);
        verifyNoInteractions(eventPackageService);
    }


    @Test
    void shouldGetEventPackageByIdSuccessfully() throws Exception {

        //Given
        Long eventPackageId = 1L;
        EventPackage eventPackage = new EventPackage();
        EventPackageResponseDto responseDto = new EventPackageResponseDto(eventPackageId,"Unicorn show",
                "Magic dancing unicorns", new BigDecimal("1000.00"), 15,3);

        when(eventPackageService.getEventPackageById(eventPackageId)).thenReturn(eventPackage);
        when(eventPackageMapper.mapToEventPackageResponseDto(eventPackage)).thenReturn(responseDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/event-packages/" + eventPackageId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(eventPackageId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Unicorn show"));

        verify(eventPackageService).getEventPackageById(eventPackageId);
        verify(eventPackageMapper).mapToEventPackageResponseDto(any(EventPackage.class));
    }


    @Test
    void shouldReturnNotFoundWhenEventPackageDoesNotExist() throws Exception {

        //Given
        Long eventPackageId = 1L;
        doThrow(new EventPackageNotFoundException(eventPackageId))
                .when(eventPackageService).getEventPackageById(eventPackageId);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/event-packages/" + eventPackageId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(eventPackageService).getEventPackageById(eventPackageId);
        verifyNoInteractions(eventPackageMapper);
    }


    @Test
    void shouldGetAllEventPackagesSuccessfully() throws Exception {

        //Given
        EventPackage e1 = new EventPackage();
        EventPackage e2 = new EventPackage();

        EventPackageResponseDto e1responseDto = new EventPackageResponseDto(1L,"Unicorn show",
                "Magic dancing unicorns", new BigDecimal("1000.00"), 15,3);
        EventPackageResponseDto e2responseDto = new EventPackageResponseDto(2L,"Little chemists",
                "Chemical experiments", new BigDecimal("1300.00"), 10,2);

        when(eventPackageService.getAllEventPackages()).thenReturn(List.of(e1, e2));
        when(eventPackageMapper.mapToEventPackageResponseDtoList(anyList())).thenReturn(List.of(e1responseDto, e2responseDto));

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/event-packages")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Unicorn show"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].basePrice").value(1300.00));

        verify(eventPackageService).getAllEventPackages();
        verify(eventPackageMapper).mapToEventPackageResponseDtoList(anyList());
    }


    @Test
    void shouldUpdatePutEventPackageSuccessfully() throws Exception {

        //Given
        Long eventPackageId = 1L;
        EventPackagePutDto putDto = new EventPackagePutDto("Unicorn show",
                "Magic dancing unicorns", new BigDecimal("1000.00"), 15,3);
        EventPackage eventPackageUpdated = new EventPackage();
        EventPackageResponseDto responseDto = new EventPackageResponseDto(eventPackageId,"Unicorn show",
                "Magic dancing unicorns", new BigDecimal("1000.00"), 15,3);

        when(eventPackageService.updatePutEventPackage(eq(eventPackageId), any(EventPackagePutDto.class)))
                 .thenReturn(eventPackageUpdated);
        when(eventPackageMapper.mapToEventPackageResponseDto(eventPackageUpdated)).thenReturn(responseDto);

        String jsonContent = objectMapper.writeValueAsString(putDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/event-packages/" + eventPackageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(eventPackageId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Unicorn show"));

        verify(eventPackageService).updatePutEventPackage(eq(eventPackageId), any(EventPackagePutDto.class));
        verify(eventPackageMapper).mapToEventPackageResponseDto(any(EventPackage.class));
    }


    @Test
    void shouldReturnBadRequestWhenUpdatingPutEventPackageWithInvalidData() throws Exception {

        //Given
        long eventPackageId = 1L;
        EventPackagePutDto putDto = new EventPackagePutDto("Unicorn show",
                "Magic dancing unicorns", new BigDecimal("1000.00"), 15,0);

        String jsonContent = objectMapper.writeValueAsString(putDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/event-packages/" + eventPackageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verifyNoInteractions(eventPackageService);
        verifyNoInteractions(eventPackageMapper);
    }


    @Test
    void shouldUpdatePatchEventPackageSuccessfully() throws Exception {

        //Given
        Long eventPackageId = 1L;
        EventPackagePatchDto patchDto = new EventPackagePatchDto("Unicorn show updated",
                null, null, null,null);
        EventPackage eventPackageUpdated = new EventPackage();
        EventPackageResponseDto responseDto = new EventPackageResponseDto(eventPackageId,"Unicorn show updated",
                "Magic dancing unicorns", new BigDecimal("1000.00"), 15,3);

        when(eventPackageService.updatePatchEventPackage(eq(eventPackageId), any(EventPackagePatchDto.class)))
                .thenReturn(eventPackageUpdated);
        when(eventPackageMapper.mapToEventPackageResponseDto(eventPackageUpdated)).thenReturn(responseDto);

        String jsonContent = objectMapper.writeValueAsString(patchDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/event-packages/" + eventPackageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(eventPackageId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Unicorn show updated"));

        verify(eventPackageService).updatePatchEventPackage(eq(eventPackageId), any(EventPackagePatchDto.class));
        verify(eventPackageMapper).mapToEventPackageResponseDto(any(EventPackage.class));
    }


    @Test
    void shouldReturnBadRequestWhenUpdatingPatchEventPackageWithInvalidData() throws Exception {

        //Given
        long eventPackageId = 1L;
        EventPackagePatchDto patchDto = new EventPackagePatchDto(null,
                null, new BigDecimal("400"), null,null);

        String jsonContent = objectMapper.writeValueAsString(patchDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/event-packages/" + eventPackageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verifyNoInteractions(eventPackageService);
        verifyNoInteractions(eventPackageMapper);
    }


    @Test
    void shouldDeleteEventPackageSuccessfully() throws Exception {

        //Given, When & Then
        Long eventPackageId = 1L;

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/event-packages/" + eventPackageId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(eventPackageService).deleteEventPackageById(eventPackageId);
    }


    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentEventPackage() throws Exception {

        //Given
        Long eventPackageId = 1L;
        doThrow(new EventPackageNotFoundException(eventPackageId))
                .when(eventPackageService).deleteEventPackageById(eventPackageId);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/event-packages/" + eventPackageId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(eventPackageService).deleteEventPackageById(eventPackageId);
    }
}
