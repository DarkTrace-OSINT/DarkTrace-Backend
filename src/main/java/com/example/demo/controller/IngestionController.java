package com.example.demo.controller;

import com.example.demo.dto.request.IngestionRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.IngestionResponse;
import com.example.demo.service.ParsingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ingestion")
@RequiredArgsConstructor
public class IngestionController {

    private final ParsingService parsingService;

    @PostMapping("/raw")
    public ApiResponse<IngestionResponse> ingestRawData(@RequestBody IngestionRequest request) {
        // 데이터 수신 및 실시간 분석 시작
        return ApiResponse.success(parsingService.processRawData(request));
    }
}