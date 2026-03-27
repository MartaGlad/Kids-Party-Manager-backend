package com.gladysz.kidspartymanager.dto.animator;

import jakarta.validation.constraints.Email;

public record AnimatorUpdateDto(
        String firstName, String lastName,
        @Email String email, String phone
){}


