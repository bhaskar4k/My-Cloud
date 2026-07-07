package com.mycloud.core_service.gateway;

import com.mycloud.common_config.model.GatewayConfig;
import com.mycloud.common_config.model.JwtConfig;
import com.mycloud.common_models.enums.ServiceName;
import com.mycloud.common_models.utils.JwtUtil;
import com.mycloud.data_access_layer.repositories.TMenuMasterRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiGatewayController {
    private final JwtUtil jwtUtil;
    private final GatewayConfig gatewayConfig;
    private final RestClient restClient;
    private final Map<String, String> services;

    public ApiGatewayController(JwtConfig jwtConfig, GatewayConfig gatewayConfig, RestClient restClient) {
        this.jwtUtil = new JwtUtil(jwtConfig.getSecret(), jwtConfig.getExpiration());
        this.gatewayConfig = gatewayConfig;
        this.restClient = restClient;
        this.services = new HashMap<>();
    }

    @PostConstruct
    public void init() {
        services.put(ServiceName.AUTH.getValue(), gatewayConfig.getAuth());
        services.put(ServiceName.COMMON.getValue(), gatewayConfig.getCommon());
        services.put(ServiceName.FILE.getValue(), gatewayConfig.getFile());
        services.put(ServiceName.PROCESSING.getValue(), gatewayConfig.getProcessing());

        System.out.println("Loaded Services: " + services);
    }

    @RequestMapping(
            path = "/{serviceName}/**",
            method = {
                    RequestMethod.GET,
                    RequestMethod.POST,
                    RequestMethod.PUT,
                    RequestMethod.DELETE,
                    RequestMethod.PATCH
            }
    )
    public ResponseEntity<?> proxyRequest(
            @PathVariable String serviceName,
            HttpServletRequest request
    ) {
        try {
            // Skip file routes
            if ("file".equalsIgnoreCase(serviceName)) {
                return ResponseEntity.notFound().build();
            }

            if (services.isEmpty()) {
                return ResponseEntity.internalServerError()
                        .body("Gateway services configuration is empty");
            }

            String serviceUrl =
                    services.get(
                            serviceName.toLowerCase()
                    );

            if (serviceUrl == null) {
                return ResponseEntity.badRequest()
                        .body("Invalid service: " + serviceName);
            }

            HttpMethod method =
                    HttpMethod.valueOf(
                            request.getMethod()
                    );

            String requestUri = request.getRequestURI();

            String prefix = "/api/" + serviceName;

            String path =
                    requestUri.substring(
                            prefix.length()
                    );

            String targetUrl = serviceUrl + path;

            if (request.getQueryString() != null) {
                targetUrl += "?" + request.getQueryString();
            }

            System.out.println(
                    "Forwarding To: " + targetUrl
            );

            RestClient.RequestBodySpec requestSpec =
                    restClient.method(method)
                            .uri(targetUrl);

            // Copy headers
            request.getHeaderNames()
                    .asIterator()
                    .forEachRemaining(headerName -> {
                        if (!"host".equalsIgnoreCase(headerName)) {
                            requestSpec.header(
                                    headerName,
                                    request.getHeader(headerName)
                            );
                        }
                    });

            // Check both variations of the header due to potential client-side normalization
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null) {
                authHeader = request.getHeader("authorization");
            }

            boolean hasValidToken = false;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    if (jwtUtil.ValidateToken(token)) {
                        Long userId = jwtUtil.ExtractUserId(token);
                        String email = jwtUtil.ExtractEmail(token);

                        // 1. Inject legitimate, verified identity headers
                        requestSpec.header("X-User-Id", String.valueOf(userId));
                        requestSpec.header("X-User-Email", email);
                        requestSpec.header("X-User-Authenticated", "true");

                        hasValidToken = true;
                    }
                } catch (Exception e) {
                    System.err.println("Failed to propagate user context headers: " + e.getMessage());
                }
            }

            // 2. If no valid token found, explicitly explicitly mark it as unauthenticated
            if (!hasValidToken) {
                requestSpec.header("X-User-Authenticated", "false");
                // Ensure downstream doesn't read junk/spoofed values if any were sent by client
                requestSpec.header("X-User-Id", "");
                requestSpec.header("X-User-Email", "");
            }

            ResponseEntity<String> response;

            // GET / DELETE
            if (method == HttpMethod.GET || method == HttpMethod.DELETE) {
                response = requestSpec.retrieve()
                        .toEntity(String.class);
            }

            // POST / PUT / PATCH
            else {
                String requestBody = StreamUtils.copyToString(
                        request.getInputStream(), StandardCharsets.UTF_8);

                response = requestSpec.body(requestBody)
                        .retrieve()
                        .toEntity(String.class);
            }

            // --- NEW: never let a downstream redirect leak an internal host to the browser ---
            if (response.getStatusCode().is3xxRedirection()) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"message\": \"Session timed out. Please login again.\"}");
            }

            // --- NEW: strip hop-by-hop / connection-specific headers before echoing them back ---
            HttpHeaders filteredHeaders = new HttpHeaders();
            response.getHeaders().forEach((key, values) -> {
                String lower = key.toLowerCase();
                if (!lower.equals("connection")
                        && !lower.equals("keep-alive")
                        && !lower.equals("transfer-encoding")
                        && !lower.equals("content-length")
                        && !lower.equals("location")) { // never forward internal Location headers
                    filteredHeaders.put(key, values);
                }
            });

            return ResponseEntity
                    .status(response.getStatusCode())
                    .headers(filteredHeaders)
                    .body(response.getBody());

        } catch (Exception ex) {
            ex.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(ex.getMessage());
        }
    }
}