package com.gladysz.kidspartymanager.controller;

import com.gladysz.kidspartymanager.domain.Animator;
import com.gladysz.kidspartymanager.dto.animator.AnimatorCreateDto;
import com.gladysz.kidspartymanager.dto.animator.AnimatorResponseDto;
import com.gladysz.kidspartymanager.dto.animator.AnimatorUpdateDto;
import com.gladysz.kidspartymanager.mapper.AnimatorMapper;
import com.gladysz.kidspartymanager.service.AnimatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/animators")
@RequiredArgsConstructor
public class AnimatorController {

    private final AnimatorService animatorService;
    private final AnimatorMapper animatorMapper;

    @PostMapping
    public ResponseEntity<AnimatorResponseDto> addAnimator(@Valid @RequestBody AnimatorCreateDto animatorCreateDto) {

        Animator animator = animatorService.createAnimator(animatorMapper.mapToAnimator(animatorCreateDto));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(animator.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(animatorMapper.mapToAnimatorResponseDto(animator));
    }


    @GetMapping("/{id}")
    public ResponseEntity<AnimatorResponseDto> getAnimator(@PathVariable Long id) {

        return ResponseEntity
                .ok(animatorMapper.mapToAnimatorResponseDto(animatorService.getAnimatorById(id)));
    }


    @GetMapping
    public ResponseEntity<List<AnimatorResponseDto>> getAllAnimators() {

        return ResponseEntity
                .ok(animatorMapper.mapToAnimatorResponseDtoList(animatorService.getAllAnimators()));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<AnimatorResponseDto> updateAnimator(
            @PathVariable Long id,
            @Valid @RequestBody AnimatorUpdateDto animatorUpdateDto) {

        Animator animatorUpdated = animatorService.updateAnimator(id, animatorUpdateDto);

        return ResponseEntity
                .ok(animatorMapper.mapToAnimatorResponseDto(animatorUpdated));
    }


    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<AnimatorResponseDto> deactivateAnimator(@PathVariable Long id) {

        Animator animatorDeactivated = animatorService.deactivateAnimatorById(id);

        return ResponseEntity.ok(animatorMapper.mapToAnimatorResponseDto(animatorDeactivated));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnimator(@PathVariable Long id) {

        animatorService.deleteAnimatorById(id);

        return ResponseEntity
                .noContent().build();
    }
}
