package com.mycloud.common_service.service;

import com.mycloud.common_models.common_entities.MenuItemEntity;
import com.mycloud.common_models.database_entities.TMenuMaster;
import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.data_access_layer.repositories.TMenuMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class MenuService {
    private final TMenuMasterRepository menuRepository;

    public ApiResponseDto<List<MenuItemEntity>> DoGetMenusByRole(Long RoleId) {
        try {
            List<TMenuMaster> Menus = menuRepository.findMenusByRoleId(RoleId);

            List<MenuItemEntity> FinalOutput = new ArrayList<>();

            // Menus
            for (TMenuMaster menu : Menus) {
                if (menu.getParentId() != null) continue;

                MenuItemEntity AMenu = new MenuItemEntity();
                AMenu.Id = menu.getId().intValue();
                AMenu.Icon = menu.getIcon();
                AMenu.Label = menu.getLabel();
                AMenu.Route = menu.getRoute();

                if (menu.getParentId() == null) AMenu.Submenu = new ArrayList<>();
                FinalOutput.add(AMenu);
            }

            // SubMenus
            for (TMenuMaster submenu : Menus) {
                if (submenu.getParentId() == null) continue;

                for (MenuItemEntity parent : FinalOutput) {
                    if (Objects.equals(parent.getId(), submenu.getParentId())){
                        MenuItemEntity ASubMenu = new MenuItemEntity();
                        ASubMenu.Id = submenu.getId().intValue();
                        ASubMenu.Icon = submenu.getIcon();
                        ASubMenu.Label = submenu.getLabel();
                        ASubMenu.Route = submenu.getRoute();
                        ASubMenu.Submenu = new ArrayList<>();

                        parent.Submenu.add(ASubMenu);
                    }
                }
            }

            return ApiResponseDto.Success(
                    "Menus fetched successfully",
                    FinalOutput
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
