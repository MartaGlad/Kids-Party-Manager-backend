package com.gladysz.kidspartymanager.mapper;

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


    public void applyUpdatePatch(final EventPackage eventPackage, final EventPackagePatchDto eventPackagePatchDto) {

        if (hasText(eventPackagePatchDto.name())) {
            eventPackage.setName(eventPackagePatchDto.name().trim());
        }

        if (hasText(eventPackagePatchDto.description())) {
            eventPackage.setDescription(eventPackagePatchDto.description().trim());
        }

        if (eventPackagePatchDto.basePrice() != null) {
            eventPackage.setBasePrice(eventPackagePatchDto.basePrice());
        }

        if (eventPackagePatchDto.maxChildrenCount() != null) {
            eventPackage.setMaxChildrenCount(eventPackagePatchDto.maxChildrenCount());
        }

        if (eventPackagePatchDto.durationHr() != null) {
            eventPackage.setDurationHr(eventPackagePatchDto.durationHr());
        }
    }


    public void applyUpdatePut(final EventPackage eventPackage, final EventPackagePutDto eventPackagePutDto) {

        eventPackage.setName(eventPackagePutDto.name().trim());

        eventPackage.setDescription(eventPackagePutDto.description().trim());

        eventPackage.setBasePrice(eventPackagePutDto.basePrice());

        eventPackage.setMaxChildrenCount(eventPackagePutDto.maxChildrenCount());

        eventPackage.setDurationHr(eventPackagePutDto.durationHr());
    }


    public EventPackage mapToEventPackage(final EventPackageCreateDto eventPackageCreateDto) {
        
        return new EventPackage (
                null,
                eventPackageCreateDto.name().trim(),
                eventPackageCreateDto.description().trim(),
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

