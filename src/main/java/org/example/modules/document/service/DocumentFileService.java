package org.example.modules.document.service;

import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.constant.FileConstant;
import org.example.infrastructure.file.FileService;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentFileService {

    private final FileService fileService;

    @Tool(name = "storeDocumentFile", value = "存储文档文件到系统，输入文件内容、文件名、用户ID、文档ID，返回存储路径")
    public String storeDocumentFile(byte[] content, String fileName, Long userId, String docId) {
        try {
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String docIdPrefix = docId.length() >= 8 ? docId.substring(0, 8) : docId;
            String storagePath = Paths.get(
                    FileConstant.FILE_ROOT_PATH,
                    datePath,
                    docIdPrefix,
                    fileName
            ).toString();

            fileService.createDirectory(Paths.get(storagePath).getParent().toString());
            
            try (InputStream inputStream = new ByteArrayInputStream(content)) {
                fileService.writeFile(storagePath, new String(content));
            }

            log.info("文档文件存储成功: {}", storagePath);
            return storagePath;
        } catch (Exception e) {
            log.error("存储文档文件失败: {}", e.getMessage());
            throw new RuntimeException("存储文档文件失败: " + e.getMessage());
        }
    }

    @Tool(name = "getDocumentFile", value = "获取文档文件，输入文档ID，返回文件对象")
    public File getDocumentFile(String docId, String storagePath) {
        try {
            if (storagePath == null || storagePath.isEmpty()) {
                throw new RuntimeException("存储路径不能为空");
            }
            return new File(storagePath);
        } catch (Exception e) {
            log.error("获取文档文件失败: {}", e.getMessage());
            throw new RuntimeException("获取文档文件失败: " + e.getMessage());
        }
    }

    @Tool(name = "readDocumentContent", value = "读取文档内容，输入存储路径，返回文档内容字符串")
    public String readDocumentContent(String storagePath) {
        try {
            return fileService.readFile(storagePath);
        } catch (Exception e) {
            log.error("读取文档内容失败: {}", e.getMessage());
            throw new RuntimeException("读取文档内容失败: " + e.getMessage());
        }
    }

    @Tool(name = "deleteDocumentFile", value = "删除文档文件，输入存储路径，返回是否成功")
    public boolean deleteDocumentFile(String storagePath) {
        try {
            return fileService.deleteFile(storagePath);
        } catch (Exception e) {
            log.error("删除文档文件失败: {}", e.getMessage());
            return false;
        }
    }

    @Tool(name = "checkDocumentFileExists", value = "检查文档文件是否存在，输入存储路径，返回是否存在")
    public boolean checkDocumentFileExists(String storagePath) {
        try {
            return fileService.fileExists(storagePath);
        } catch (Exception e) {
            log.error("检查文档文件失败: {}", e.getMessage());
            return false;
        }
    }

    @Tool(name = "getDocumentStoragePath", value = "获取文档存储路径，输入文档ID和文件名，返回预期的存储路径")
    public String getDocumentStoragePath(String docId, String fileName) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String docIdPrefix = docId.length() >= 8 ? docId.substring(0, 8) : docId;
        return Paths.get(
                FileConstant.FILE_ROOT_PATH,
                datePath,
                docIdPrefix,
                fileName
        ).toString();
    }
}
