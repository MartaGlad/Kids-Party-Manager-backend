package com.gladysz.kidspartymanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gladysz.kidspartymanager.domain.Animator;
import com.gladysz.kidspartymanager.dto.animator.AnimatorCreateDto;
import com.gladysz.kidspartymanager.dto.animator.AnimatorRatingResponseDto;
import com.gladysz.kidspartymanager.dto.animator.AnimatorResponseDto;
import com.gladysz.kidspartymanager.dto.animator.AnimatorUpdateDto;
import com.gladysz.kidspartymanager.exception.animator.AnimatorNotFoundException;
import com.gladysz.kidspartymanager.mapper.AnimatorMapper;
import com.gladysz.kidspartymanager.service.AnimatorService;
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
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

@WebMvcTest(AnimatorController.class)
public class AnimatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AnimatorService animatorService;

    @MockitoBean
    private AnimatorMapper animatorMapper;


    @Test
    void shouldAddAnimatorSuccessfully() throws Exception {

        //Given
        Long animatorId = 1L;
        AnimatorCreateDto createDto = new AnimatorCreateDto("Peter", "Papper", "peter@gmail.com",
                "123123");
        Animator animator = new Animator();
        Animator animatorCreated = new Animator();
        ReflectionTestUtils.setField(animatorCreated, "id", animatorId);
        AnimatorResponseDto responseDto = new AnimatorResponseDto(animatorId, "Peter", "Papper",
                "peter@gmail.com", "123123", true);

        when(animatorMapper.mapToAnimator(any(AnimatorCreateDto.class))).thenReturn(animator);
        when(animatorService.createAnimator(animator)).thenReturn(animatorCreated);
        when(animatorMapper.mapToAnimatorResponseDto(animatorCreated)).thenReturn(responseDto);

        String jsonContent = objectMapper.writeValueAsString(createDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/animators")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", containsString("/api/v1/animators/" + animatorId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Peter"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Papper"));

        verify(animatorMapper).mapToAnimator(createDto);
        verify(animatorService).createAnimator(animator);
        verify(animatorMapper).mapToAnimatorResponseDto(animatorCreated);
    }


    @Test
    void shouldReturnBadRequestWhenCreatingAnimatorWithInvalidData() throws Exception {

        //Given
        AnimatorCreateDto createDto = new AnimatorCreateDto("Peter", "Papper",
                "peter@gmail.com", "");

        String jsonContent = objectMapper.writeValueAsString(createDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/animators")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verifyNoInteractions(animatorMapper);
        verifyNoInteractions(animatorService);
    }


    @Test
    void shouldGetAnimatorByIdSuccessfully() throws Exception {

        //Given
        Long animatorId = 1L;
        Animator animator = new Animator();
        AnimatorResponseDto responseDto = new AnimatorResponseDto(animatorId, "Peter", "Papper",
                "peter@gmail.com", "123123", true);

        when(animatorService.getAnimatorById(animatorId)).thenReturn(animator);
        when(animatorMapper.mapToAnimatorResponseDto(animator)).thenReturn(responseDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/animators/" + animatorId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Peter"));

        verify(animatorService).getAnimatorById(animatorId);
        verify(animatorMapper).mapToAnimatorResponseDto(any(Animator.class));
    }


    @Test
    void shouldReturnNotFoundWhenAnimatorDoesNotExist() throws Exception {

        //Given
        Long animatorId = 1L;
        doThrow(new AnimatorNotFoundException(animatorId)).when(animatorService).getAnimatorById(animatorId);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/animators/" + animatorId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(animatorService).getAnimatorById(animatorId);
        verifyNoInteractions(animatorMapper);
    }


    @Test
    void shouldGetAllAnimatorsSuccessfully() throws Exception {

        //Given
        Animator a1 = new Animator();
        Animator a2 = new Animator();

        AnimatorResponseDto a1ResponseDto = new AnimatorResponseDto(1L, "Peter", "Papper",
                "peter@gmail.com", "123123", true);

        AnimatorResponseDto a2ResponseDto = new AnimatorResponseDto(2L, "Daniel", "Crail",
                "daniel@gmail.com", "8888123", true);

        when(animatorService.getAllAnimators()).thenReturn(List.of(a1, a2));
        when(animatorMapper.mapToAnimatorResponseDtoList(anyList())).thenReturn(List.of(a1ResponseDto, a2ResponseDto));

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/animators")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("Peter"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].firstName").value("Daniel"));

        verify(animatorService).getAllAnimators();
        verify(animatorMapper).mapToAnimatorResponseDtoList(anyList());
    }


    @Test
    void shouldUpdateAnimatorSuccessfully() throws Exception {

        //Given
        Long animatorId = 1L;
        AnimatorUpdateDto updateDto = new AnimatorUpdateDto(null, "Letter", null, null);
        Animator animatorUpdated = new Animator();
        AnimatorResponseDto responseDto = new AnimatorResponseDto(animatorId, "Peter", "Letter",
                "peter@gmail.com", "123123", true);

        when(animatorService.updateAnimator(eq(animatorId), any(AnimatorUpdateDto.class)))
                .thenReturn(animatorUpdated);
        when(animatorMapper.mapToAnimatorResponseDto(animatorUpdated)).thenReturn(responseDto);

        String jsonContent = objectMapper.writeValueAsString(updateDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/v1/animators/" + animatorId)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Letter"));

        verify(animatorService).updateAnimator(eq(animatorId), any(AnimatorUpdateDto.class));
        verify(animatorMapper).mapToAnimatorResponseDto(any(Animator.class));
    }


    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingAnimator() throws Exception {

        //Given
        Long animatorId = 1L;
        AnimatorUpdateDto updateDto = new AnimatorUpdateDto(null, "Letter", null, null);
        doThrow(new AnimatorNotFoundException(animatorId))
                .when(animatorService).updateAnimator(animatorId, updateDto);

        String jsonContent = objectMapper.writeValueAsString(updateDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/v1/animators/" + animatorId)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(animatorService).updateAnimator(animatorId, updateDto);
        verifyNoInteractions(animatorMapper);
    }


    @Test
    void shouldDeactivateAnimatorSuccessfully() throws Exception {

        //Given
        Long animatorId = 1L;
        Animator animatorDeactivated = new Animator();
        ReflectionTestUtils.setField(animatorDeactivated, "id", animatorId);
        animatorDeactivated.setActive(false);

        AnimatorResponseDto responseDto = new AnimatorResponseDto(animatorId, "Peter", "Letter",
                "peter@gmail.com", "123123", false);

        when(animatorService.deactivateAnimatorById(animatorId)).thenReturn(animatorDeactivated);
        when(animatorMapper.mapToAnimatorResponseDto(animatorDeactivated)).thenReturn(responseDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/animators/" + animatorId + "/deactivate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.active").value(false));

        verify(animatorService).deactivateAnimatorById(animatorId);
        verify(animatorMapper).mapToAnimatorResponseDto(animatorDeactivated);
    }


    @Test
    void shouldDeleteAnimatorSuccessfully() throws Exception {

        //Given, When & Then
        Long animatorId = 1L;
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/animators/" + animatorId)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(animatorService).deleteAnimatorById(animatorId);
        verifyNoInteractions(animatorMapper);
    }


    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingAnimator() throws Exception {

        //Given
        Long animatorId = 1L;
        doThrow(new AnimatorNotFoundException(animatorId)).when(animatorService).deleteAnimatorById(animatorId);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/animators/" + animatorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(animatorService).deleteAnimatorById(animatorId);
        verifyNoInteractions(animatorMapper);
    }


    @Test
    void shouldReturnAnimatorReservationRating() throws Exception {

        //Given
        Long animatorId = 1L;
        AnimatorRatingResponseDto responseDto = new AnimatorRatingResponseDto(1L, 4.5, 3);

        when(animatorService.getAnimatorReservationRating(animatorId)).thenReturn(responseDto);

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/animators/" + animatorId + "/rating")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.animatorId").value(1))
                        .andExpect(MockMvcResultMatchers.jsonPath("$.averageRating").value(4.5));

        verify(animatorService).getAnimatorReservationRating(animatorId);
    }


    @Test
    void shouldGetAllAnimatorsRatings() throws Exception {

        //Given
        AnimatorRatingResponseDto responseDto1 = new AnimatorRatingResponseDto(1L, 4.5, 3);
        AnimatorRatingResponseDto responseDto2 = new AnimatorRatingResponseDto(2L, 5.0, 4);

        when(animatorService.getAllAverageRatings()).thenReturn(List.of(responseDto1, responseDto2));

        //When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/animators/ratings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].animatorId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].averageRating").value(4.5))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].averageRating").value(5.0));

        verify(animatorService).getAllAverageRatings();
    }
}
