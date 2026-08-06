package com.mycloud.file_service.controller;

import com.mycloud.common_models.database_entities.TFileMaster;
import com.mycloud.file_service.service.DownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/download")
@RequiredArgsConstructor
public class DownloadController {

    private final DownloadService downloadService;

    @GetMapping("/download/{fileId}")
    public ResponseEntity<ResourceRegion> download(
            @PathVariable String fileId,
            @RequestHeader HttpHeaders headers)
            throws IOException {

        return downloadService.DownloadFile(fileId, headers);
    }
}
