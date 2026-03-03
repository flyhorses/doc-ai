package org.example.common.utils;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MultipartFileUtils {
    public static MultipartFile convert(String filePath, String fieldName) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在：" + filePath);
        }
        String fileName = file.getName();
        String contentType = getContentType(fileName);

        return new MockMultipartFile(
                fieldName,
                fileName,
                contentType,
                new FileInputStream(file)
        );
    }

    private static String getContentType(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) {
            return null;
        }
        String suffix = fileName.substring(dotIndex + 1).toLowerCase();
        return switch (suffix) {
            case "pdf" -> "application/pdf";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "doc" -> "application/msword";
            case "txt" -> "text/plain";
            default -> null;
        };
    }
}
