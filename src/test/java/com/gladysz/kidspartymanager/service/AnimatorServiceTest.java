package com.gladysz.kidspartymanager.service;

import com.gladysz.kidspartymanager.domain.Animator;
import com.gladysz.kidspartymanager.dto.animator.AnimatorRatingResponseDto;
import com.gladysz.kidspartymanager.dto.animator.AnimatorUpdateDto;
import com.gladysz.kidspartymanager.exception.animator.AnimatorDeleteException;
import com.gladysz.kidspartymanager.exception.animator.AnimatorInactiveException;
import com.gladysz.kidspartymanager.exception.animator.AnimatorNotFoundException;
import com.gladysz.kidspartymanager.mapper.AnimatorMapper;
import com.gladysz.kidspartymanager.repository.AnimatorRepository;
import com.gladysz.kidspartymanager.repository.EventAssessmentRepository;
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
public class AnimatorServiceTest {

    @InjectMocks
    private AnimatorService animatorService;

    @Mock
    private AnimatorRepository animatorRepository;

    @Mock
    private AnimatorMapper animatorMapper;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private EventAssessmentRepository eventAssessmentRepository;


    @Test
    void shouldCreateAnimatorSuccessfully() {

        //Given
        Animator animator = new Animator();
        when(animatorRepository.save(animator)).thenReturn(animator);

        //When
        Animator animatorSaved = animatorService.createAnimator(animator);

        //Then
        assertSame(animator, animatorSaved);
        verify(animatorRepository).save(animator);
    }


    @Test
    void shouldReturnAllAnimators() {

        //Given
        Animator animator = new Animator();

        when(animatorRepository.findAll()).thenReturn(List.of(animator));

        //When
        List<Animator> animators = animatorService.getAllAnimators();

        //Then
        assertEquals(1,  animators.size());
        verify(animatorRepository).findAll();
    }


    @Test
    void shouldUpdateAnimatorSuccessfully() {

        //Given
        Animator a = new Animator();
        AnimatorUpdateDto dto = new AnimatorUpdateDto(
                "Peter", "Papper", "peter@gmail.com",
                "123123"
        );

        when(animatorRepository.findById(1L)).thenReturn(Optional.of(a));

        doAnswer(i -> {
            Animator animator = i.getArgument(0);
            AnimatorUpdateDto updateDto = i.getArgument(1);

            animator.setFirstName(updateDto.firstName());
            animator.setLastName(updateDto.lastName());
            animator.setEmail(updateDto.email());
            animator.setPhone(updateDto.phone());

            return null;
        }).when(animatorMapper).applyUpdate(a, dto);

        //When
        Animator animatorUpdated = animatorService.updateAnimator(1L, dto);

        //Then
        assertSame(a, animatorUpdated);
        assertEquals("Peter", animatorUpdated.getFirstName());
        verify(animatorRepository).findById(1L);
        verify(animatorMapper).applyUpdate(a, dto);
    }


    @Test
    void shouldThrowExceptionWhenAnimatorNotFoundDuringUpdate() {

        //Given
        AnimatorUpdateDto dto = new AnimatorUpdateDto(
                "Peter", "Papper", "peter@gmail.com",
                "123123"
        );
        when(animatorRepository.findById(1L)).thenReturn(Optional.empty());

        //When & Then
        assertThrows(AnimatorNotFoundException.class,
                () -> animatorService.updateAnimator(1L, dto));
    }


    @Test
    void shouldDeactivateAnimatorByIdSuccessfully() {

        //Given
        Animator animator = new Animator();
        animator.setActive(true);

        when(animatorRepository.findById(1L)).thenReturn(Optional.of(animator));

        //When
        Animator animatorDeactivated = animatorService.deactivateAnimatorById(1L);

        //Then
        assertFalse(animator.isActive());
        assertSame(animator, animatorDeactivated);
        verify(animatorRepository).findById(1L);
    }


    @Test
    void shouldThrowExceptionWhenAnimatorNotFoundDuringDeactivate() {

        //Given
        when(animatorRepository.findById(1L)).thenReturn(Optional.empty());

        //When & Then
        assertThrows(AnimatorNotFoundException.class,
                () -> animatorService.deactivateAnimatorById(1L));
    }


    @Test
    void shouldThrowExceptionWhenAnimatorAlreadyInactiveDuringDeactivate() {

        //Given
        Animator animator = new Animator();
        animator.setActive(false);

        when(animatorRepository.findById(1L)).thenReturn(Optional.of(animator));

        //When & Then
        assertThrows(AnimatorInactiveException.class,
                () ->  animatorService.deactivateAnimatorById(1L));
        verify(animatorRepository).findById(1L);
    }


    @Test
    void shouldDeleteAnimatorByIdSuccessfully() {

        //Given
        Animator animator = new Animator();
        ReflectionTestUtils.setField(animator, "id", 1L);

        when(animatorRepository.findById(1L)).thenReturn(Optional.of(animator));
        when(reservationRepository.existsByAnimatorId(1L)).thenReturn(false);

        //When
        animatorService.deleteAnimatorById(1L);

        //Then
        verify(animatorRepository).findById(1L);
        verify(reservationRepository).existsByAnimatorId(1L);
        verify(animatorRepository).delete(animator);
    }


    @Test
    void shouldThrowExceptionWhenAnimatorNotFoundDuringDelete() {

        //Given
        when(animatorRepository.findById(1L)).thenReturn(Optional.empty());

        //When & Then
        assertThrows(AnimatorNotFoundException.class,
                () -> animatorService.deleteAnimatorById(1L));
        verify(animatorRepository).findById(1L);
    }


    @Test
    void shouldThrowExceptionWhenDeletingAnimatorWithReservations() {

        //Given
        Animator animator = new Animator();
        ReflectionTestUtils.setField(animator, "id", 1L);

        when(animatorRepository.findById(1L)).thenReturn(Optional.of(animator));
        when(reservationRepository.existsByAnimatorId(1L)).thenReturn(true);

        //When & Then
        assertThrows(AnimatorDeleteException.class,
                () -> animatorService.deleteAnimatorById(1L));
        verify(animatorRepository).findById(1L);
        verify(reservationRepository).existsByAnimatorId(1L);
    }


    @Test
    void shouldReturnAnimatorRatingWhenRatingsExist() {

        //Given
        when(animatorRepository.findById(1L)).thenReturn(Optional.of(new Animator()));
        when(eventAssessmentRepository.findAverageReservationRatingByAnimatorId(1L)).thenReturn(4.8);
        when(eventAssessmentRepository.countReservationRatingsByAnimatorId(1L)).thenReturn(10L);

        //When
        AnimatorRatingResponseDto result = animatorService.getAnimatorReservationRating(1L);

        //Then
        assertEquals(1L, result.animatorId());
        assertEquals(4.8, result.averageRating());
        assertEquals(10L, result.ratingsCount());
        verify(animatorRepository).findById(1L);
        verify(eventAssessmentRepository).findAverageReservationRatingByAnimatorId(1L);
        verify(eventAssessmentRepository).countReservationRatingsByAnimatorId(1L);
    }


    @Test
    void shouldReturnZeroAverageWhenNoRatingsExist() {

        //Given
        when(animatorRepository.findById(1L)).thenReturn(Optional.of(new Animator()));
        when(eventAssessmentRepository.findAverageReservationRatingByAnimatorId(1L)).thenReturn(null);
        when(eventAssessmentRepository.countReservationRatingsByAnimatorId(1L)).thenReturn(0L);

        //When
        AnimatorRatingResponseDto result = animatorService.getAnimatorReservationRating(1L);

        //Then
        assertEquals(1L, result.animatorId());
        assertEquals(0.0, result.averageRating());
        assertEquals(0L, result.ratingsCount());
        verify(animatorRepository).findById(1L);
        verify(eventAssessmentRepository).findAverageReservationRatingByAnimatorId(1L);
        verify(eventAssessmentRepository).countReservationRatingsByAnimatorId(1L);
    }


    @Test
    void shouldThrowExceptionWhenAnimatorNotFoundForRating() {

        //Given
        when(animatorRepository.findById(1L)).thenReturn(Optional.empty());

        //When & Then
        assertThrows(AnimatorNotFoundException.class,
                () -> animatorService.getAnimatorReservationRating(1L));
    }


    @Test
    void shouldReturnAllAverageRatings() {

        //Given
        List<AnimatorRatingResponseDto> resultList = List.of(
                new AnimatorRatingResponseDto(1L, 3.0, 5),
                new AnimatorRatingResponseDto(2L, 5.0, 4)
        );

        when(eventAssessmentRepository.findAllAverageReservationRatings()).thenReturn(resultList);

        //When
        List<AnimatorRatingResponseDto> resultListFetched = animatorService.getAllAverageRatings();

        //Then
        assertEquals(2, resultListFetched.size());
        verify(eventAssessmentRepository).findAllAverageReservationRatings();
    }
}



