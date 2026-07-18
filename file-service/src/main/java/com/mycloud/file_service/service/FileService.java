package com.mycloud.file_service.service;

import com.mycloud.common_config.model.JwtConfig;
import com.mycloud.common_models.common_entities.FileInformationEntity;
import com.mycloud.common_models.common_entities.JwtUser;
import com.mycloud.common_models.database_entities.TFileMaster;
import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.common_models.utils.JwtUtil;
import com.mycloud.common_models.utils.DatetimeUtil;
import com.mycloud.data_access_layer.repositories.TFileMasterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class FileService {
    private final JwtUtil jwtUtil;
    private final TFileMasterRepository fileMasterRepository;

    public FileService(JwtConfig jwtConfig, TFileMasterRepository fileMasterRepository) throws IOException {
        this.jwtUtil = new JwtUtil(jwtConfig.getSecret(), jwtConfig.getExpiration());
        this.fileMasterRepository = fileMasterRepository;
    }

    public ApiResponseDto<List<FileInformationEntity>> DoGetAllFileListByUserId() {
        try {
            JwtUser user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(500, "Access denied. Please login again.");
            }

            List<TFileMaster> files = fileMasterRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(user.userId());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss");

            List<FileInformationEntity> Output = files.stream()
                    .map(file -> {
                        FileInformationEntity dto = new FileInformationEntity();
                        dto.setId(file.getId().intValue());
                        dto.setFileId(file.getFileId());
                        dto.setOriginalName(file.getOriginalName());
                        dto.setFileExtension(file.getFileExtension());
                        dto.setFileSize(file.getFileSize());

                        if (file.getCreatedAt() != null) {
                            dto.setCreatedAt(file.getCreatedAt().format(formatter));
                            dto.setUploadedAgo(DatetimeUtil.GetUploadedAgo(file.getCreatedAt()));
                        }

                        return dto;
                    })
                    .toList();

            return ApiResponseDto.Success("Files retrieved successfully.", Output);
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to retrieve file list.");
        }
    }
}
