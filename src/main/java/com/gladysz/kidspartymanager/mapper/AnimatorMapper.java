package com.gladysz.kidspartymanager.mapper;

import com.gladysz.kidspartymanager.domain.Animator;
import com.gladysz.kidspartymanager.dto.AnimatorUpdateDto;
import org.springframework.stereotype.Component;

@Component
public class AnimatorMapper {

    private boolean hasText (String s) {

        return s != null && !s.trim().isEmpty();
    }


    private String normalizeName (String name) {

        String result = name.trim().toLowerCase();
        return result.substring(0, 1).toUpperCase() + result.substring(1);
    }


    public void mapToAnimator (final Animator animator, final AnimatorUpdateDto animatorUpdateDto) {

        if (hasText(animatorUpdateDto.firstName())) {
            animator.setFirstName(normalizeName(animatorUpdateDto.firstName()));
        }

        if (hasText(animatorUpdateDto.lastName())) {
            animator.setLastName(normalizeName(animatorUpdateDto.lastName()));
        }

        if (hasText(animatorUpdateDto.email())) {
            animator.setEmail(animatorUpdateDto.email().trim().toLowerCase());
        }

        if (hasText(animatorUpdateDto.phone())) {
            animator.setPhone(animatorUpdateDto.phone().trim());
        }
    }
}


