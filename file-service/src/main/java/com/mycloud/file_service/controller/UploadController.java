package com.mycloud.file_service.controller;

import com.mycloud.common_models.common_entities.InitiateUploadRequestEntity;
import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.file_service.service.UploadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
            return ApiResponseDto.Error(500, ex.getMessage());
        }
    }

    @PostMapping("/chunk")
    public ApiResponseDto<String> uploadChunk(
            HttpServletRequest request,
            @RequestHeader("X-Upload-Id") String uploadId,
            @RequestHeader("X-Chunk-Index") int chunkIndex,
            @RequestHeader("X-Total-Chunks") int totalChunks) {
        try {
            // Read binary body directly as InputStream
            InputStream inputStream = request.getInputStream();

            uploadService.DoSaveChunk(inputStream, uploadId, chunkIndex, totalChunks);

            return ApiResponseDto.Success("Chunk " + chunkIndex + " uploaded successfully", null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ApiResponseDto.Error(500, "Failed to upload chunk: " + ex.getMessage());
        }
    }
}