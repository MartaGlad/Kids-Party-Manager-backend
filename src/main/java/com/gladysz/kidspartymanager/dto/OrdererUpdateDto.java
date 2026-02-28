package com.gladysz.kidspartymanager.dto;

import jakarta.validation.constraints.Email;

public record OrdererUpdateDto(String firstName, String lastName, @Email String email, String phone) {
}
