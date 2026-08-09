package com.mycloud.file_service.service;

import com.mycloud.common_config.model.JwtConfig;
import com.mycloud.common_config.model.StorageConfig;
import com.mycloud.common_models.common_entities.JwtUser;
import com.mycloud.common_models.database_entities.TFileMaster;
import com.mycloud.common_models.database_entities.TFolderMaster;
import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.common_models.enums.UploadStatus;
import com.mycloud.common_models.utils.JwtUtil;
import com.mycloud.data_access_layer.repositories.TFileMasterRepository;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DownloadService {
    private final JwtUtil jwtUtil;
    private final FileService fileService;

    private final String BASE_TEMP_DIR;
    private final String FINAL_UPLOAD_DIR;

    public DownloadService(StorageConfig storageConfig, JwtConfig jwtConfig, FileService fileService) throws IOException {
        this.jwtUtil = new JwtUtil(jwtConfig.getSecret(), jwtConfig.getExpiration());
        this.fileService = fileService;

        Path BASE_TEMP_PATH = Paths.get(storageConfig.getRootDirectory(), storageConfig.getTempDirectory());
        Path BASE_FINAL_PATH = Paths.get(storageConfig.getRootDirectory(), storageConfig.getFinalDirectory());

        if (!Files.exists(BASE_TEMP_PATH)) {
            Files.createDirectories(BASE_TEMP_PATH);
        }

        if (!Files.exists(BASE_FINAL_PATH)) {
            Files.createDirectories(BASE_FINAL_PATH);
        }

        this.BASE_TEMP_DIR = String.valueOf(BASE_TEMP_PATH);
        this.FINAL_UPLOAD_DIR = String.valueOf(BASE_FINAL_PATH);
    }

    public ResponseEntity<ResourceRegion> DownloadFile(String fileId, HttpHeaders headers) {
        try {
            if (fileId == null || fileId.isEmpty() || headers == null || headers.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            JwtUser user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            TFileMaster CurrentFile = fileService.GetCurrentFileInfoFromFileIdAndUploadStatusAndDeleted(
                    user.userId(),
                    fileId,
                    UploadStatus.COMPLETED,
                    false
            );

            FileSystemResource resource = new FileSystemResource(this.FINAL_UPLOAD_DIR + "/" + CurrentFile.getFileId() + ".file");

            long length = resource.contentLength();

            if (headers.getRange().isEmpty()) {
                ResourceRegion region = new ResourceRegion(resource, 0, length);

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(CurrentFile.getContentType()))
                        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + CurrentFile.getOriginalName() + "." + CurrentFile.getFileExtension() + "\"")
                        .contentLength(length)
                        .body(region);
            }

            HttpRange range = headers.getRange().getFirst();

            long start = range.getRangeStart(length);
            long end = range.getRangeEnd(length);

            ResourceRegion region = new ResourceRegion(resource, start, end - start + 1);

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaType.parseMediaType(CurrentFile.getContentType()))
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + CurrentFile.getOriginalName() + "\"")
                    .header(HttpHeaders.CONTENT_RANGE,
                            "bytes " + start + "-" + end + "/" + length)
                    .contentLength(end - start + 1)
                    .body(region);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
