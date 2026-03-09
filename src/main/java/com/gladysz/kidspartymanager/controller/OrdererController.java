package com.gladysz.kidspartymanager.controller;

import com.gladysz.kidspartymanager.domain.Orderer;
import com.gladysz.kidspartymanager.dto.orderer.OrdererCreateDto;
import com.gladysz.kidspartymanager.dto.orderer.OrdererResponseDto;
import com.gladysz.kidspartymanager.dto.orderer.OrdererUpdateDto;
import com.gladysz.kidspartymanager.mapper.OrdererMapper;
import com.gladysz.kidspartymanager.service.OrdererService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orderers")
@RequiredArgsConstructor
public class OrdererController {

    private final OrdererService ordererService;
    private final OrdererMapper ordererMapper;

    @PostMapping
    public ResponseEntity<OrdererResponseDto> addOrderer(@Valid @RequestBody OrdererCreateDto ordererCreateDto) {

        Orderer orderer = ordererService.createOrderer(ordererMapper.mapToOrderer(ordererCreateDto));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(orderer.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(ordererMapper.mapToOrdererResponseDto(orderer));
    }


    @GetMapping("/{id}")
    public ResponseEntity<OrdererResponseDto> getOrderer(@PathVariable Long id) {

        return ResponseEntity
                .ok(ordererMapper.mapToOrdererResponseDto(ordererService.getOrdererById(id)));
    }


    @GetMapping
    public ResponseEntity<List<OrdererResponseDto>> getAllOrderers() {

        return ResponseEntity
                .ok(ordererMapper.mapToOrdererResponseDtoList(ordererService.getAllOrderers()));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<OrdererResponseDto> updateOrderer(
            @PathVariable Long id,
            @Valid @RequestBody OrdererUpdateDto ordererUpdateDto) {

        Orderer ordererUpdated = ordererService.updateOrderer(id, ordererUpdateDto);

        return ResponseEntity
                .ok(ordererMapper.mapToOrdererResponseDto(ordererUpdated));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderer(@PathVariable Long id) {

        ordererService.deleteOrdererById(id);

        return ResponseEntity
                .noContent().build();
    }
}

