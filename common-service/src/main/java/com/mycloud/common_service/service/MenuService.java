package com.mycloud.common_service.service;

import com.mycloud.common_config.model.JwtConfig;
import com.mycloud.common_models.common_entities.JwtUser;
import com.mycloud.common_models.common_entities.MenuItemEntity;
import com.mycloud.common_models.database_entities.TMenuMaster;
import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.common_models.utils.JwtUtil;
import com.mycloud.data_access_layer.repositories.TMenuMasterRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class MenuService {
    private final JwtUtil jwtUtil;
    private final TMenuMasterRepository menuRepository;

    public MenuService(JwtConfig jwtConfig, TMenuMasterRepository menuRepository) {
        this.jwtUtil = new JwtUtil(jwtConfig.getSecret(), jwtConfig.getExpiration());
        this.menuRepository = menuRepository;
    }

    public ApiResponseDto<List<MenuItemEntity>> DoGetMenusByRole() {
        try {
            JwtUser User = jwtUtil.GetCurrentUser();

            Long RoleId = null;
            if (User == null || !User.IsAuthenticated()){
                RoleId = 1L;
            } else {
                RoleId = 2L;
            }

            List<TMenuMaster> Menus = menuRepository.findMenusByRoleId(RoleId);

            List<MenuItemEntity> FinalOutput = new ArrayList<>();

            // Menus
            for (TMenuMaster menu : Menus) {
                if (menu.getParentId() != null) continue;

                MenuItemEntity AMenu = new MenuItemEntity();
                AMenu.id = menu.getId().intValue();
                AMenu.icon = menu.getIcon();
                AMenu.label = menu.getLabel();
                AMenu.route = menu.getRoute();

                if (menu.getParentId() == null) AMenu.submenu = new ArrayList<>();
                FinalOutput.add(AMenu);
            }

            // SubMenus
            for (TMenuMaster submenu : Menus) {
                if (submenu.getParentId() == null) continue;

                for (MenuItemEntity parent : FinalOutput) {
                    if (Objects.equals(parent.getId(), submenu.getParentId())){
                        MenuItemEntity ASubMenu = new MenuItemEntity();
                        ASubMenu.id = submenu.getId().intValue();
                        ASubMenu.icon = submenu.getIcon();
                        ASubMenu.label = submenu.getLabel();
                        ASubMenu.route = submenu.getRoute();
                        ASubMenu.submenu = new ArrayList<>();
                        parent.submenu.add(ASubMenu);
                    }
                }
            }

            return ApiResponseDto.Success("Menus fetched successfully", FinalOutput);
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(
                    500,
                    ex.getMessage()
            );
        }
    }
}
