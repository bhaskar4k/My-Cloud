package com.mycloud.auth_service.controller;

import com.mycloud.auth_service.service.UserService;
import com.mycloud.common_models.database_entities.TUserMaster;
import com.mycloud.common_models.dto.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public ApiResponseDto<Boolean> CreateUser(@RequestBody TUserMaster User) {
        try {
            return userService.DoCreateUser(User);
        } catch (Exception ex) {
            return ApiResponseDto.Error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "An internal error was occurred."
            );
        }
    }

    @PostMapping("/login")
    public ApiResponseDto<String> LoginUser(@RequestBody TUserMaster User) {
        try {
            return userService.DoLoginUser(User);
        } catch (Exception ex) {
            return ApiResponseDto.Error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "An internal error was occurred."
            );
        }
    }
}
