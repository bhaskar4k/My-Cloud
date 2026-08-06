package com.mycloud.auth_service.controller;

import com.mycloud.auth_service.service.UserService;
import com.mycloud.common_models.database_entities.TUserMaster;
import com.mycloud.common_models.dto.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

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
            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An internal error was occurred.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<String>> LoginUser(@RequestBody TUserMaster user) {

        try {
            ApiResponseDto<String> response = userService.DoLoginUser(user);

            if (!response.isSuccess()) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            String jwt = response.getData(); // assuming the JWT is in data

            ResponseCookie cookie = ResponseCookie.from("MY_CLOUD_COOKIE", jwt)
                    .httpOnly(true)
                    .secure(false)      // true when using HTTPS
                    .path("/")
                    .sameSite("Lax")
                    .maxAge(Duration.ofDays(7))
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.Error(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "An internal error was occurred."));
        }
    }
}
