package com.mycloud.common_service.controller;

import com.mycloud.common_models.database_entities.TMenuMaster;
import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.data_access_layer.repositories.TMenuMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final TMenuMasterRepository menuRepository;

    @GetMapping("/get-menu/{roleId}")
    public ApiResponseDto<List<TMenuMaster>> getMenusByRole(@PathVariable Long roleId) {
        try {
            List<TMenuMaster> menus =
                    menuRepository.findMenusByRoleId(roleId);

            return ApiResponseDto.Success(
                    "Menus fetched successfully",
                    menus
            );

        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(
                    500,
                    ex.getMessage()
            );
        }
    }

    @GetMapping("/check")
    public ApiResponseDto<String> getMenusByRole() {
        try {
            return ApiResponseDto.Success(
                    "Menus fetched successfully",
                    "GG"
            );
        } catch (Exception ex) {
            return ApiResponseDto.Error(
                    500,
                    ex.getMessage()
            );
        }
    }
}