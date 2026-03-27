package com.example.demo.controller;

import com.example.demo.dto.request.ActionUpdateRequest;
import com.example.demo.dto.request.ThreatSearchRequest;
import com.example.demo.dto.response.ActionUpdateResponse;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.ThreatSearchResponse;
import com.example.demo.service.DataProcessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/threats")
@RequiredArgsConstructor
public class ThreatController {

    private final DataProcessService dataProcessService;

    @PostMapping("/search")
    public ApiResponse<ThreatSearchResponse> searchThreats(@RequestBody ThreatSearchRequest request) {
        // [규칙 7] 공통 응답 규격 사용
        return ApiResponse.success(dataProcessService.searchThreats(request));
    }

    // [API 5] 위협 조치 업데이트
    @PatchMapping("/action")
    public ApiResponse<ActionUpdateResponse> updateAction(@Valid @RequestBody ActionUpdateRequest request) {
        return ApiResponse.success(dataProcessService.updateThreatAction(request));
    }
}