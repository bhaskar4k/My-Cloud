package com.mycloud.core_service.gateway;

import com.mycloud.common_config.model.GatewayConfig;
import com.mycloud.common_models.enums.ServiceName;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileGatewayController {

    private final GatewayConfig gatewayConfig;
    private final Map<String, String> services = new HashMap<>();

    @PostConstruct
    public void init() {
        services.put(ServiceName.AUTH.getValue(), gatewayConfig.getAuth());
        services.put(ServiceName.COMMON.getValue(), gatewayConfig.getCommon());
        services.put(ServiceName.FILE.getValue(), gatewayConfig.getFile());
        services.put(ServiceName.PROCESSING.getValue(), gatewayConfig.getProcessing());

        System.out.println(
                "Loaded Services: " + services
        );
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

                response.getWriter()
                        .write("File service not configured");

                return;
            }

            String requestUri =
                    request.getRequestURI();

            String path =
                    requestUri.substring(
                            "/api/file".length()
                    );

            String targetUrl =
                    baseUrl + path;

            if (request.getQueryString() != null) {
                targetUrl += "?" + request.getQueryString();
            }

            System.out.println(
                    "Forwarding File Request To: " + targetUrl
            );

            URL url = new URL(targetUrl);

            connection =
                    (HttpURLConnection)
                            url.openConnection();

            connection.setRequestMethod(
                    request.getMethod()
            );

            connection.setDoInput(true);

            if (!"GET".equalsIgnoreCase(request.getMethod())) {

                connection.setDoOutput(true);
            }

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(300000);

            GatewayUtils.copyHeaders(
                    request,
                    connection
            );

            // Upload body
            if (!"GET".equalsIgnoreCase(request.getMethod())) {
                try (
                        InputStream clientInput =
                                request.getInputStream();

                        OutputStream serviceOutput =
                                connection.getOutputStream()
                ) {

                    byte[] buffer = new byte[8192];

                    int bytesRead;

                    while ((bytesRead =
                            clientInput.read(buffer)) != -1) {

                        serviceOutput.write(
                                buffer,
                                0,
                                bytesRead
                        );
                    }

                    serviceOutput.flush();
                }
            }

            int responseCode =
                    connection.getResponseCode();

            response.setStatus(responseCode);

            // Copy response headers
            connection.getHeaderFields()
                    .forEach((key, values) -> {

                        if (key != null && values != null) {

                            for (String value : values) {

                                response.addHeader(
                                        key,
                                        value
                                );
                            }
                        }
                    });

            InputStream serviceInput =
                    responseCode >= 400
                            ? connection.getErrorStream()
                            : connection.getInputStream();

            if (serviceInput != null) {
                try (
                        OutputStream clientOutput =
                                response.getOutputStream()
                ) {
                    byte[] buffer = new byte[8192];

                    int bytesRead;

                    while ((bytesRead =
                            serviceInput.read(buffer)) != -1) {

                        clientOutput.write(
                                buffer,
                                0,
                                bytesRead
                        );
                    }

                    clientOutput.flush();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            response.setStatus(500);

            try {
                response.getWriter()
                        .write(ex.getMessage());

            } catch (Exception ignored) {
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}