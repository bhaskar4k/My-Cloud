package com.mycloud.file_service.service;

import com.mycloud.common_config.model.JwtConfig;
import com.mycloud.common_models.common_entities.InitiateUploadRequestEntity;
import com.mycloud.common_models.common_entities.JwtUser;
import com.mycloud.common_models.dto.ApiResponseDto;
import com.mycloud.common_models.utils.JwtUtil;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;

@Service
public class UploadService {
    private final JwtUtil jwtUtil;
    private final String BASE_TEMP_DIR;
    private final String FINAL_UPLOAD_DIR;

    public UploadService(JwtConfig jwtConfig) {
        this.jwtUtil = new JwtUtil(jwtConfig.getSecret(), jwtConfig.getExpiration());
        this.BASE_TEMP_DIR = "E:/Project/MyCloudStorageTemp/";
        this.FINAL_UPLOAD_DIR = "E:/Project/MyCloudStorage/";
    }

    public ApiResponseDto<String> DoInitiateFileUpload(InitiateUploadRequestEntity request) {
        try {
            JwtUser user = jwtUtil.GetCurrentUser(); // Kept your authentication hook
            if (!user.IsAuthenticated()) {
                return ApiResponseDto.Error(
                        500,
                        "Access denied. Please login again."
                );
            }

            // Generate a secure unique ID for this upload session
            String uploadId = UUID.randomUUID().toString();

            // Create a temporary folder specifically for this upload chunks
            Path chunkDirPath = Paths.get(BASE_TEMP_DIR, uploadId);
            Files.createDirectories(chunkDirPath);

            return ApiResponseDto.Success("File upload initiated successfully", uploadId);
        } catch (Exception ex) {
            ex.printStackTrace();

            return ApiResponseDto.Error(500, ex.getMessage());
        }

    }

    public void DoSaveChunk(InputStream inputStream, String uploadId, int chunkIndex, int totalChunks) throws IOException {
        Path chunkFile = Paths.get(BASE_TEMP_DIR, uploadId, "chunk_" + chunkIndex);

        // Save the current chunk data to disk
        Files.copy(inputStream, chunkFile, StandardCopyOption.REPLACE_EXISTING);

        // If this is the last chunk, kick off the assembly process
        if (chunkIndex == totalChunks - 1) {
            mergeChunks(uploadId, totalChunks);
        }
    }

    private void mergeChunks(String UploadId, int TotalChunks) throws IOException {
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
    }
}