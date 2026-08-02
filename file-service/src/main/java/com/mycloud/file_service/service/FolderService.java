package com.mycloud.file_service.service;

import com.mycloud.common_config.model.JwtConfig;
import com.mycloud.common_models.common_constants.CommonConstants;
import com.mycloud.common_models.common_entities.FileInformationEntity;
import com.mycloud.common_models.common_entities.JwtUser;
import com.mycloud.common_models.database_entities.TFileMaster;
import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.common_models.utils.DatetimeUtil;
import com.mycloud.common_models.utils.EncryptionUtil;
import com.mycloud.common_models.utils.JwtUtil;
import com.mycloud.data_access_layer.repositories.TFolderMasterRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

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

    public ApiResponseDto<Boolean> DoValidateFolderAccess(String FolderId) {
        try {
            JwtUser user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(500, "Access denied. Please login again.");
            }

            String DecryptedFolderId = FolderId;
            if (!FolderId.toUpperCase().equals(CommonConstants.UserRootFolderName)){
                try {
                    DecryptedFolderId = encryptionUtil.Decrypt(FolderId);
                } catch (RuntimeException ex) {
                    throw new IllegalArgumentException("The folder you're trying to access is an invalid folder.");
                }
            }

            Long ActualFolderId = -1L;

            try {
                ActualFolderId = Long.valueOf(DecryptedFolderId);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("The folder you're trying to access is an invalid folder.");
            }

            Boolean IsValidFolder = folderRepository.existsByIdAndUserId(ActualFolderId, user.userId());

            return ApiResponseDto.Success("Folder access validation is done successfully.", IsValidFolder);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to validate folder access.");
        }
    }
}
