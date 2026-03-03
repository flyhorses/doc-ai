package org.example.modules.document.service;

import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.example.common.constant.FileConstant;
import org.example.common.exception.ServiceException;
import org.example.common.utils.Md5Utils;
import org.example.common.utils.MultipartFileUtils;
import org.example.infrastructure.redis.RedisService;
import org.example.infrastructure.storage.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentSkillService {
    private final StorageService storageService;
    private final RedisService redisService;

    @Tool("存储文件时使用的方法，存储文件并进行MD5查重，仅支持PDF/DOCX/DOC/TXT格式，必须传入userID")
    public Map<String, Object> storeAndCheckDuplicateFile(String filePath, String fieldName, Long userId) {
        try {
            if (userId == null || userId <= 0) {
                throw new ServiceException("userId不能为空");
            }

            MultipartFile file = MultipartFileUtils.convert(filePath, fieldName);
            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.contains(".")) {
                throw new ServiceException("文件格式不正确");
            }
            
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            List<String> suffixList = List.of(FileConstant.ALLOW_FILE_SUFFIX);
            if (!suffixList.contains(suffix)) {
                throw new ServiceException("文件格式不支持");
            }
            
            if (file.isEmpty()) {
                throw new ServiceException("文件不能为空");
            }

            String md5 = Md5Utils.md5Hex(file.getBytes());

            Boolean exist = redisService.hExists(FileConstant.FILE_MD5_HASH_KEY, md5);
            if (exist) {
                throw new ServiceException("文件已存在，无需重复上传");
            }
            
            String storagePath = storageService.store(file.getInputStream(), fileName, userId, md5);
            redisService.hSet(FileConstant.FILE_MD5_HASH_KEY, md5, storagePath);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("fileMd5", md5);
            result.put("storagePath", storagePath);
            result.put("fileName", fileName);
            result.put("fileSuffix", suffix);
            result.put("fileSize", file.getSize());
            result.put("userId", userId);
            return result;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("文件上传失败: " + e.getMessage());
        }
    }

    @Tool("读取文件内容的方法，通过文件的MD5值读取已存储的文件内容，返回Base64编码的文件内容")
    public Map<String, Object> readFileByMd5(String fileMd5) {
        try {
            if (fileMd5 == null || fileMd5.isEmpty()) {
                throw new ServiceException("文件MD5不能为空");
            }

            String storagePath = redisService.hGet(FileConstant.FILE_MD5_HASH_KEY, fileMd5);
            if (storagePath == null || storagePath.isEmpty()) {
                throw new ServiceException("文件不存在，请先上传");
            }

            byte[] fileContent = storageService.readFile(storagePath);
            String fileName = storageService.getFileName(storagePath);
            String base64Content = Base64.getEncoder().encodeToString(fileContent);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("fileName", fileName);
            result.put("fileSize", fileContent.length);
            result.put("content", base64Content);
            result.put("encoding", "base64");
            return result;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("文件读取失败: " + e.getMessage());
        }
    }

    @Tool("下载文件的方法，通过文件的MD5值获取文件的下载路径和文件名")
    public Map<String, Object> downloadFileByMd5(String fileMd5) {
        try {
            if (fileMd5 == null || fileMd5.isEmpty()) {
                throw new ServiceException("文件MD5不能为空");
            }

            String storagePath = redisService.hGet(FileConstant.FILE_MD5_HASH_KEY, fileMd5);
            if (storagePath == null || storagePath.isEmpty()) {
                throw new ServiceException("文件不存在，请先上传");
            }

            String fileName = storageService.getFileName(storagePath);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("fileName", fileName);
            result.put("storagePath", storagePath);
            result.put("fileMd5", fileMd5);
            return result;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("文件下载失败: " + e.getMessage());
        }
    }

    @Tool("检查文件是否存在的方法，通过文件的MD5值检查文件是否已存储")
    public Map<String, Object> checkFileExists(String fileMd5) {
        try {
            if (fileMd5 == null || fileMd5.isEmpty()) {
                throw new ServiceException("文件MD5不能为空");
            }

            Boolean exists = redisService.hExists(FileConstant.FILE_MD5_HASH_KEY, fileMd5);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("exists", exists);
            result.put("fileMd5", fileMd5);
            
            if (exists) {
                String storagePath = redisService.hGet(FileConstant.FILE_MD5_HASH_KEY, fileMd5);
                String fileName = storageService.getFileName(storagePath);
                result.put("fileName", fileName);
                result.put("storagePath", storagePath);
            }
            return result;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("检查文件失败: " + e.getMessage());
        }
    }
}
