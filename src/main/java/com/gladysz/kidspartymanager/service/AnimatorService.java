package com.gladysz.kidspartymanager.service;

import com.gladysz.kidspartymanager.domain.Animator;
import com.gladysz.kidspartymanager.dto.animator.AnimatorUpdateDto;
import com.gladysz.kidspartymanager.exception.animator.AnimatorDeleteException;
import com.gladysz.kidspartymanager.exception.animator.AnimatorNotFoundException;
import com.gladysz.kidspartymanager.mapper.AnimatorMapper;
import com.gladysz.kidspartymanager.repository.AnimatorRepository;
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


    public void deleteAnimatorById(final Long id) {

        Animator fetchedAnimator = animatorRepository.findById(id)
                .orElseThrow(() -> new AnimatorNotFoundException(id));

        if (reservationRepository.existsByAnimatorId(fetchedAnimator.getId())) {
            throw new AnimatorDeleteException(id);
        }
        animatorRepository.delete(fetchedAnimator);
    }
}




