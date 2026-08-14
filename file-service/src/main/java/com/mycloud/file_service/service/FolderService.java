package com.mycloud.file_service.service;

import com.mycloud.common_config.model.JwtConfig;
import com.mycloud.common_config.model.StorageConfig;
import com.mycloud.common_models.common_constants.CommonConstants;
import com.mycloud.common_models.common_entities.*;
import com.mycloud.common_models.database_entities.TFileMaster;
import com.mycloud.common_models.database_entities.TFolderMaster;
import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.common_models.enums.UploadStatus;
import com.mycloud.common_models.utils.DatetimeUtil;
import com.mycloud.common_models.utils.EncryptionUtil;
import com.mycloud.common_models.utils.JwtUtil;
import com.mycloud.data_access_layer.repositories.TFolderMasterRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class FolderService {
    private final JwtUtil jwtUtil;
    private final TFolderMasterRepository folderRepository;
    private final EncryptionUtil encryptionUtil;
    private final Long AutoDeleteTimeInDays;

    public FolderService(JwtConfig jwtConfig, StorageConfig storageConfig, TFolderMasterRepository folderRepository) {
        this.jwtUtil = new JwtUtil(jwtConfig.getSecret(), jwtConfig.getExpiration());
        this.folderRepository = folderRepository;
        this.encryptionUtil = new EncryptionUtil(jwtConfig.getSecret());
        this.AutoDeleteTimeInDays = storageConfig.getAutoDeleteTimeInDays();
    }


    // UTIL :: START
    // ===========================
    public TFolderMaster GetCurrentFolderInfoFromFolderId(Long UserId, String FolderId, boolean Deleted){
        Optional<TFolderMaster> CurrentFolder;

        if (FolderId.toUpperCase().equals(CommonConstants.UserRootFolderName)) {
            CurrentFolder = folderRepository.findByUserIdAndDeletedAndDepth(UserId, Deleted, 1);
        } else {
            try {
                FolderId = encryptionUtil.DecryptHexEncoding(FolderId);
            } catch (RuntimeException ex) {
                throw new IllegalArgumentException("The folder you're trying to access is an invalid folder.");
            }

            long ActualFolderId = -1L;
            try {
                ActualFolderId = Long.parseLong(FolderId);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("The folder you're trying to access is an invalid folder.");
            }

            CurrentFolder = folderRepository.findByIdAndUserIdAndDeleted(ActualFolderId, UserId, Deleted);
        }

        if (CurrentFolder.isEmpty()) {
            throw new IllegalArgumentException("The folder you're trying to access is an invalid folder.");
        }

        return CurrentFolder.get();
    }


    private FolderInfoEntity GetFolderInformationDto(TFolderMaster Folder){
        FolderInfoEntity dto = new FolderInfoEntity();
        dto.setFolderId(encryptionUtil.EncryptHexEncoding(Folder.getId().toString()));
        dto.setFolderName(Folder.getName());
        dto.setDepth(Folder.getDepth() - 1); // Treating ROOT as 0 Depth
        dto.setSubFolderCount(0);
        dto.setFilesCount(0);
        dto.setTotalSize(0L);
        dto.setFavourite(Folder.getFavourite());
        dto.setDeleted(Folder.getDeleted());

        if (Folder.getCreatedAt() != null) {
            dto.setCreatedAt(Folder.getCreatedAt().format(DatetimeUtil.DateTimeShortMonthFormatter));
            dto.setCreatedAgo(DatetimeUtil.GetUploadedAgo(Folder.getCreatedAt()));
        }

        if (Folder.getUpdatedAt() != null) {
            dto.setModifiedAt(Folder.getUpdatedAt().format(DatetimeUtil.DateTimeShortMonthFormatter));
        }

        if (Folder.getDeleted()) {
            if (Folder.getDeletedAt() != null) {
                dto.setDeletedAt(Folder.getDeletedAt().format(DatetimeUtil.DateTimeShortMonthFormatter));
            }

            if (Folder.getAutoDeleteAt() != null) {
                dto.setAutoDeletingAt(DatetimeUtil.GetAutoDeletionText(Folder.getAutoDeleteAt()));
            }
        }

        return dto;
    }
    // ===========================
    // UTIL :: END



    // BUSINESS :: START
    // ===========================
    public ApiResponseDto<FolderInfoEntity[]> DoValidateFolderAccess(String FolderId) {
        try {
            if (FolderId == null || FolderId.isEmpty()){
                return ApiResponseDto.Error(HttpStatus.BAD_REQUEST.value(), "Invalid Payload.");
            }

            JwtUser user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(HttpStatus.UNAUTHORIZED.value(), "Access denied. Please login again.");
            }

            if (FolderId.toUpperCase().equals(CommonConstants.UserRootFolderName)) {
                return ApiResponseDto.Success("Folder access validation is done successfully.",
                        new FolderInfoEntity[] { new FolderInfoEntity(CommonConstants.UserRootFolderName.toLowerCase(), CommonConstants.UserRootFolderName) });
            }

            TFolderMaster ExistedFolder = GetCurrentFolderInfoFromFolderId(user.userId(), FolderId, false);

            String[] FolderPathIds = ExistedFolder.getPath().split(",");
            FolderInfoEntity[] FolderPathFullInfo = new FolderInfoEntity[FolderPathIds.length];

            for (int i = 0; i < FolderPathIds.length; i++) {
                if (i == 0) {
                    FolderPathFullInfo[i] = new FolderInfoEntity(FolderPathIds[i].toLowerCase(), CommonConstants.UserRootFolderName);
                    continue;
                }

                Optional<TFolderMaster> PathFolder = folderRepository.findById(Long.valueOf(FolderPathIds[i]));

                if (PathFolder.isPresent()) {
                    FolderPathFullInfo[i] = new FolderInfoEntity(encryptionUtil.EncryptHexEncoding(FolderPathIds[i]), PathFolder.get().getName());
                } else {
                    throw new IllegalArgumentException("The folder you're trying to access is an invalid folder.");
                }
            }

            return ApiResponseDto.Success("Folder access validation is done successfully.", FolderPathFullInfo);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to validate folder access.");
        }
    }


    public ApiResponseDto<FolderDetailsEntity> DoGetAllFolders(String FolderId) {
        try {
            if (FolderId == null || FolderId.isEmpty()){
                return ApiResponseDto.Error(HttpStatus.BAD_REQUEST.value(), "Invalid Payload.");
            }

            JwtUser user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(HttpStatus.UNAUTHORIZED.value(), "Access denied. Please login again.");
            }

            TFolderMaster CurrentFolder = GetCurrentFolderInfoFromFolderId(user.userId(), FolderId, false);

            List<TFolderMaster> ChildFolders = folderRepository.findByParentFolderIdAndUserIdAndDeleted(CurrentFolder.getId(), user.userId(), false);

            FolderDetailsEntity Output = new FolderDetailsEntity();
            Output.HasFolder = !ChildFolders.isEmpty();
            Output.FolderCount = ChildFolders.size();

            Output.FoldersList = ChildFolders.stream()
                    .map(this::GetFolderInformationDto)
                    .toList();

            return ApiResponseDto.Success("Folder list has been fetched successfully.", Output);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch all folders list.");
        }
    }


    // CRUD OPERATIONS :: START
    // ===========================
    @Transactional
    public ApiResponseDto<FolderInfoEntity> DoCreateFolder(FolderInfoEntity FolderInfo) {
        try {
            if (FolderInfo == null || FolderInfo.getFolderId() == null || FolderInfo.getFolderId().isEmpty() ||
                FolderInfo.getFolderName() == null || FolderInfo.getFolderName().isEmpty()){
                return ApiResponseDto.Error(HttpStatus.BAD_REQUEST.value(), "Invalid Payload.");
            }

            JwtUser user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(HttpStatus.UNAUTHORIZED.value(), "Access denied. Please login again.");
            }

            TFolderMaster CurrentFetchedFolder = GetCurrentFolderInfoFromFolderId(user.userId(), FolderInfo.getFolderId(), false);

            TFolderMaster NewFolder = new TFolderMaster();
            NewFolder.setDepth(CurrentFetchedFolder.getDepth() + 1);
            NewFolder.setName(FolderInfo.getFolderName());
            NewFolder.setParentFolderId(CurrentFetchedFolder.getId());
            NewFolder.setUserId(user.userId());
            NewFolder.setPath(CurrentFetchedFolder.getPath());
            NewFolder.setDeleted(false);
            NewFolder.setFavourite(false);

            TFolderMaster CreatedFolder = folderRepository.save(NewFolder);

            CreatedFolder.setPath(CreatedFolder.getPath() + "," + CreatedFolder.getId().toString());
            CreatedFolder = folderRepository.save(CreatedFolder);

            Optional<TFolderMaster> OutputFolder = folderRepository.findByIdAndUserIdAndDeleted(CreatedFolder.getId(), user.userId(), false);

            if (OutputFolder.isEmpty()) {
                return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Folder is created but failed to fetch folder information.");
            }

            return ApiResponseDto.Success("Folder has been created successfully.", GetFolderInformationDto(OutputFolder.get()));
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to create folder.");
        }
    }


    @Transactional
    public ApiResponseDto<FolderInfoEntity> DoRenameFolder(FolderRenameInputEntity Folder) {
        try {
            if (Folder == null || Folder.getFolderId() == null || Folder.getFolderId().isEmpty() ||
                    Folder.getUpdatedFolderName() == null || Folder.getUpdatedFolderName().isEmpty()){
                return ApiResponseDto.Error(HttpStatus.BAD_REQUEST.value(), "Invalid Payload.");
            }

            JwtUser user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(HttpStatus.UNAUTHORIZED.value(), "Access denied. Please login again.");
            }

            TFolderMaster CurrentFolder = GetCurrentFolderInfoFromFolderId(user.userId(), Folder.getFolderId(), false);

            if (CurrentFolder.getName().equals(Folder.getUpdatedFolderName())){
                return ApiResponseDto.Success("New folder name is same as existing folder name.<br>Skipping...", GetFolderInformationDto(CurrentFolder));
            }

            CurrentFolder.setName(Folder.getUpdatedFolderName());
            folderRepository.save(CurrentFolder);

            CurrentFolder = GetCurrentFolderInfoFromFolderId(user.userId(), Folder.getFolderId(), false);

            return ApiResponseDto.Success("Folder has been renamed successfully.", GetFolderInformationDto(CurrentFolder));
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to rename this folder.");
        }
    }


    @Transactional
    public ApiResponseDto<Boolean> DoDelete(FolderDeleteInputEntity Folder) {
        try {
            if (Folder == null || Folder.getFolderId() == null || Folder.getFolderId().isEmpty()){
                return ApiResponseDto.Error(HttpStatus.BAD_REQUEST.value(), "Invalid Payload.");
            }

            JwtUser user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(HttpStatus.UNAUTHORIZED.value(), "Access denied. Please login again.");
            }

            TFolderMaster CurrentFolder = GetCurrentFolderInfoFromFolderId(user.userId(), Folder.getFolderId(), false);

            long AutoDeleteTime = Instant.now().getEpochSecond() + this.AutoDeleteTimeInDays * 86400L;

            CurrentFolder.setDeleted(true);
            CurrentFolder.setDeletedAt(LocalDateTime.now());
            CurrentFolder.setAutoDeleteAt(AutoDeleteTime);
            folderRepository.save(CurrentFolder);

            return ApiResponseDto.Success("Folder has been successfully moved into recycle bin.<br>It will be auto deleted from recycle bin after "
                    + this.AutoDeleteTimeInDays + " days.", true);
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to delete this folder.", false);
        }
    }


    @Transactional
    public ApiResponseDto<FolderInfoEntity> DoUpdateFavourite(FolderFavouriteInputEntity Folder) {
        try {
            if (Folder == null || Folder.getFolderId() == null || Folder.getFolderId().isEmpty() || Folder.getFavourite() == null){
                return ApiResponseDto.Error(HttpStatus.BAD_REQUEST.value(), "Invalid Payload.");
            }

            JwtUser user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(HttpStatus.UNAUTHORIZED.value(), "Access denied. Please login again.");
            }

            TFolderMaster CurrentFolder = GetCurrentFolderInfoFromFolderId(user.userId(), Folder.getFolderId(), false);

            CurrentFolder.setFavourite(Folder.getFavourite());
            folderRepository.save(CurrentFolder);

            CurrentFolder = GetCurrentFolderInfoFromFolderId(user.userId(), Folder.getFolderId(), false);

            String ReturnMessage = "Folder has been successfully marked as your favourite.";
            if (!Folder.getFavourite()) {
                ReturnMessage = "Folder has been removed from your favourite list.";
            }

            return ApiResponseDto.Success(ReturnMessage, GetFolderInformationDto(CurrentFolder));
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to update favourite status of this folder.");
        }
    }
    // ===========================
    // CRUD OPERATIONS :: END

    // ===========================
    // BUSINESS :: END
}
