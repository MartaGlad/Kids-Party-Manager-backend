package com.gladysz.kidspartymanager.controller;

import com.gladysz.kidspartymanager.domain.EventPackage;
import com.gladysz.kidspartymanager.domain.pricing.PricingResult;
import com.gladysz.kidspartymanager.dto.pricing.PricingRequestDto;
import com.gladysz.kidspartymanager.service.EventPackageService;
import com.gladysz.kidspartymanager.service.PricingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final EventPackageService eventPackageService;
    private final PricingService pricingService;

    @PostMapping("/preview")
    public ResponseEntity<PricingResult> previewPrice(
            @Valid @RequestBody PricingRequestDto pricingRequestDto) {

        EventPackage eventPackage = eventPackageService
                .getEventPackageById(pricingRequestDto.eventPackageId());

        PricingResult pricingResult = pricingService
                .getPricingResult(eventPackage, pricingRequestDto.childrenCount(), pricingRequestDto.date());

        return ResponseEntity.ok(pricingResult);
    }
}
