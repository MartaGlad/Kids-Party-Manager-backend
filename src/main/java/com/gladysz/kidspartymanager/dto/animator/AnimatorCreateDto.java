package com.gladysz.kidspartymanager.dto.animator;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AnimatorCreateDto(@NotBlank String firstName, @NotBlank String lastName,
                                @NotBlank @Email String email, @NotBlank String phone) {
}
