package com.mycloud.file_service.controller;

import com.mycloud.common_models.common_entities.InitiateUploadRequestEntity;
import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.file_service.service.UploadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping("/initiate")
    public ApiResponseDto<String> initiateFileUpload(@RequestBody InitiateUploadRequestEntity request) {
        try {
            return uploadService.DoInitiateFileUpload(request);
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An internal error was occurred.");
        }
    }

    @PostMapping("/chunk")
    public ApiResponseDto<Boolean> uploadChunk(
            HttpServletRequest request,
            @RequestHeader("X-Upload-Id") String uploadId,
            @RequestHeader("X-Chunk-Index") int chunkIndex,
            @RequestHeader("X-Total-Chunks") int totalChunks) {
        try {
            InputStream inputStream = request.getInputStream();

            return uploadService.DoSaveChunk(inputStream, uploadId, chunkIndex, totalChunks);
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An internal error was occurred.");
        }
    }
}