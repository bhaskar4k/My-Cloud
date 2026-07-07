package com.mycloud.common_service.controller;

import com.mycloud.common_models.common_entities.MenuItemEntity;
import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.common_service.service.MenuService;
import com.mycloud.data_access_layer.repositories.TMenuMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final TMenuMasterRepository menuRepository;
    private final MenuService menuService;

    @GetMapping("/get-menu")
    public ApiResponseDto<List<MenuItemEntity>> GetMenusByRole() {
        try {
            return menuService.DoGetMenusByRole();
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(
                    500,
                    ex.getMessage()
            );
        }
    }
}