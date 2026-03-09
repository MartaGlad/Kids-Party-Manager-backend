package com.gladysz.kidspartymanager.controller;


import com.gladysz.kidspartymanager.domain.EventPackage;
import com.gladysz.kidspartymanager.dto.eventpackage.EventPackageCreateDto;
import com.gladysz.kidspartymanager.dto.eventpackage.EventPackagePutDto;
import com.gladysz.kidspartymanager.dto.eventpackage.EventPackageResponseDto;
import com.gladysz.kidspartymanager.dto.eventpackage.EventPackagePatchDto;
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


    @PutMapping("/{id}")
    public ResponseEntity<EventPackageResponseDto> updatePutEventPackage(
            @PathVariable Long id,
            @Valid @RequestBody EventPackagePutDto eventPackagePutDto) {

        EventPackage eventPackageUpdated = eventPackageService
                .updatePutEventPackage(id, eventPackagePutDto);

        return ResponseEntity
                .ok(eventPackageMapper.mapToEventPackageResponseDto(eventPackageUpdated));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<EventPackageResponseDto> updatePatchEventPackage(
            @PathVariable Long id,
            @Valid @RequestBody EventPackagePatchDto eventPackagePatchDto) {

        EventPackage eventPackageUpdated = eventPackageService
                .updatePatchEventPackage(id, eventPackagePatchDto);

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

