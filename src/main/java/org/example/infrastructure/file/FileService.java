package org.example.infrastructure.file;

import dev.langchain4j.agent.tool.Tool;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileService {

    @Tool(name = "readFile", value = "读取文件内容，输入文件路径，返回文件内容字符串")
    public String readFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new RuntimeException("文件不存在: " + filePath);
            }
            return Files.readString(path);
        } catch (IOException e) {
            log.error("读取文件失败: {}", e.getMessage());
            throw new RuntimeException("读取文件失败: " + e.getMessage());
        }
    }

    @Tool(name = "writeFile", value = "写入文件内容，输入文件路径和内容，返回是否成功")
    public boolean writeFile(String filePath, String content) {
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            Files.writeString(path, content);
            return true;
        } catch (IOException e) {
            log.error("写入文件失败: {}", e.getMessage());
            return false;
        }
    }

    @Tool(name = "deleteFile", value = "删除文件，输入文件路径，返回是否成功")
    public boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                return false;
            }
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("删除文件失败: {}", e.getMessage());
            return false;
        }
    }

    @Tool(name = "fileExists", value = "检查文件是否存在，输入文件路径，返回是否存在")
    public boolean fileExists(String filePath) {
        Path path = Paths.get(filePath);
        return Files.exists(path) && Files.isRegularFile(path);
    }

    @Tool(name = "getFileInfo", value = "获取文件信息，输入文件路径，返回文件信息（名称、大小、类型、修改时间）")
    public FileInfo getFileInfo(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new RuntimeException("文件不存在: " + filePath);
            }
            // 核心修复：明确指定读取 BasicFileAttributes 类型的属性
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            String fileName = path.getFileName().toString();
            String extension = fileName.contains(".")
                    ? fileName.substring(fileName.lastIndexOf(".") + 1)
                    : "";
            return new FileInfo(
                    fileName,
                    extension.toLowerCase(),
                    attrs.size(),
                    attrs.lastModifiedTime().toMillis(),
                    filePath
            );
        } catch (IOException e) {
            log.error("获取文件信息失败: {}", e.getMessage());
            throw new RuntimeException("获取文件信息失败: " + e.getMessage());
        }
    }

    @Tool(name = "listFiles", value = "列出目录下的所有文件，输入目录路径，返回文件信息列表")
    public List<FileInfo> listFiles(String directory) {
        try {
            Path dirPath = Paths.get(directory);
            if (!Files.exists(dirPath)) {
                return new ArrayList<>();
            }
            List<FileInfo> files = new ArrayList<>();
            Files.walk(dirPath)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            files.add(getFileInfo(path.toString()));
                        } catch (Exception e) {
                            log.warn("跳过文件: {}", path);
                        }
                    });
            return files;
        } catch (IOException e) {
            log.error("列出文件失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Tool(name = "copyFile", value = "复制文件，输入源文件路径和目标路径，返回是否成功")
    public boolean copyFile(String sourcePath, String targetPath) {
        try {
            Path source = Paths.get(sourcePath);
            Path target = Paths.get(targetPath);
            if (!Files.exists(source)) {
                return false;
            }
            Files.createDirectories(target.getParent());
            Files.copy(source, target);
            return true;
        } catch (IOException e) {
            log.error("复制文件失败: {}", e.getMessage());
            return false;
        }
    }

    @Tool(name = "moveFile", value = "移动文件，输入源文件路径和目标路径，返回是否成功")
    public boolean moveFile(String sourcePath, String targetPath) {
        try {
            Path source = Paths.get(sourcePath);
            Path target = Paths.get(targetPath);
            if (!Files.exists(source)) {
                return false;
            }
            Files.createDirectories(target.getParent());
            Files.move(source, target);
            return true;
        } catch (IOException e) {
            log.error("移动文件失败: {}", e.getMessage());
            return false;
        }
    }

    @Tool(name = "calculateHash", value = "计算文件哈希值，输入文件路径，返回MD5哈希值")
    public String calculateHash(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new RuntimeException("文件不存在: " + filePath);
            }
            return org.example.common.utils.Md5Utils.md5Hex(Files.readAllBytes(path));
        } catch (IOException e) {
            log.error("计算哈希失败: {}", e.getMessage());
            throw new RuntimeException("计算哈希失败: " + e.getMessage());
        }
    }

    @Tool(name = "calculateHashFromContent", value = "从内容计算哈希值，输入文件内容，返回MD5哈希值")
    public String calculateHashFromContent(String content) {
        return org.example.common.utils.Md5Utils.md5Hex(content.getBytes());
    }

    @Tool(name = "createDirectory", value = "创建目录，输入目录路径，返回是否成功")
    public boolean createDirectory(String directoryPath) {
        try {
            Path path = Paths.get(directoryPath);
            Files.createDirectories(path);
            return true;
        } catch (IOException e) {
            log.error("创建目录失败: {}", e.getMessage());
            return false;
        }
    }

    @Tool(name = "deleteDirectory", value = "删除目录，输入目录路径，返回是否成功")
    public boolean deleteDirectory(String directoryPath) {
        try {
            Path path = Paths.get(directoryPath);
            if (!Files.exists(path)) {
                return false;
            }
            Files.delete(path);
            return true;
        } catch (IOException e) {
            log.error("删除目录失败: {}", e.getMessage());
            return false;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileInfo {
        private String fileName;
        private String extension;
        private long size;
        private long lastModified;
        private String filePath;
    }
}
