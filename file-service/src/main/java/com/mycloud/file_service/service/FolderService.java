package com.mycloud.file_service.service;

import com.mycloud.common_config.model.JwtConfig;
import com.mycloud.common_models.common_constants.CommonConstants;
import com.mycloud.common_models.common_entities.FolderInfoEntity;
import com.mycloud.common_models.common_entities.JwtUser;
import com.mycloud.common_models.database_entities.TFolderMaster;
import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.common_models.utils.EncryptionUtil;
import com.mycloud.common_models.utils.JwtUtil;
import com.mycloud.data_access_layer.repositories.TFolderMasterRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FolderService {
    private final JwtUtil jwtUtil;
    private final TFolderMasterRepository folderRepository;
    private final EncryptionUtil encryptionUtil;

    public FolderService(JwtConfig jwtConfig, TFolderMasterRepository folderRepository) {
        this.jwtUtil = new JwtUtil(jwtConfig.getSecret(), jwtConfig.getExpiration());
        this.folderRepository = folderRepository;
        this.encryptionUtil = new EncryptionUtil(jwtConfig.getSecret());
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
                DecryptedFolderId = encryptionUtil.Decrypt(FolderId);
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
                    FolderPathFullInfo[i] = new FolderInfoEntity(FolderPathIds[i], CommonConstants.UserRootFolderName);
                    continue;
                }

                Optional<TFolderMaster> PathFolder = folderRepository.findById(Long.valueOf(FolderPathIds[i]));

                if (PathFolder.isPresent()) {
                    FolderPathFullInfo[i] = new FolderInfoEntity(encryptionUtil.Encrypt(FolderPathIds[i]), PathFolder.get().getName());
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


    @Transactional
    public ApiResponseDto<Boolean> DoCreateFolder(FolderInfoEntity FolderInfo) {
        try {
            JwtUser user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(500, "Access denied. Please login again.");
            }

            Optional<TFolderMaster> CurrentFolder;

            if (FolderInfo.getFolderId().toUpperCase().equals(CommonConstants.UserRootFolderName)) {
                CurrentFolder = folderRepository.findByUserIdAndDeletedAndDepth(user.userId(), false, 1);
            } else {
                try {
                    FolderInfo.setFolderId(encryptionUtil.Decrypt(FolderInfo.getFolderId()));
                } catch (RuntimeException ex) {
                    throw new IllegalArgumentException("The folder you're trying to access is an invalid folder.");
                }

                long ActualFolderId = -1L;
                try {
                    ActualFolderId = Long.parseLong(FolderInfo.getFolderId());
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("The folder you're trying to access is an invalid folder.");
                }

                CurrentFolder = folderRepository.findByIdAndUserIdAndDeletedFalse(ActualFolderId, user.userId());
            }

            if (CurrentFolder.isEmpty()) {
                throw new IllegalArgumentException("The folder you're trying to access is an invalid folder.");
            }

            TFolderMaster CurrentFetchedFolder = CurrentFolder.get();

            TFolderMaster NewFolder = new TFolderMaster();
            NewFolder.setDepth(CurrentFetchedFolder.getDepth() + 1);
            NewFolder.setName(FolderInfo.getFolderName());
            NewFolder.setParentFolderId(CurrentFetchedFolder.getId());
            NewFolder.setUserId(user.userId());
            NewFolder.setPath(CurrentFetchedFolder.getPath().toString());

            TFolderMaster CreatedFolder = folderRepository.save(NewFolder);

            CreatedFolder.setPath(CreatedFolder.getPath() + "," + CreatedFolder.getId().toString());
            folderRepository.save(CreatedFolder);

            return ApiResponseDto.Success("Folder has been created successfully.", true);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to create folder.");
        }
    }
}
