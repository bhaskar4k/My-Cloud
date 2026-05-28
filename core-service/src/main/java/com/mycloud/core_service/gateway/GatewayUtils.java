package com.mycloud.core_service.gateway;

import jakarta.servlet.http.HttpServletRequest;

import java.net.HttpURLConnection;
import java.util.Enumeration;

public class GatewayUtils {

    public static void copyHeaders(
            HttpServletRequest request,
            HttpURLConnection connection
    ) {

        Enumeration<String> headerNames =
                request.getHeaderNames();

        while (headerNames.hasMoreElements()) {

            String headerName =
                    headerNames.nextElement();

            if ("host".equalsIgnoreCase(headerName)) {
                continue;
            }

            connection.setRequestProperty(
                    headerName,
                    request.getHeader(headerName)
            );
        }
    }
}