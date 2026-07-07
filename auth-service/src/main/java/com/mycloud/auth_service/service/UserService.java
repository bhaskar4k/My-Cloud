package com.mycloud.auth_service.service;

import com.mycloud.common_config.model.JwtConfig;
import com.mycloud.common_models.database_entities.TUserMaster;
import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.common_models.utils.JwtUtil;
import com.mycloud.data_access_layer.repositories.TUserMasterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserService {
    private final JwtUtil jwtUtil;
    private final TUserMasterRepository userRepository;

    public UserService(JwtConfig jwtConfig, TUserMasterRepository userRepository) {
        this.jwtUtil = new JwtUtil(jwtConfig.getSecret(), jwtConfig.getExpiration());
        this.userRepository = userRepository;
    }

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

    public ApiResponseDto<String> DoLoginUser(TUserMaster user) {
        try {
            Optional<TUserMaster> existingUser = userRepository.findByEmail(user.getEmail());

            if (existingUser.isEmpty()) {
                return ApiResponseDto.Error(
                        HttpStatus.BAD_REQUEST.value(),
                        "No user exists with this email."
                );
            }

            TUserMaster dbUser = existingUser.get();

            if (Boolean.FALSE.equals(dbUser.getActive())) {
                return ApiResponseDto.Error(
                        HttpStatus.BAD_REQUEST.value(),
                        "Your account is inactive."
                );
            }

            if (Boolean.TRUE.equals(dbUser.getDeleted())) {
                return ApiResponseDto.Error(
                        HttpStatus.BAD_REQUEST.value(),
                        "Your account has been deleted."
                );
            }

            if (!dbUser.getPassword().equals(user.getPassword())) {
                return ApiResponseDto.Error(
                        HttpStatus.BAD_REQUEST.value(),
                        "You've typed an incorrect password."
                );
            }

            String JwtToken = jwtUtil.GenerateToken(dbUser.getId(), dbUser.getEmail());

            return ApiResponseDto.Success(
                    "You've been logged-in successfully.",
                    JwtToken
            );

        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "An internal error occurred."
            );
        }
    }
}
