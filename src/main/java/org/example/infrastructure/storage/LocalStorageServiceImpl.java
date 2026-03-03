package org.example.infrastructure.storage;

import org.example.common.constant.FileConstant;
import org.example.common.exception.ServiceException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class LocalStorageServiceImpl implements StorageService {

    @Override
    public String store(InputStream inputStream, String fileName, Long userId, String md5) {
        try {
            String safeFileName = sanitizeFileName(fileName);
            
            String mainName = safeFileName.substring(0, safeFileName.lastIndexOf("."));
            String extension = safeFileName.substring(safeFileName.lastIndexOf(".")).toLowerCase();
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String md5Prefix = md5.substring(0, 8);
            String finalFileName = String.format("%s_%s%s", mainName, md5Prefix, extension);

            Path savePath = Paths.get(FileConstant.FILE_ROOT_PATH, datePath, md5Prefix, finalFileName);
            
            validatePath(savePath);

            Files.createDirectories(savePath.getParent());
            Files.copy(inputStream, savePath);
            
            return savePath.toString();
        } catch (IOException e) {
            throw new ServiceException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public File getFile(String storagePath) {
        Path path = Paths.get(storagePath);
        validatePath(path);
        
        File file = path.toFile();
        if (!file.exists() || !file.isFile()) {
            throw new ServiceException("文件不存在");
        }
        return file;
    }

    @Override
    public byte[] readFile(String storagePath) {
        try {
            Path path = Paths.get(storagePath);
            validatePath(path);
            
            if (!Files.exists(path) || !Files.isRegularFile(path)) {
                throw new ServiceException("文件不存在");
            }
            
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new ServiceException("文件读取失败: " + e.getMessage());
        }
    }

    @Override
    public String getFileName(String storagePath) {
        Path path = Paths.get(storagePath);
        validatePath(path);
        
        return path.getFileName().toString();
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new ServiceException("文件名不能为空");
        }
        
        String safeName = fileName.replaceAll("\\.\\.", "")
                .replaceAll("[/\\\\]", "_")
                .replaceAll("[<>:\"|?*]", "_");
        
        if (!safeName.contains(".")) {
            throw new ServiceException("文件名格式不正确，缺少扩展名");
        }
        
        return safeName;
    }

    private void validatePath(Path path) {
        Path normalizedPath = path.normalize();
        Path rootPath = Paths.get(FileConstant.FILE_ROOT_PATH).normalize();
        
        if (!normalizedPath.startsWith(rootPath)) {
            throw new ServiceException("非法的文件路径");
        }
    }
}
