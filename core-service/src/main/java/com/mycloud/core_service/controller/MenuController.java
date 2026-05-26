package com.mycloud.core_service.controller;

import com.mycloud.common_models.database_entities.TMenuMaster;
import com.mycloud.data_access_layer.repositories.TMenuMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("Menu")
@RequiredArgsConstructor
public class MenuController {

    private final TMenuMasterRepository menuRepository;

    @GetMapping("/GetMenu/{roleId}")
    public List<TMenuMaster> getMenusByRole(
            @PathVariable Long roleId
    ) {
        try {

            List<TMenuMaster> menus =
                    menuRepository.findMenusByRoleId(roleId);

            return menus;

        } catch (Exception ex) {
            return null;
        }
    }
}