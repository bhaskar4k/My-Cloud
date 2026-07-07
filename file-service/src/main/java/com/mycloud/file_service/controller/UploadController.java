package com.mycloud.file_service.controller;


import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.data_access_layer.repositories.TMenuMasterRepository;
import com.mycloud.file_service.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {
    private final TMenuMasterRepository menuRepository;
    private final UploadService uploadService;

    @GetMapping("/initiate-file-upload")
    public ApiResponseDto<Boolean> InitiateFileUpload() {
        try {
            return uploadService.DoInitiateFileUpload();
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(
                    500,
                    ex.getMessage()
            );
        }
    }
}
