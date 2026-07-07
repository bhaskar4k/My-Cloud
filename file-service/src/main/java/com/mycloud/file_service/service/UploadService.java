package com.mycloud.file_service.service;

import com.mycloud.common_config.model.JwtConfig;
import com.mycloud.common_models.common_entities.JwtUser;
import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.common_models.utils.JwtUtil;
import org.springframework.stereotype.Service;


@Service
public class UploadService {
    private final JwtUtil jwtUtil;

    public UploadService(JwtConfig jwtConfig) {
        this.jwtUtil = new JwtUtil(jwtConfig.getSecret(), jwtConfig.getExpiration());
    }

    public ApiResponseDto<Boolean> DoInitiateFileUpload() {
        try {
            JwtUser User = jwtUtil.GetCurrentUser();

            return ApiResponseDto.Success(
                    "Menus fetched successfully",
                    true
            );

        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(
                    500,
                    ex.getMessage()
            );
        }
    }
}
