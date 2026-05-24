package com.mycloud.core_service.controller;
import com.mycloud.common_config.model.JwtConfig;
import com.mycloud.common_models.database_entities.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("core")
public class CoreController {
    private final JwtConfig jwtConfig;

    public CoreController(
            JwtConfig jwtConfig
    ) {
        this.jwtConfig = jwtConfig;
    }

    @GetMapping("/test")
    public String test() {
        User user = new User();
        return "Expiration Time -> " + jwtConfig.getExpiration();
    }
}
