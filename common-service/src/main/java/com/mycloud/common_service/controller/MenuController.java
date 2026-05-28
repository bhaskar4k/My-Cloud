package com.mycloud.common_service.controller;

import com.mycloud.common_models.database_entities.TMenuMaster;
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
    public ResponseEntity<?> getMenusByRole(
            @PathVariable Long roleId
    ) {

        try {

            List<TMenuMaster> menus =
                    menuRepository.findMenusByRoleId(roleId);

            return ResponseEntity.ok(menus);

        } catch (Exception ex) {

            return ResponseEntity.internalServerError()
                    .body(ex.getMessage());
        }
    }
}