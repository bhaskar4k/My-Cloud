package com.mycloud.core_service.seeder;

import com.mycloud.common_models.database_entities.TMenuMaster;
import com.mycloud.common_models.database_entities.TRoleMaster;
import com.mycloud.common_models.database_entities.TRoleMenuMapping;
import com.mycloud.data_access_layer.repositories.TMenuMasterRepository;
import com.mycloud.data_access_layer.repositories.TRoleMasterRepository;
import com.mycloud.data_access_layer.repositories.TRoleMenuMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {
    private final TMenuMasterRepository menuRepository;
    private final TRoleMasterRepository roleRepository;
    private final TRoleMenuMappingRepository mappingRepository;

    @Override
    public void run(String... args) {
        SeedRoles();
        SeedMenus();
        SeedRoleMenuMappings();
    }

    // =========================================================
    // ROLES
    // =========================================================
    private void SeedRoles() {
        if (roleRepository.count() > 0) {
            return;
        }

        List<TRoleMaster> roles = List.of(

                TRoleMaster.builder()
                        .roleName("Unauthorized")
                        .description("Unauthorized i.e user didn't login")
                        .active(true)
                        .build(),

                TRoleMaster.builder()
                        .roleName("BRONZE")
                        .description("Bronze")
                        .active(true)
                        .build(),

                TRoleMaster.builder()
                        .roleName("SILVER")
                        .description("Silver")
                        .active(true)
                        .build(),

                TRoleMaster.builder()
                        .roleName("GOLD")
                        .description("Gold")
                        .active(true)
                        .build(),

                TRoleMaster.builder()
                        .roleName("PLATINUM")
                        .description("Platinum")
                        .active(true)
                        .build(),

                TRoleMaster.builder()
                        .roleName("DIAMOND")
                        .description("Diamond")
                        .active(true)
                        .build()
        );

        roleRepository.saveAll(roles);
    }



    // =========================================================
    // MENUS
    // =========================================================
    private void SeedMenus() {
        if (menuRepository.count() > 0) {
            return;
        }

        List<TMenuMaster> menus = List.of(
                TMenuMaster.builder()
                        .parentId(null)
                        .label("Dashboard")
                        .icon("bi bi-house")
                        .route("/dashboard")
                        .displayOrder(1)
                        .active(true)
                        .build(),

                TMenuMaster.builder()
                        .parentId(null)
                        .label("Content")
                        .icon("bi bi-diagram-3")
                        .route("/content")
                        .displayOrder(2)
                        .active(true)
                        .build(),

                TMenuMaster.builder()
                        .parentId(null)
                        .label("Library")
                        .icon("bi bi-collection")
                        .route("/library")
                        .displayOrder(3)
                        .active(true)
                        .build(),

                TMenuMaster.builder()
                        .parentId(null)
                        .label("Favourite")
                        .icon("bi bi-star-fill")
                        .route("/favourite")
                        .displayOrder(4)
                        .active(true)
                        .build(),

                TMenuMaster.builder()
                        .parentId(null)
                        .label("Settings")
                        .icon("bi bi-gear")
                        .route(null)
                        .displayOrder(5)
                        .active(true)
                        .build(),

                TMenuMaster.builder()
                        .parentId(null)
                        .label("Profile")
                        .icon("bi bi-person-circle")
                        .route("/profile")
                        .displayOrder(6)
                        .active(true)
                        .build(),

                TMenuMaster.builder()
                        .parentId(null)
                        .label("Login")
                        .icon("bi bi-box-arrow-in-right")
                        .route("/login")
                        .displayOrder(7)
                        .active(true)
                        .build(),

                TMenuMaster.builder()
                        .parentId(null)
                        .label("Register")
                        .icon("bi bi-person-add")
                        .route("/register")
                        .displayOrder(8)
                        .active(true)
                        .build(),

                TMenuMaster.builder()
                        .parentId(null)
                        .label("Logout")
                        .icon("bi bi-box-arrow-left")
                        .route("/logout")
                        .displayOrder(9)
                        .active(true)
                        .build(),

                // SETTINGS CHILD MENUS (parentId = Settings = 5)
                TMenuMaster.builder()
                        .parentId(5)
                        .label("Profile Settings")
                        .icon("bi bi-person-gear")
                        .route("/settings/profile-settings")
                        .displayOrder(1)
                        .active(true)
                        .build(),

                TMenuMaster.builder()
                        .parentId(5)
                        .label("Basic Settings")
                        .icon("bi bi-sliders2")
                        .route("/settings/basic-settings")
                        .displayOrder(2)
                        .active(true)
                        .build(),

                TMenuMaster.builder()
                        .parentId(5)
                        .label("Delete Account")
                        .icon("bi bi-person-slash")
                        .route("/settings/delete-account")
                        .displayOrder(3)
                        .active(true)
                        .build()
        );

        menuRepository.saveAll(menus);
    }



    // =========================================================
    // ROLE MENU MAPPINGS
    // =========================================================
    private void SeedRoleMenuMappings() {
        if (mappingRepository.count() > 0) {
            return;
        }

        List<TRoleMenuMapping> mappings = List.of(
                // Unauthorized
                TRoleMenuMapping.builder()
                        .menuMasterId(1L)
                        .roleMasterId(1L)
                        .build(),

                TRoleMenuMapping.builder()
                        .menuMasterId(7L)   // Login
                        .roleMasterId(1L)
                        .build(),

                TRoleMenuMapping.builder()
                        .menuMasterId(8L)   // Register
                        .roleMasterId(1L)
                        .build(),

                // Bronze
                TRoleMenuMapping.builder()
                        .menuMasterId(1L)   // Dashboard
                        .roleMasterId(2L)
                        .build(),

                TRoleMenuMapping.builder()
                        .menuMasterId(2L)   // Content
                        .roleMasterId(2L)
                        .build(),

                TRoleMenuMapping.builder()
                        .menuMasterId(3L)   // Library
                        .roleMasterId(2L)
                        .build(),

                TRoleMenuMapping.builder()
                        .menuMasterId(4L)   // Favourite
                        .roleMasterId(2L)
                        .build(),

                TRoleMenuMapping.builder()
                        .menuMasterId(5L)   // Settings
                        .roleMasterId(2L)
                        .build(),

                TRoleMenuMapping.builder()
                        .menuMasterId(6L)   // Profile
                        .roleMasterId(2L)
                        .build(),

                TRoleMenuMapping.builder()
                        .menuMasterId(9L)   // Logout
                        .roleMasterId(2L)
                        .build(),

                TRoleMenuMapping.builder()
                        .menuMasterId(10L)  // Profile Settings
                        .roleMasterId(2L)
                        .build(),

                TRoleMenuMapping.builder()
                        .menuMasterId(11L)  // Basic Settings
                        .roleMasterId(2L)
                        .build(),

                TRoleMenuMapping.builder()
                        .menuMasterId(11L)  // Basic Settings (duplicate preserved)
                        .roleMasterId(2L)
                        .build(),

                TRoleMenuMapping.builder()
                        .menuMasterId(12L)  // Delete Account
                        .roleMasterId(2L)
                        .build()
        );

        mappingRepository.saveAll(mappings);
    }
}