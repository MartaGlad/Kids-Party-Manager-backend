package com.gladysz.kidspartymanager.dto.orderer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OrdererCreateDto(@NotBlank String firstName, @NotBlank String lastName,
                               @NotBlank @Email String email, @NotBlank String phone) {
}
