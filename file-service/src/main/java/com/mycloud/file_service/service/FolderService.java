package com.mycloud.file_service.service;

import com.mycloud.common_config.model.JwtConfig;
import com.mycloud.common_models.common_constants.CommonConstants;
import com.mycloud.common_models.common_entities.FileInformationEntity;
import com.mycloud.common_models.common_entities.FolderDetailsEntity;
import com.mycloud.common_models.common_entities.FolderInfoEntity;
import com.mycloud.common_models.common_entities.JwtUser;
import com.mycloud.common_models.database_entities.TFolderMaster;
import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.common_models.utils.DatetimeUtil;
import com.mycloud.common_models.utils.EncryptionUtil;
import com.mycloud.common_models.utils.JwtUtil;
import com.mycloud.data_access_layer.repositories.TFolderMasterRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class FolderService {
    private final JwtUtil jwtUtil;
    private final TFolderMasterRepository folderRepository;
    private final EncryptionUtil encryptionUtil;
    DateTimeFormatter dateTimeFormatter;

    public FolderService(JwtConfig jwtConfig, TFolderMasterRepository folderRepository) {
        this.jwtUtil = new JwtUtil(jwtConfig.getSecret(), jwtConfig.getExpiration());
        this.folderRepository = folderRepository;
        this.encryptionUtil = new EncryptionUtil(jwtConfig.getSecret());
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");
    }


    public ApiResponseDto<FolderInfoEntity[]> DoValidateFolderAccess(String FolderId) {
        try {
            JwtUser user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(500, "Access denied. Please login again.");
            }

            if (FolderId.toUpperCase().equals(CommonConstants.UserRootFolderName)) {
                return ApiResponseDto.Success("Folder access validation is done successfully.",
                        new FolderInfoEntity[] { new FolderInfoEntity(CommonConstants.UserRootFolderName.toLowerCase(), CommonConstants.UserRootFolderName) });
            }

            String DecryptedFolderId = FolderId;
            try {
                DecryptedFolderId = encryptionUtil.DecryptHexEncoding(FolderId);
            } catch (RuntimeException ex) {
                throw new IllegalArgumentException("The folder you're trying to access is an invalid folder.");
            }

            long ActualFolderId = -1L;

            try {
                ActualFolderId = Long.parseLong(DecryptedFolderId);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("The folder you're trying to access is an invalid folder.");
            }

            Optional<TFolderMaster> UserFolder = folderRepository.findByIdAndUserIdAndDeletedFalse(ActualFolderId, user.userId());

            TFolderMaster ExistedFolder = null;
            if (UserFolder.isPresent()) {
                ExistedFolder = UserFolder.get();
            } else {
                throw new IllegalArgumentException("The folder you're trying to access is an invalid folder.");
            }

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



    private TFolderMaster GetCurrentFolderInfoFromFolderId(Long UserId, String FolderId){
        Optional<TFolderMaster> CurrentFolder;

        if (FolderId.toUpperCase().equals(CommonConstants.UserRootFolderName)) {
            CurrentFolder = folderRepository.findByUserIdAndDeletedAndDepth(UserId, false, 1);
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

            CurrentFolder = folderRepository.findByIdAndUserIdAndDeletedFalse(ActualFolderId, UserId);
        }

        if (CurrentFolder.isEmpty()) {
            throw new IllegalArgumentException("The folder you're trying to access is an invalid folder.");
        }

        return CurrentFolder.get();
    }



    @Transactional
    public ApiResponseDto<FolderInfoEntity> DoCreateFolder(FolderInfoEntity FolderInfo) {
        try {
            JwtUser user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(500, "Access denied. Please login again.");
            }

            TFolderMaster CurrentFetchedFolder = GetCurrentFolderInfoFromFolderId(user.userId(), FolderInfo.getFolderId());

            TFolderMaster NewFolder = new TFolderMaster();
            NewFolder.setDepth(CurrentFetchedFolder.getDepth() + 1);
            NewFolder.setName(FolderInfo.getFolderName());
            NewFolder.setParentFolderId(CurrentFetchedFolder.getId());
            NewFolder.setUserId(user.userId());
            NewFolder.setPath(CurrentFetchedFolder.getPath());

            TFolderMaster CreatedFolder = folderRepository.save(NewFolder);

            CreatedFolder.setPath(CreatedFolder.getPath() + "," + CreatedFolder.getId().toString());
            folderRepository.save(CreatedFolder);

            FolderInfoEntity Output = new FolderInfoEntity();
            Output.setFolderId(encryptionUtil.EncryptHexEncoding(CreatedFolder.getId().toString()));
            Output.setFolderName(CreatedFolder.getName());
            Output.setDepth(CreatedFolder.getDepth());

            if (CreatedFolder.getCreatedAt() != null) {
                Output.setCreatedAt(CreatedFolder.getCreatedAt().format(dateTimeFormatter));
            }

            return ApiResponseDto.Success("Folder has been created successfully.", Output);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to create folder.");
        }
    }



    public ApiResponseDto<FolderDetailsEntity> DoGetAllFolders(String FolderId) {
        try {
            JwtUser user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(500, "Access denied. Please login again.");
            }

            TFolderMaster CurrentFolder = GetCurrentFolderInfoFromFolderId(user.userId(), FolderId);

            List<TFolderMaster> ChildFolders = folderRepository.findByParentFolderIdAndUserIdAndDeleted(CurrentFolder.getId(), user.userId(), false);

            FolderDetailsEntity Output = new FolderDetailsEntity();
            Output.HasFolder = !ChildFolders.isEmpty();
            Output.FolderCount = ChildFolders.size();

            Output.FoldersList = ChildFolders.stream()
                    .map(folder -> {
                        FolderInfoEntity dto = new FolderInfoEntity();
                        dto.setFolderId(encryptionUtil.EncryptHexEncoding(folder.getId().toString()));
                        dto.setFolderName(folder.getName());
                        dto.setDepth(folder.getDepth());

                        if (folder.getCreatedAt() != null) {
                            dto.setCreatedAt(folder.getCreatedAt().format(dateTimeFormatter));
                        }

                        return dto;
                    })
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
}
