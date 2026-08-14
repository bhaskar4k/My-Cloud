package com.mycloud.file_service.service;

import com.mycloud.common_config.model.JwtConfig;
import com.mycloud.common_config.model.StorageConfig;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class FileService {
    private final JwtUtil jwtUtil;
    private final TFileMasterRepository fileMasterRepository;
    private final FolderService folderService;
    private final EncryptionUtil encryptionUtil;
    private final Long AutoDeleteTimeInDays;

    public FileService(JwtConfig jwtConfig, StorageConfig storageConfig, TFileMasterRepository fileMasterRepository, FolderService folderService) throws IOException {
        this.jwtUtil = new JwtUtil(jwtConfig.getSecret(), jwtConfig.getExpiration());
        this.fileMasterRepository = fileMasterRepository;
        this.folderService = folderService;
        this.encryptionUtil = new EncryptionUtil(jwtConfig.getSecret());
        this.AutoDeleteTimeInDays = storageConfig.getAutoDeleteTimeInDays();
    }


    // UTIL :: START
    // ===========================
    public TFileMaster GetCurrentFileInfoFromFileIdAndUploadStatusAndDeleted(Long UserId, String FileId, UploadStatus Status, boolean Deleted){
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

        CurrentFile = fileMasterRepository.findByIdAndUserIdAndDeletedAndStatus(ActualFolderId, UserId, Deleted, Status);

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
            dto.setFavourite(File.getFavourite());
            dto.setDeleted(File.getDeleted());

            if (File.getCreatedAt() != null) {
                dto.setCreatedAt(File.getCreatedAt().format(DatetimeUtil.DateTimeShortMonthFormatter));
                dto.setUploadedAgo(DatetimeUtil.GetUploadedAgo(File.getCreatedAt()));
            }

            if (File.getUpdatedAt() != null) {
                dto.setModifiedAt(File.getUpdatedAt().format(DatetimeUtil.DateTimeShortMonthFormatter));
            }

            if (File.getDeleted()) {
                if (File.getDeletedAt() != null) {
                    dto.setDeletedAt(File.getDeletedAt().format(DatetimeUtil.DateTimeShortMonthFormatter));
                }

                if (File.getAutoDeleteAt() != null) {
                    dto.setAutoDeletingAt(DatetimeUtil.GetAutoDeletionText(File.getAutoDeleteAt()));
                }
            }

            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // ===========================
    // UTIL :: END



    // BUSINESS :: START
    // ===========================
    public ApiResponseDto<FileInformationEntity> DoGetFileInfoByFileGuid(String FileGuid) {
        try {
            if (FileGuid == null || FileGuid.isEmpty()){
                return ApiResponseDto.Error(HttpStatus.BAD_REQUEST.value(), "Invalid Payload.");
            }

            JwtUser user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(HttpStatus.UNAUTHORIZED.value(), "Access denied. Please login again.");
            }

            Optional<TFileMaster> FileMaster = fileMasterRepository.findByFileIdAndUserIdAndDeletedAndStatus(
                    FileGuid,
                    user.userId(),
                    false,
                    UploadStatus.COMPLETED
            );

            if (FileMaster.isEmpty()) {
                return ApiResponseDto.Error(HttpStatus.BAD_REQUEST.value(), "The file you're trying to access is an invalid file.");
            }

            return ApiResponseDto.Success("File information has been fetched successfully.", GetFileInformationDto(FileMaster.get()));
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch file information.");
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

            TFolderMaster CurrentFolder = folderService.GetCurrentFolderInfoFromFolderId(user.userId(), FolderId, false);

            List<TFileMaster> files = fileMasterRepository.findByUserIdAndParentFolderIdAndDeletedAndStatus(
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


    // CRUD OPERATIONS :: START
    // ===========================
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

            TFileMaster CurrentFile = GetCurrentFileInfoFromFileIdAndUploadStatusAndDeleted(user.userId(), File.getFileId(), UploadStatus.COMPLETED, false);

            if (CurrentFile.getOriginalName().equals(File.getUpdatedFileName())){
                return ApiResponseDto.Success("New file name is same as existing file name.<br>Skipping...", GetFileInformationDto(CurrentFile));
            }

            CurrentFile.setOriginalName(File.getUpdatedFileName());
            fileMasterRepository.save(CurrentFile);

            CurrentFile = GetCurrentFileInfoFromFileIdAndUploadStatusAndDeleted(user.userId(), File.getFileId(), UploadStatus.COMPLETED, false);

            return ApiResponseDto.Success("File has been renamed successfully.", GetFileInformationDto(CurrentFile));
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to rename this file.");
        }
    }


    @Transactional
    public ApiResponseDto<Boolean> DoDelete(FileDeleteInputEntity File) {
        try {
            if (File == null || File.getFileId() == null || File.getFileId().isEmpty()){
                return ApiResponseDto.Error(HttpStatus.BAD_REQUEST.value(), "Invalid Payload.");
            }

            JwtUser user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(HttpStatus.UNAUTHORIZED.value(), "Access denied. Please login again.");
            }

            TFileMaster CurrentFile = GetCurrentFileInfoFromFileIdAndUploadStatusAndDeleted(user.userId(), File.getFileId(), UploadStatus.COMPLETED, false);

            long AutoDeleteTime = Instant.now().getEpochSecond() + this.AutoDeleteTimeInDays * 86400L;

            CurrentFile.setDeleted(true);
            CurrentFile.setDeletedAt(LocalDateTime.now());
            CurrentFile.setAutoDeleteAt(AutoDeleteTime);
            fileMasterRepository.save(CurrentFile);

            return ApiResponseDto.Success("File has been successfully moved into recycle bin.<br>It will be auto deleted from recycle bin after 30 days.", true);
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to delete this file.", false);
        }
    }


    @Transactional
    public ApiResponseDto<FileInformationEntity> DoUpdateFavourite(FileFavouriteInputEntity File) {
        try {
            if (File == null || File.getFileId() == null || File.getFileId().isEmpty() || File.getFavourite() == null){
                return ApiResponseDto.Error(HttpStatus.BAD_REQUEST.value(), "Invalid Payload.");
            }

            JwtUser user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(HttpStatus.UNAUTHORIZED.value(), "Access denied. Please login again.");
            }

            TFileMaster CurrentFile = GetCurrentFileInfoFromFileIdAndUploadStatusAndDeleted(user.userId(), File.getFileId(), UploadStatus.COMPLETED, false);

            CurrentFile.setFavourite(File.getFavourite());
            fileMasterRepository.save(CurrentFile);

            CurrentFile = GetCurrentFileInfoFromFileIdAndUploadStatusAndDeleted(user.userId(), File.getFileId(), UploadStatus.COMPLETED, false);

            String ReturnMessage = "File has been successfully marked as favourite.";
            if (!File.getFavourite()) {
                ReturnMessage = "File has been successfully un-marked from favourite.";
            }

            return ApiResponseDto.Success(ReturnMessage, GetFileInformationDto(CurrentFile));
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to update favourite status of this file.");
        }
    }
    // ===========================
    // CRUD OPERATIONS :: END

    // ===========================
    // BUSINESS :: END
}
