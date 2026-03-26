package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.DashboardStatsResponse;
import com.example.demo.dto.response.RealtimeSummaryResponse;
import com.example.demo.service.DataProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DataProcessService dataProcessService;

    @PostMapping("/statistics")
    public ApiResponse<DashboardStatsResponse> getStatistics() {
        return ApiResponse.success(dataProcessService.getDashboardStatistics());
    }

    @PostMapping("/realtime")
    public ApiResponse<RealtimeSummaryResponse> getRealtimeSummary() {
        return ApiResponse.success(dataProcessService.getRealtimeSummary());
    }
}