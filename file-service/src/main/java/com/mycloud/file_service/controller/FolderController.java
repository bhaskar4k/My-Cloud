package com.mycloud.file_service.controller;

import com.mycloud.common_models.common_entities.FileInformationEntity;
import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.file_service.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/folder")
@RequiredArgsConstructor
public class FolderController {
    private final FolderService folderService;

    @GetMapping("/validate-folder-access/{FolderId}")
    public ApiResponseDto<String> ValidateFolderAccess(@PathVariable String FolderId) {
        try {
            return folderService.DoValidateFolderAccess(FolderId);
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An internal error was occurred.");
        }
    }
}
