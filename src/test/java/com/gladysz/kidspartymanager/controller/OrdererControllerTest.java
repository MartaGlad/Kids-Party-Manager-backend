package com.gladysz.kidspartymanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gladysz.kidspartymanager.domain.Orderer;
import com.gladysz.kidspartymanager.dto.orderer.OrdererCreateDto;
import com.gladysz.kidspartymanager.dto.orderer.OrdererResponseDto;
import com.gladysz.kidspartymanager.dto.orderer.OrdererUpdateDto;
import com.gladysz.kidspartymanager.exception.orderer.OrdererNotFoundException;
import com.gladysz.kidspartymanager.mapper.OrdererMapper;
import com.gladysz.kidspartymanager.service.OrdererService;
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

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

@WebMvcTest(OrdererController.class)
public class OrdererControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrdererService ordererService;

    @MockitoBean
    private OrdererMapper ordererMapper;


    @Test
    void shouldAddOrdererSuccessfully() throws Exception {

        //Given
        Long ordererId = 1L;
        OrdererCreateDto createDto = new OrdererCreateDto("Eliza","Smith",
                "eliza49@gmail.com", "80056734");
        Orderer orderer = new Orderer();
        Orderer ordererCreated = new Orderer();
        ReflectionTestUtils.setField(ordererCreated, "id", ordererId);
        OrdererResponseDto responseDto = new OrdererResponseDto(ordererId,"Eliza","Smith",
                "eliza49@gmail.com", "80056734");

        when(ordererMapper.mapToOrderer(any(OrdererCreateDto.class))).thenReturn(orderer);
        when(ordererService.createOrderer(orderer)).thenReturn(ordererCreated);
        when(ordererMapper.mapToOrdererResponseDto(ordererCreated)).thenReturn(responseDto);

        String jsonContent = objectMapper.writeValueAsString(createDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/orderers")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string(
                        "Location", containsString("/api/v1/orderers/" + ordererId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ordererId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Eliza"));

        verify(ordererMapper).mapToOrderer(any(OrdererCreateDto.class));
        verify(ordererService).createOrderer(orderer);
        verify(ordererMapper).mapToOrdererResponseDto(ordererCreated);
    }


    @Test
    void shouldReturnBadRequestWhenCreatingOrdererWithInvalidData() throws Exception {

        //Given
        OrdererCreateDto createDto = new OrdererCreateDto("", "Smith",
                "eliza@49gmail.com", "80056734");

        String jsonContent = objectMapper.writeValueAsString(createDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/orderers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verifyNoInteractions(ordererMapper);
        verifyNoInteractions(ordererService);
    }


    @Test
    void shouldGetOrdererByIdSuccessfully() throws Exception {

        //Given
        Long ordererId = 1L;
        Orderer orderer = new Orderer();
        OrdererResponseDto responseDto = new OrdererResponseDto(ordererId,"Eliza","Smith",
                "eliza49@gmail.com", "80056734");

        when(ordererService.getOrdererById(ordererId)).thenReturn(orderer);
        when(ordererMapper.mapToOrdererResponseDto(orderer)).thenReturn(responseDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/orderers/" + ordererId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ordererId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Eliza"));

        verify(ordererService).getOrdererById(ordererId);
        verify(ordererMapper).mapToOrdererResponseDto(any(Orderer.class));
    }


    @Test
    void shouldReturnNotFoundWhenOrdererDoesNotExist() throws Exception {

        //Given
        Long ordererId = 1L;
        doThrow(new OrdererNotFoundException(ordererId)).when(ordererService).getOrdererById(ordererId);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/orderers/" + ordererId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(ordererService).getOrdererById(ordererId);
        verifyNoInteractions(ordererMapper);
    }


    @Test
    void shouldGetAllOrderersSuccessfully() throws Exception {

        //Given
        Orderer o1 = new Orderer();
        Orderer o2 = new Orderer();

        OrdererResponseDto o1responseDto = new OrdererResponseDto(1L, "Eliza", "Smith",
                "eliza49@gmail.com", "80056734");

        OrdererResponseDto o2responseDto = new OrdererResponseDto(2L, "Kate", "Smog",
                "kate50@gmail.com", "80056734");

        when(ordererService.getAllOrderers()).thenReturn(List.of(o1, o2));
        when(ordererMapper.mapToOrdererResponseDtoList(anyList())).thenReturn(List.of(o1responseDto, o2responseDto));


        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/orderers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("Eliza"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].email").value("kate50@gmail.com"));

        verify(ordererService).getAllOrderers();
        verify(ordererMapper).mapToOrdererResponseDtoList(anyList());
    }


    @Test
    void shouldUpdateOrdererSuccessfully() throws Exception {

        //Given
        Long ordererId = 1L;
        OrdererUpdateDto updateDto = new OrdererUpdateDto(null, null, null,
                "eliza50@onet.eu");
        Orderer ordererUpdated = new Orderer();
        OrdererResponseDto responseDto = new OrdererResponseDto(ordererId,"Eliza","Smith",
                "eliza50@onet.eu", "80056734");

        when(ordererService.updateOrderer(eq(ordererId), any(OrdererUpdateDto.class)))
                .thenReturn(ordererUpdated);
        when(ordererMapper.mapToOrdererResponseDto(ordererUpdated)).thenReturn(responseDto);

        String jsonContent = objectMapper.writeValueAsString(updateDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/orderers/" + ordererId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ordererId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("eliza50@onet.eu"));

        verify(ordererService).updateOrderer(eq(ordererId), any(OrdererUpdateDto.class));
        verify(ordererMapper).mapToOrdererResponseDto(any(Orderer.class));
    }


    @Test
    void shouldReturnBadRequestWhenUpdatingOrdererWithInvalidData() throws Exception {

        //Given
        long ordererId = 1L;
        OrdererUpdateDto updateDto = new OrdererUpdateDto(null, null,
                "eliza50-onet.eu", null);

        String jsonContent = objectMapper.writeValueAsString(updateDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/orderers/" + ordererId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verifyNoInteractions(ordererMapper);
        verifyNoInteractions(ordererService);
    }


    @Test
    void shouldDeleteOrdererSuccessfully() throws Exception {

        //Given, When & Then
        Long ordererId = 1L;

        mockMvc.perform(MockMvcRequestBuilders
                    .delete("/api/v1/orderers/" + ordererId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(ordererService).deleteOrdererById(ordererId);
        verifyNoInteractions(ordererMapper);
    }


    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingOrderer() throws Exception {

        //Given
        Long ordererId = 1L;
        doThrow(new OrdererNotFoundException(ordererId)).when(ordererService).deleteOrdererById(ordererId);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                    .delete("/api/v1/orderers/" + ordererId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8"))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(ordererService).deleteOrdererById(ordererId);
        verifyNoInteractions(ordererMapper);
    }
}
