package com.mycloud.auth_service.service;

import com.mycloud.common_models.database_entities.TUserMaster;
import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.data_access_layer.repositories.TUserMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class UserService {
    private final TUserMasterRepository userRepository;

    public ApiResponseDto<Boolean> DoCreateUser(TUserMaster User) {
        try {
            if (userRepository.existsByEmail(User.getEmail())) {
                return ApiResponseDto.Error(
                        HttpStatus.BAD_REQUEST.value(),
                        "An user with this email already exists."
                );
            }

            User.setActive(true);
            User.setDeleted(false);
            TUserMaster SavedUser = userRepository.save(User);

            return ApiResponseDto.Success(
                    "User has been registered successfully",
                    true
            );
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "An internal error was occurred."
            );
        }
    }
}
