package com.gladysz.kidspartymanager.mapper;


import com.gladysz.kidspartymanager.domain.Orderer;
import com.gladysz.kidspartymanager.dto.OrdererCreateDto;
import com.gladysz.kidspartymanager.dto.OrdererResponseDto;
import com.gladysz.kidspartymanager.dto.OrdererUpdateDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrdererMapper {

    private boolean hasText(String s) {

        return s != null && !s.trim().isEmpty();
    }


    public void applyUpdate(final Orderer orderer, final OrdererUpdateDto ordererUpdateDto) {

        if (hasText(ordererUpdateDto.firstName())) {
            orderer.setFirstName(ordererUpdateDto.firstName().trim());
        }

        if (hasText(ordererUpdateDto.lastName())) {
            orderer.setLastName(ordererUpdateDto.lastName().trim());
        }

        if (hasText(ordererUpdateDto.email())) {
            orderer.setEmail(ordererUpdateDto.email().trim());
        }

        if (hasText(ordererUpdateDto.phone())) {
            orderer.setPhone(ordererUpdateDto.phone().trim());
        }
    }


    public Orderer mapToOrderer(final OrdererCreateDto ordererCreateDto) {

        return new Orderer (
                null,
                ordererCreateDto.firstName().trim(),
                ordererCreateDto.lastName().trim(),
                ordererCreateDto.email().trim().toLowerCase(),
                ordererCreateDto.phone().trim(),
                new ArrayList<>()
        );
    }


    public OrdererResponseDto mapToOrdererResponseDto(final Orderer orderer) {

        return new OrdererResponseDto (
                orderer.getId(),
                orderer.getFirstName(),
                orderer.getLastName(),
                orderer.getEmail(),
                orderer.getPhone()
        );
    }


    public List<OrdererResponseDto> mapToOrdererResponseDtoList(final List<Orderer> orderers) {

        return orderers.stream()
                .map(this::mapToOrdererResponseDto)
                .collect(Collectors.toList());
    }

}
