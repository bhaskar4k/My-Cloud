package com.mycloud.core_service.gateway;

import com.mycloud.common_config.model.GatewayConfig;
import com.mycloud.common_config.model.JwtConfig;
import com.mycloud.common_models.enums.ServiceName;
import com.mycloud.common_models.utils.JwtUtil; // Injected to read user data
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
public class FileGatewayController {
    private final GatewayConfig gatewayConfig;
    private final JwtUtil jwtUtil;
    private final Map<String, String> services;

    public FileGatewayController(JwtConfig jwtConfig, GatewayConfig gatewayConfig) {
        this.jwtUtil = new JwtUtil(jwtConfig.getSecret(), jwtConfig.getExpiration());
        this.gatewayConfig = gatewayConfig;
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
            path = "/**",
            method = {
                    RequestMethod.GET,
                    RequestMethod.POST,
                    RequestMethod.PUT,
                    RequestMethod.DELETE,
                    RequestMethod.PATCH
            }
    )
    public void proxyFileRequest(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        HttpURLConnection connection = null;

        try {
            String baseUrl = gatewayConfig.getFile();

            if (baseUrl == null) {
                response.setStatus(500);
                response.getWriter().write("File service not configured");
                return;
            }

            String requestUri = request.getRequestURI();
            String path = requestUri.substring("/api/file".length());
            String targetUrl = baseUrl + path;

            if (request.getQueryString() != null) {
                targetUrl += "?" + request.getQueryString();
            }

            System.out.println("Forwarding File Request To: " + targetUrl);

            URL url = new URL(targetUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(request.getMethod());
            connection.setDoInput(true);

            if (!"GET".equalsIgnoreCase(request.getMethod())) {
                connection.setDoOutput(true);
            }

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(300000);

            // 1. Copy original headers via your existing utility
            GatewayUtils.copyHeaders(request, connection);

            // 2. Case-insensitive Extraction & Header Spoofing Protection
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

                        // Inject verified user contexts into the connection
                        connection.setRequestProperty("X-User-Id", String.valueOf(userId));
                        connection.setRequestProperty("X-User-Email", email);
                        connection.setRequestProperty("X-User-Authenticated", "true");

                        hasValidToken = true;
                    }
                } catch (Exception e) {
                    System.err.println("Failed to propagate user context headers in File Gateway: " + e.getMessage());
                }
            }

            // 3. Explicitly overwrite spoofed data if request is unauthenticated/public
            if (!hasValidToken) {
                connection.setRequestProperty("X-User-Authenticated", "false");
                connection.setRequestProperty("X-User-Id", "");
                connection.setRequestProperty("X-User-Email", "");
            }

            // --- Streaming Upload ---
            if (!"GET".equalsIgnoreCase(request.getMethod())) {
                try (
                        InputStream clientInput = request.getInputStream();
                        OutputStream serviceOutput = connection.getOutputStream()
                ) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = clientInput.read(buffer)) != -1) {
                        serviceOutput.write(buffer, 0, bytesRead);
                    }
                    serviceOutput.flush();
                }
            }

            int responseCode = connection.getResponseCode();
            response.setStatus(responseCode);

            // Copy response headers back to the client
            connection.getHeaderFields().forEach((key, values) -> {
                if (key != null && values != null) {
                    String lowerKey = key.toLowerCase();
                    // Prevent forwarding of hop-by-hop and location headers
                    if (!lowerKey.equals("connection")
                            && !lowerKey.equals("keep-alive")
                            && !lowerKey.equals("transfer-encoding")
                            && !lowerKey.equals("location")) {
                        for (String value : values) {
                            response.addHeader(key, value);
                        }
                    }
                }
            });

            // --- Streaming Download ---
            InputStream serviceInput = responseCode >= 400
                    ? connection.getErrorStream()
                    : connection.getInputStream();

            if (serviceInput != null) {
                try (OutputStream clientOutput = response.getOutputStream()) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = serviceInput.read(buffer)) != -1) {
                        clientOutput.write(buffer, 0, bytesRead);
                    }
                    clientOutput.flush();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            try {
                response.getWriter().write("An internal error was occurred.");
            } catch (Exception ignored) {}
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}