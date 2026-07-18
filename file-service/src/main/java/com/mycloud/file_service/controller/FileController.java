package com.mycloud.file_service.controller;

import com.mycloud.common_models.common_entities.FileInformationEntity;
import com.mycloud.common_models.common_entities.InitiateUploadRequestEntity;
import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.file_service.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/get-all")
    public ApiResponseDto<List<FileInformationEntity>> GetAll() {
        try {
            return fileService.DoGetAllFileListByUserId();
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An internal error was occurred.");
        }
    }
}
