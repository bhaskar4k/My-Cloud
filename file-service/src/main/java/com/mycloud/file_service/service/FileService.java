package com.mycloud.file_service.service;

import com.mycloud.common_config.model.JwtConfig;
import com.mycloud.common_models.common_constants.CommonConstants;
import com.mycloud.common_models.common_entities.*;
import com.mycloud.common_models.database_entities.TFileMaster;
import com.mycloud.common_models.database_entities.TFolderMaster;
import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.common_models.enums.UploadStatus;
import com.mycloud.common_models.utils.EncryptionUtil;
import com.mycloud.common_models.utils.JwtUtil;
import com.mycloud.common_models.utils.DatetimeUtil;
import com.mycloud.data_access_layer.repositories.TFileMasterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@Service
public class FileService {
    private final JwtUtil jwtUtil;
    private final TFileMasterRepository fileMasterRepository;
    private final FolderService folderService;
    private final EncryptionUtil encryptionUtil;

    public FileService(JwtConfig jwtConfig, TFileMasterRepository fileMasterRepository, FolderService folderService) throws IOException {
        this.jwtUtil = new JwtUtil(jwtConfig.getSecret(), jwtConfig.getExpiration());
        this.fileMasterRepository = fileMasterRepository;
        this.folderService = folderService;
        this.encryptionUtil = new EncryptionUtil(jwtConfig.getSecret());
    }


    public TFileMaster GetCurrentFileInfoFromFileIdAndUploadStatus(Long UserId, String FileId, UploadStatus Status){
        Optional<TFileMaster> CurrentFile;

        try {
            FileId = encryptionUtil.DecryptHexEncoding(FileId);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("The file you're trying to access is an invalid file.");
        }

        long ActualFolderId = -1L;
        try {
            ActualFolderId = Long.parseLong(FileId);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("The file you're trying to access is an invalid file.");
        }

        CurrentFile = fileMasterRepository.findByIdAndUserIdAndDeletedFalseAndStatus(ActualFolderId, UserId, Status);

        if (CurrentFile.isEmpty()) {
            throw new IllegalArgumentException("The file you're trying to access is an invalid file.");
        }

        return CurrentFile.get();
    }


    private FileInformationEntity GetFileInformationDto(TFileMaster File) {
        try {
            FileInformationEntity dto = new FileInformationEntity();
            dto.setFileId(encryptionUtil.EncryptHexEncoding(File.getId().toString()));
            dto.setOriginalName(File.getOriginalName());
            dto.setFileExtension(File.getFileExtension());
            dto.setContentType(File.getContentType());
            dto.setFileSize(File.getFileSize());

            if (File.getCreatedAt() != null) {
                dto.setCreatedAt(File.getCreatedAt().format(DatetimeUtil.DateTimeShortMonthFormatter));
                dto.setUploadedAgo(DatetimeUtil.GetUploadedAgo(File.getCreatedAt()));
                dto.setModifiedAt(File.getUpdatedAt().format(DatetimeUtil.DateTimeShortMonthFormatter));
            }

            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public ApiResponseDto<FileDetailsEntity> DoGetAllFileListByUserId(String FolderId) {
        try {
            if (FolderId == null || FolderId.isEmpty()){
                return ApiResponseDto.Error(HttpStatus.BAD_REQUEST.value(), "Invalid Payload.");
            }

            JwtUser user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(HttpStatus.UNAUTHORIZED.value(), "Access denied. Please login again.");
            }

            TFolderMaster CurrentFolder = folderService.GetCurrentFolderInfoFromFolderId(user.userId(), FolderId);

            List<TFileMaster> files = fileMasterRepository.findByUserIdAndParentFolderIdAndDeletedAndStatusOrderByCreatedAtDesc(
                    user.userId(),
                    CurrentFolder.getId(),
                    false,
                    UploadStatus.COMPLETED
            );

            FileDetailsEntity Output = new FileDetailsEntity();
            Output.HasFile = !files.isEmpty();
            Output.FileCount = files.size();

            Output.FilesList = files.stream()
                    .map(this::GetFileInformationDto)
                    .toList();

            return ApiResponseDto.Success("File list has been fetched successfully.", Output);
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch all files list.");
        }
    }


    @Transactional
    public ApiResponseDto<FileInformationEntity> DoRenameFile(FileRenameInputEntity File) {
        try {
            if (File == null || File.getFileId() == null || File.getFileId().isEmpty() ||
                    File.getUpdatedFileName() == null || File.getUpdatedFileName().isEmpty()){
                return ApiResponseDto.Error(HttpStatus.BAD_REQUEST.value(), "Invalid Payload.");
            }

            JwtUser user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(HttpStatus.UNAUTHORIZED.value(), "Access denied. Please login again.");
            }

            TFileMaster CurrentFile = GetCurrentFileInfoFromFileIdAndUploadStatus(user.userId(), File.getFileId(), UploadStatus.COMPLETED);
            CurrentFile.setOriginalName(File.getUpdatedFileName());
            fileMasterRepository.save(CurrentFile);

            CurrentFile = GetCurrentFileInfoFromFileIdAndUploadStatus(user.userId(), File.getFileId(), UploadStatus.COMPLETED);

            return ApiResponseDto.Success("File has been renamed successfully.", GetFileInformationDto(CurrentFile));
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to rename this file.");
        }
    }


    @Transactional
    public ApiResponseDto<FileInformationEntity> DoDelete(FileDeleteInputEntity File) {
        try {
            if (File == null || File.getFileId() == null || File.getFileId().isEmpty()){
                return ApiResponseDto.Error(HttpStatus.BAD_REQUEST.value(), "Invalid Payload.");
            }

            JwtUser user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(HttpStatus.UNAUTHORIZED.value(), "Access denied. Please login again.");
            }

//            TFileMaster CurrentFile = GetCurrentFileInfoFromFileIdAndUploadStatus(user.userId(), File.getFileId(), UploadStatus.COMPLETED);
//            CurrentFile.setOriginalName(File.getUpdatedFileName());
//            fileMasterRepository.save(CurrentFile);
//
//            CurrentFile = GetCurrentFileInfoFromFileIdAndUploadStatus(user.userId(), File.getFileId(), UploadStatus.COMPLETED);

            return ApiResponseDto.Success("File has been renamed successfully.", null);
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to rename this file.");
        }
    }
}
