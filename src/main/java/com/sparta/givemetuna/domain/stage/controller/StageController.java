package com.sparta.givemetuna.domain.stage.controller;

import com.sparta.givemetuna.domain.security.UserDetailsImpl;
import com.sparta.givemetuna.domain.stage.dto.*;
import com.sparta.givemetuna.domain.stage.entity.Stage;
import com.sparta.givemetuna.domain.stage.service.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stages")
public class StageController {

	private final StageService stageService;

	@PostMapping("/{board_id}/stages")
	public ResponseEntity<CreateStageResponseDto> createStage(@PathVariable Long board_id,
															  @RequestBody CreateStageRequestDto requestDto,
															  @AuthenticationPrincipal UserDetailsImpl userDetails) {
		CreateStageResponseDto response = stageService.createStage(board_id, requestDto, userDetails.getUser());
		return ResponseEntity.ok().body(response);
	}

	//stage 수정
	@PatchMapping("/{stage_id}")
	public ResponseEntity<UpdateStageResponseDto> updateStage(@PathVariable Long stage_id,
							@RequestBody UpdateStageRequestDto requestDto,
							@AuthenticationPrincipal UserDetailsImpl userDetails) {

		UpdateStageResponseDto response = stageService.updateStage(stage_id, requestDto, userDetails.getUser());
		return ResponseEntity.ok().body(response);
	}

	// 페이징 및 정렬

	//stage 삭제
	@DeleteMapping("/{stage_id}")
	public ResponseEntity<DeleteStageResponseDto> deleteStage(@PathVariable Long stage_id,
															  @AuthenticationPrincipal UserDetailsImpl userDetails) {
		stageService.deleteStage(stage_id, userDetails.getUser());
		return ResponseEntity.ok().body(new DeleteStageResponseDto("Stage deleted successfully"));
	}

	@GetMapping("/{stage_id}")
	public void getStage() {

	}

	@GetMapping
	public void getStages() {

	}
}
