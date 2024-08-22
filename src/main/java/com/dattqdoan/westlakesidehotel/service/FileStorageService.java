package com.dattqdoan.westlakesidehotel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class FileStorageService {
    private static final long MAX_TOTAL_IMAGE_SIZE_BYTES = 20 * 1024 * 1024; // 20 MB
    private static final Set<String> ALLOWED_IMAGE_TYPES = new HashSet<>(Set.of("jpg", "jpeg", "png", "gif")); // Các loại ảnh cho phép

    public String uploadFile(MultipartFile file) {
        String result = null;

        if (file != null && !file.isEmpty()) {
            try {
                validateImageType(file);

                if (file.getSize() > MAX_TOTAL_IMAGE_SIZE_BYTES) {
                    throw new Exception("Kích thước file vượt quá giới hạn cho phép (" + (MAX_TOTAL_IMAGE_SIZE_BYTES / 1024 / 1024) + " MB)");
                }

                // Chuyển đổi thành Base64
                result = convertFileToBase64(file);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return result;
    }

    private void validateImageType(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("Không xác định loại tệp.");
        }

        // Xác định định dạng tệp từ contentType
        String[] contentTypeParts = contentType.split("/");
        if (contentTypeParts.length != 2) {
            throw new IllegalArgumentException("Loại tệp không hợp lệ.");
        }

        String fileExtension = contentTypeParts[1].toLowerCase();
        if (!ALLOWED_IMAGE_TYPES.contains(fileExtension)) {
            throw new IllegalArgumentException("Loại ảnh không được phép: " + fileExtension);
        }
    }

    public String convertFileToBase64(MultipartFile file) throws IOException {
        // Đọc tệp vào BufferedImage
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new IOException("Không thể đọc ảnh từ tệp.");
        }

        // Chuyển đổi ảnh thành Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(originalImage, getImageFormat(file), baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    private String getImageFormat(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IOException("Không xác định loại tệp.");
        }

        // Xác định định dạng ảnh từ contentType
        String[] contentTypeParts = contentType.split("/");
        if (contentTypeParts.length != 2) {
            throw new IOException("Loại tệp không hợp lệ.");
        }

        String fileExtension = contentTypeParts[1].toLowerCase();
        switch (fileExtension) {
            case "jpeg":
            case "jpg":
                return "jpg";
            case "png":
                return "png";
            case "gif":
                return "gif";
            default:
                throw new IOException("Định dạng ảnh không hỗ trợ: " + fileExtension);
        }
    }
}
