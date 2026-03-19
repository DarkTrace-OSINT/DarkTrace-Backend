package com.example.demo.controller;

import com.example.demo.dto.request.SettingRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.EngineStatusResponse;
import com.example.demo.dto.response.SettingResponse;
import com.example.demo.service.DataProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
public class EngineController {

    private final DataProcessService dataProcessService;

    @PostMapping("/engines")
    public ApiResponse<EngineStatusResponse> getEngineStatuses() {
        // [API 6] 명세서상 Request Body는 {} 이므로 인자 없음
        return ApiResponse.success(dataProcessService.getEngineStatuses());
    }

    // [API 7] 시스템 설정 수정
    @PutMapping("/settings")
    public ApiResponse<SettingResponse> updateSettings(@RequestBody SettingRequest request) {
        return ApiResponse.success(dataProcessService.updateSystemSettings(request));
    }
}