package com.gladysz.kidspartymanager.mapper;

import com.gladysz.kidspartymanager.domain.Animator;
import com.gladysz.kidspartymanager.domain.EventPackage;
import com.gladysz.kidspartymanager.dto.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class EventPackageMapper {

    private boolean hasText(String s) {

        return s != null && !s.trim().isEmpty();
    }


    public void applyUpdate(final EventPackage eventPackage, final EventPackageUpdateDto eventPackageUpdateDto) {

        if (hasText(eventPackageUpdateDto.name())) {
            eventPackage.setName(eventPackageUpdateDto.name().trim());
        }

        if (hasText(eventPackageUpdateDto.description())) {
            eventPackage.setDescription(eventPackageUpdateDto.description().trim());
        }

        if (eventPackageUpdateDto.basePrice() != null) {
            eventPackage.setBasePrice(eventPackageUpdateDto.basePrice());
        }

        if (eventPackageUpdateDto.maxChildrenCount() != null) {
            eventPackage.setMaxChildrenCount(eventPackageUpdateDto.maxChildrenCount());
        }

        if (eventPackageUpdateDto.durationHr() != null) {
            eventPackage.setDurationHr(eventPackageUpdateDto.durationHr());
        }
    }


    public EventPackage mapToEventPackage(final EventPackageCreateDto eventPackageCreateDto) {
        
        return new EventPackage (
                null,
                eventPackageCreateDto.name().trim().toLowerCase(),
                eventPackageCreateDto.description().trim().toLowerCase(),
                eventPackageCreateDto.basePrice(),
                eventPackageCreateDto.maxChildrenCount(),
                eventPackageCreateDto.durationHr(),
                new ArrayList<>()
        );
    }


    public EventPackageResponseDto mapToEventPackageResponseDto(final EventPackage eventPackage) {

        return new EventPackageResponseDto (
                eventPackage.getId(),
                eventPackage.getName(),
                eventPackage.getDescription(),
                eventPackage.getBasePrice(),
                eventPackage.getMaxChildrenCount(),
                eventPackage.getDurationHr()
        );
    }


    public List<EventPackageResponseDto> mapToEventPackageResponseDtoList(final List<EventPackage> eventPackages) {

        return eventPackages.stream()
                .map(this::mapToEventPackageResponseDto)
                .collect(Collectors.toList());
    }

}

