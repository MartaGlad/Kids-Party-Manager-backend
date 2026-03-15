package com.gladysz.kidspartymanager.mapper;


import com.gladysz.kidspartymanager.domain.Orderer;
import com.gladysz.kidspartymanager.dto.orderer.OrdererCreateDto;
import com.gladysz.kidspartymanager.dto.orderer.OrdererResponseDto;
import com.gladysz.kidspartymanager.dto.orderer.OrdererUpdateDto;
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

        Orderer orderer = new Orderer();

        orderer.setFirstName(ordererCreateDto.firstName().trim());
        orderer.setLastName(ordererCreateDto.lastName().trim());
        orderer.setEmail(ordererCreateDto.email().trim().toLowerCase());
        orderer.setPhone(ordererCreateDto.phone().trim());

        return orderer;
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
