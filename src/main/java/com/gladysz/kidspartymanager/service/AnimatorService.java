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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AnimatorService {

    private final AnimatorRepository animatorRepository;
    private final AnimatorMapper animatorMapper;
    private final ReservationRepository reservationRepository;
    private final EventAssessmentRepository eventAssessmentRepository;


    public Animator createAnimator(final Animator animator) {

        return animatorRepository.save(animator);
    }


    @Transactional(readOnly = true)
    public Animator getAnimatorById(final Long id) {

        return animatorRepository.findById(id)
                .orElseThrow(() -> new AnimatorNotFoundException(id));
    }


    @Transactional(readOnly = true)
    public List<Animator> getAllAnimators() {

        return animatorRepository.findAll();
    }


    public Animator updateAnimator(final Long id, final AnimatorUpdateDto animatorUpdateDto) {

        Animator fetchedAnimator = animatorRepository.findById(id)
                .orElseThrow(() -> new AnimatorNotFoundException(id));

        animatorMapper.applyUpdate(fetchedAnimator, animatorUpdateDto);

        return fetchedAnimator;
    }


    public Animator deactivateAnimatorById(final Long id) {

        Animator fetchedAnimator = animatorRepository.findById(id)
                .orElseThrow(() -> new AnimatorNotFoundException(id));

        if (!fetchedAnimator.isActive()) {
            throw new AnimatorInactiveException(id);
        }
        fetchedAnimator.setActive(false);

        return fetchedAnimator;
    }


    public void deleteAnimatorById(final Long id) {

        Animator fetchedAnimator = animatorRepository.findById(id)
                .orElseThrow(() -> new AnimatorNotFoundException(id));

        if (reservationRepository.existsByAnimatorId(fetchedAnimator.getId())) {
            throw new AnimatorDeleteException(id);
        }
        animatorRepository.delete(fetchedAnimator);
    }


    public AnimatorRatingResponseDto getAnimatorReservationRating(final Long id) {

        getAnimatorById(id);

        Double averageRating = eventAssessmentRepository
                .findAverageReservationRatingByAnimatorId(id);

        Double average = averageRating == null ? 0.0 : averageRating;

        long ratingsCount = eventAssessmentRepository
                .countReservationRatingsByAnimatorId(id);

        return new AnimatorRatingResponseDto(id, average, ratingsCount);
    }


    public List<AnimatorRatingResponseDto> getAllAverageRatings() {

        return eventAssessmentRepository.findAllAverageReservationRatings();
    }
}




