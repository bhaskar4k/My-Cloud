package com.mycloud.core_service.gateway;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiGatewayController {

    private final GatewayProperties gatewayProperties;
    private final RestClient restClient;

    @PostConstruct
    public void init() {
        System.out.println(
                "Loaded Services: " +
                        gatewayProperties.getServices()
        );
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

            Map<String, String> services =
                    gatewayProperties.getServices();

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

            ResponseEntity<String> response;

            // GET / DELETE
            if (method == HttpMethod.GET || method == HttpMethod.DELETE) {
                response =
                        requestSpec.retrieve()
                                .toEntity(String.class);
            }

            // POST / PUT / PATCH
            else {
                String requestBody =
                        StreamUtils.copyToString(
                                request.getInputStream(),
                                StandardCharsets.UTF_8
                        );

                response =
                        requestSpec.body(requestBody)
                                .retrieve()
                                .toEntity(String.class);
            }

            return ResponseEntity
                    .status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(response.getBody());

        } catch (Exception ex) {
            ex.printStackTrace();

            return ResponseEntity.internalServerError()
                    .body(ex.getMessage());
        }
    }
}