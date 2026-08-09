package com.mycloud.file_service.service;

import com.mycloud.common_config.model.JwtConfig;
import com.mycloud.common_config.model.StorageConfig;
import com.mycloud.common_models.common_entities.InitiateUploadRequestEntity;
import com.mycloud.common_models.common_entities.JwtUser;
import com.mycloud.common_models.database_entities.TFileMaster;
import com.mycloud.common_models.database_entities.TFolderMaster;
import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.common_models.enums.UploadStatus;
import com.mycloud.common_models.utils.JwtUtil;
import com.mycloud.data_access_layer.repositories.TFileMasterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;

@Service
public class UploadService {
    private final JwtUtil jwtUtil;
    private final TFileMasterRepository fileMasterRepository;
    private final FolderService folderService;
    private final Long CHUNK_SIZE;

    private final String BASE_TEMP_DIR;
    private final String FINAL_UPLOAD_DIR;

    public UploadService(StorageConfig storageConfig, JwtConfig jwtConfig, TFileMasterRepository fileMasterRepository, FolderService folderService) throws IOException {
        this.jwtUtil = new JwtUtil(jwtConfig.getSecret(), jwtConfig.getExpiration());
        this.fileMasterRepository = fileMasterRepository;
        this.folderService = folderService;
        this.CHUNK_SIZE = 10L * 1024 * 1024;

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


    @Transactional
    private void UpdateStatusOfFileUploadProcess(String fileId, Long userId, UploadStatus status) {
        TFileMaster fileMaster = fileMasterRepository.findByFileIdAndUserIdAndDeleted(fileId, userId, false)
                .orElseThrow(() -> new RuntimeException("File not found: " + fileId));

        fileMaster.setStatus(status);

        fileMasterRepository.save(fileMaster);
    }


    public ApiResponseDto<String> DoInitiateFileUpload(InitiateUploadRequestEntity request) {
        TFileMaster fileMetadata = null;
        Path chunkDirPath = null;

        try {
            if (request == null || request.getFileName() == null || request.getFileName().isEmpty() ||
                    request.getFileSize() == null || request.getFileSize() == 0 ||
                    request.getContentType() == null || request.getContentType().isEmpty() ||
                    request.getFolderId() == null || request.getFolderId().isEmpty()){
                return ApiResponseDto.Error(HttpStatus.BAD_REQUEST.value(), "Invalid Payload.");
            }

            JwtUser user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(HttpStatus.UNAUTHORIZED.value(), "Access denied. Please login again.");
            }

            // Generate a secure unique ID for this upload session
            String uploadId = UUID.randomUUID().toString();

            String fileName = request.getFileName();
            String originalFileName = fileName;
            String fileExtension = "";

            int lastIndexOfDot = fileName.lastIndexOf('.');
            if (lastIndexOfDot > 0) {
                fileExtension = fileName.substring(lastIndexOfDot + 1);
                originalFileName = fileName.substring(0, lastIndexOfDot);
            }

            // 3. Compute total chunks based on your 10MB math boundary
            int totalChunks = (int) Math.ceil((double) request.getFileSize() / CHUNK_SIZE);

            TFolderMaster ParentFolderInfo = folderService.GetCurrentFolderInfoFromFolderId(user.userId(), request.getFolderId());

            fileMetadata = TFileMaster.builder()
                    .parentFolderId(ParentFolderInfo.getId())
                    .fileId(uploadId)
                    .originalName(originalFileName)
                    .fileExtension(fileExtension)
                    .contentType(request.getContentType() != null ? request.getContentType() : "application/octet-stream")
                    .fileSize(request.getFileSize())
                    .totalChunks(totalChunks)
                    .userId(user.userId())
                    .status(UploadStatus.INITIATED)
                    .deleted(false)
                    .build();

            // Save metadata entry to the database
            fileMasterRepository.save(fileMetadata);

            // Create a temporary folder specifically for this upload chunks
            chunkDirPath = Paths.get(BASE_TEMP_DIR, uploadId);
            Files.createDirectories(chunkDirPath);

            return ApiResponseDto.Success("File upload initiated successfully", uploadId);
        } catch (Exception ex) {
            ex.printStackTrace();

            // Roll back directory creation if it was created but something else failed
            if (chunkDirPath != null && Files.exists(chunkDirPath)) {
                try {
                    Files.delete(chunkDirPath);
                } catch (IOException ioEx) {
                    System.err.println("Failed to clean up temp directory on initialization failure: " + ioEx.getMessage());
                }
            }

            // Remove the database record so you don't leave an orphan 'INITIATED' row
            if (fileMetadata != null && fileMetadata.getId() != null) {
                try {
                    fileMasterRepository.delete(fileMetadata);
                } catch (Exception dbEx) {
                    System.err.println("Failed to clean up database record on initialization failure: " + dbEx.getMessage());
                }
            }

            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to initiate the file upload process. Please try again.");
        }
    }


    public ApiResponseDto<Boolean> DoSaveChunk(InputStream inputStream, String uploadId, int chunkIndex, int totalChunks) throws IOException {
        JwtUser user = new JwtUser(false, null, null);

        try {
            user = jwtUtil.GetCurrentUser();
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(HttpStatus.UNAUTHORIZED.value(), "Access denied. Please login again.");
            }

            if (chunkIndex == 0) {
                UpdateStatusOfFileUploadProcess(uploadId, user.userId(), UploadStatus.UPLOADING);
            }

            Path chunkFile = Paths.get(BASE_TEMP_DIR, uploadId, "chunk_" + chunkIndex);

            Files.copy(inputStream, chunkFile, StandardCopyOption.REPLACE_EXISTING);

            Boolean MergingDone = false;
            if (chunkIndex == totalChunks - 1) {
                MergingDone = MergeChunks(uploadId, totalChunks, user);
            }

            if (MergingDone) {
                return ApiResponseDto.Success("Chunk - " + chunkIndex + " has been uploaded successfully", true);
            }

            return ApiResponseDto.Error(HttpStatus.UNPROCESSABLE_CONTENT.value(), "All chunks have been uploaded successfully<br>but server failed to process and merge them.");
        } catch (Exception ex) {
            if (user.IsAuthenticated()){
                UpdateStatusOfFileUploadProcess(uploadId, user.userId(), UploadStatus.FAILED);
            }

            ex.printStackTrace();
            return ApiResponseDto.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to upload chunk - " + chunkIndex + ".");
        }
    }


    private Boolean MergeChunks(String UploadId, int TotalChunks, JwtUser user) throws IOException {
        try {
            UpdateStatusOfFileUploadProcess(UploadId, user.userId(), UploadStatus.PROCESSING);

            Path tempDirPath = Paths.get(BASE_TEMP_DIR, UploadId);
            Path finalDirPath = Paths.get(FINAL_UPLOAD_DIR);

            // Ensure destination directory exists
            Files.createDirectories(finalDirPath);

            // Save the combined binary data purely as the uploadId with a .file extension
            Path finalFilePath = finalDirPath.resolve(UploadId + ".file");

            // Append every chunk in perfect order into the final file
            try (OutputStream destStream = new BufferedOutputStream(
                    Files.newOutputStream(finalFilePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE))) {

                for (int i = 0; i < TotalChunks; i++) {
                    Path chunkFile = tempDirPath.resolve("chunk_" + i);

                    if (!Files.exists(chunkFile)) {
                        throw new FileNotFoundException("Missing chunk index: " + i + " for upload session: " + TotalChunks);
                    }

                    Files.copy(chunkFile, destStream);
                }
            }

            // Clean up: delete temporary chunks and folder after a successful merge
            try (var walk = Files.walk(tempDirPath)) {
                walk.sorted((a, b) -> b.compareTo(a)) // Delete files before folders
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException ignored) {}
                        });
            }

            UpdateStatusOfFileUploadProcess(UploadId, user.userId(), UploadStatus.COMPLETED);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            UpdateStatusOfFileUploadProcess(UploadId, user.userId(), UploadStatus.FAILED);
            return false;
        }
    }
}