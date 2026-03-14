package com.gladysz.kidspartymanager.mapper;

import com.gladysz.kidspartymanager.domain.Animator;
import com.gladysz.kidspartymanager.dto.animator.AnimatorCreateDto;
import com.gladysz.kidspartymanager.dto.animator.AnimatorResponseDto;
import com.gladysz.kidspartymanager.dto.animator.AnimatorUpdateDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class AnimatorMapper {

    private boolean hasText(String s) {

        return s != null && !s.trim().isEmpty();
    }


    public void applyUpdate(final Animator animator, final AnimatorUpdateDto animatorUpdateDto) {

        if (hasText(animatorUpdateDto.firstName())) {
            animator.setFirstName(animatorUpdateDto.firstName());
        }

        if (hasText(animatorUpdateDto.lastName())) {
            animator.setLastName(animatorUpdateDto.lastName());
        }

        if (hasText(animatorUpdateDto.email())) {
            animator.setEmail(animatorUpdateDto.email().trim().toLowerCase());
        }

        if (hasText(animatorUpdateDto.phone())) {
            animator.setPhone(animatorUpdateDto.phone().trim());
        }
    }


    public Animator mapToAnimator(final AnimatorCreateDto animatorCreateDto) {

        return new Animator (
                null,
                animatorCreateDto.firstName(),
                animatorCreateDto.lastName(),
                animatorCreateDto.email().trim().toLowerCase(),
                animatorCreateDto.phone().trim(),
                true,
                new ArrayList<>()
        );
    }


    public AnimatorResponseDto mapToAnimatorResponseDto(final Animator animator) {

        return new AnimatorResponseDto (
                animator.getId(),
                animator.getFirstName(),
                animator.getLastName(),
                animator.getEmail(),
                animator.getPhone(),
                animator.isActive()
        );
    }


    public List<AnimatorResponseDto> mapToAnimatorResponseDtoList(final List<Animator> animators) {

        return animators.stream()
                .map(this::mapToAnimatorResponseDto)
                .collect(Collectors.toList());
    }

}


