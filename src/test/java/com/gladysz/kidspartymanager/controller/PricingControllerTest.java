package com.gladysz.kidspartymanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gladysz.kidspartymanager.domain.EventPackage;
import com.gladysz.kidspartymanager.domain.pricing.PricingResult;
import com.gladysz.kidspartymanager.dto.pricing.PricingRequestDto;
import com.gladysz.kidspartymanager.service.EventPackageService;
import com.gladysz.kidspartymanager.service.PricingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

@WebMvcTest(PricingController.class)
public class PricingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventPackageService eventPackageService;

    @MockitoBean
    private PricingService pricingService;


    @Test
    void shouldPreviewPriceSuccessfully() throws Exception {

        //Given
        LocalDate date = LocalDate.now();
        PricingRequestDto requestDto = new PricingRequestDto(1L, 7, date);
        PricingResult result = new PricingResult(
                new BigDecimal("1000"),
                new BigDecimal("236.7"),
                new BigDecimal("279.35"),
                new BigDecimal("206.04"),
                false);

        when(eventPackageService.getEventPackageById(1L)).thenReturn(new EventPackage());
        when(pricingService.getPricingResult(any(EventPackage.class), eq(7), any(LocalDate.class))).thenReturn(result);

        String jsonContent = objectMapper.writeValueAsString(requestDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/pricing/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalPricePln").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.priceInEur").value(236.7))
                .andExpect(MockMvcResultMatchers.jsonPath("$.holiday").value(false));

        verify(eventPackageService).getEventPackageById(1L);
        verify(pricingService).getPricingResult(any(EventPackage.class), eq(7), any(LocalDate.class));
    }


    @Test
    void shouldReturnBadRequestWhenPreviewPricingRequestIsInvalid() throws Exception {

        //Given
        LocalDate date = LocalDate.now();
        PricingRequestDto requestDto = new PricingRequestDto(null, 7, date);

        String jsonContent = objectMapper.writeValueAsString(requestDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/pricing/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verifyNoInteractions(eventPackageService);
        verifyNoInteractions(pricingService);
    }
}
