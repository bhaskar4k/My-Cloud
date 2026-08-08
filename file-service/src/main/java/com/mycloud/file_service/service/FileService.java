package com.mycloud.file_service.service;

import com.mycloud.common_config.model.JwtConfig;
import com.mycloud.common_models.common_constants.CommonConstants;
import com.mycloud.common_models.common_entities.FileDetailsEntity;
import com.mycloud.common_models.common_entities.FileInformationEntity;
import com.mycloud.common_models.common_entities.JwtUser;
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


    public ApiResponseDto<FileDetailsEntity> DoGetAllFileListByUserId(String FolderId) {
        try {
            JwtUser user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(500, "Access denied. Please login again.");
            }

            TFolderMaster CurrentFolder = folderService.GetCurrentFolderInfoFromFolderId(user.userId(), FolderId);

            List<TFileMaster> files = fileMasterRepository.findByUserIdAndParentFolderIdAndDeletedFalseOrderByCreatedAtDesc(user.userId(), CurrentFolder.getId());

            FileDetailsEntity Output = new FileDetailsEntity();
            Output.HasFile = !files.isEmpty();
            Output.FileCount = files.size();

            Output.FilesList = files.stream()
                    .map(file -> {
                        FileInformationEntity dto = new FileInformationEntity();
                        dto.setFileId(encryptionUtil.EncryptHexEncoding(file.getId().toString()));
                        dto.setOriginalName(file.getOriginalName());
                        dto.setFileExtension(file.getFileExtension());
                        dto.setFileSize(file.getFileSize());

                        if (file.getCreatedAt() != null) {
                            dto.setCreatedAt(file.getCreatedAt().format(DatetimeUtil.DateTimeShortMonthFormatter));
                            dto.setUploadedAgo(DatetimeUtil.GetUploadedAgo(file.getCreatedAt()));
                        }

                        return dto;
                    })
                    .toList();

            return ApiResponseDto.Success("File list has been fetched successfully.", Output);
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch all files list.");
        }
    }
}
