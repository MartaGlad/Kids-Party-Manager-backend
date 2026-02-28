package com.gladysz.kidspartymanager.controller;


import com.gladysz.kidspartymanager.domain.EventPackage;
import com.gladysz.kidspartymanager.dto.EventPackageCreateDto;
import com.gladysz.kidspartymanager.dto.EventPackageResponseDto;
import com.gladysz.kidspartymanager.dto.EventPackageUpdateDto;
import com.gladysz.kidspartymanager.mapper.EventPackageMapper;
import com.gladysz.kidspartymanager.service.EventPackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/event-packages")
@RequiredArgsConstructor
public class EventPackageController {

    private final EventPackageService eventPackageService;
    private final EventPackageMapper eventPackageMapper;

    @PostMapping
    public ResponseEntity<EventPackageResponseDto> addEventPackage(
            @Valid @RequestBody EventPackageCreateDto eventPackageCreateDto) {

        EventPackage eventPackage = eventPackageService
                .createEventPackage(eventPackageMapper.mapToEventPackage(eventPackageCreateDto));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(eventPackage.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(eventPackageMapper.mapToEventPackageResponseDto(eventPackage));
    }


    @GetMapping("/{id}")
    public ResponseEntity<EventPackageResponseDto> getEventPackage(@PathVariable Long id) {

        return ResponseEntity
                .ok(eventPackageMapper.mapToEventPackageResponseDto(eventPackageService.getEventPackageById(id)));
    }


    @GetMapping
    public ResponseEntity<List<EventPackageResponseDto>> getAllEventPackages() {

        return ResponseEntity
                .ok(eventPackageMapper.mapToEventPackageResponseDtoList(eventPackageService.getAllEventPackages()));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<EventPackageResponseDto> updateEventPackage(
            @PathVariable Long id,
            @Valid @RequestBody EventPackageUpdateDto eventPackageUpdateDto) {

        EventPackage eventPackageUpdated = eventPackageService.updateEventPackage(id, eventPackageUpdateDto);

        return ResponseEntity
                .ok(eventPackageMapper.mapToEventPackageResponseDto(eventPackageUpdated));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventPackage(@PathVariable Long id) {

        eventPackageService.deleteEventPackageById(id);

        return ResponseEntity
                .noContent().build();
    }
}

