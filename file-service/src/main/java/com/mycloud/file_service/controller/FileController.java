package com.mycloud.file_service.controller;

import com.mycloud.common_models.common_entities.FileDetailsEntity;
import com.mycloud.common_models.common_entities.FileInformationEntity;
import com.mycloud.common_models.common_entities.FileRenameInputEntity;
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

    @GetMapping("/get-all/{FolderId}")
    public ApiResponseDto<FileDetailsEntity> GetAll(@PathVariable String FolderId) {
        try {
            return fileService.DoGetAllFileListByUserId(FolderId);
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An internal error was occurred.");
        }
    }

    @PostMapping("/rename")
    public ApiResponseDto<FileInformationEntity> Rename(@RequestBody FileRenameInputEntity File) {
        try {
            return fileService.DoRenameFile(File);
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An internal error was occurred.");
        }
    }
}
