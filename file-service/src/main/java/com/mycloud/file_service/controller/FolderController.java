package com.mycloud.file_service.controller;

import com.mycloud.common_models.common_entities.*;
import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.file_service.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/folder")
@RequiredArgsConstructor
public class FolderController {
    private final FolderService folderService;

    @GetMapping("/validate-folder-access/{FolderId}")
    public ApiResponseDto<FolderInfoEntity[]> ValidateFolderAccess(@PathVariable String FolderId) {
        try {
            return folderService.DoValidateFolderAccess(FolderId);
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An internal error was occurred.");
        }
    }

    @PostMapping("/create")
    public ApiResponseDto<FolderInfoEntity> Create(@RequestBody FolderInfoEntity FolderInfo) {
        try {
            return folderService.DoCreateFolder(FolderInfo);
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An internal error was occurred.");
        }
    }

    @GetMapping("/get-all/{FolderId}")
    public ApiResponseDto<FolderDetailsEntity> GetALl(@PathVariable String FolderId) {
        try {
            return folderService.DoGetAllFolders(FolderId);
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An internal error was occurred.");
        }
    }

    @PostMapping("/rename")
    public ApiResponseDto<FolderInfoEntity> Rename(@RequestBody FolderRenameInputEntity Folder) {
        try {
            return folderService.DoRenameFolder(Folder);
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An internal error was occurred.");
        }
    }

    @DeleteMapping("/delete")
    public ApiResponseDto<Boolean> Delete(@RequestBody FolderDeleteInputEntity Folder) {
        try {
            return folderService.DoDelete(Folder);
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An internal error was occurred.", false);
        }
    }

    @PostMapping("/update-favourite")
    public ApiResponseDto<FolderInfoEntity> UpdateFavourite(@RequestBody FolderFavouriteInputEntity Folder) {
        try {
            return folderService.DoUpdateFavourite(Folder);
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An internal error was occurred.");
        }
    }
}
