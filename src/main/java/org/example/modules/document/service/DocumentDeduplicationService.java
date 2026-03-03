package org.example.modules.document.service;

import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.infrastructure.file.FileService;
import org.example.infrastructure.redis.RedisService;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentDeduplicationService {

    private final FileService fileService;
    private final RedisService redisService;
    private static final String DOC_HASH_INDEX_KEY = "doc_ai:document:hash_index";

    @Tool(name = "calculateDocumentHash", value = "计算文档哈希值，输入文件路径，返回MD5哈希值")
    public String calculateDocumentHash(String filePath) {
        try {
            String hash = fileService.calculateHash(filePath);
            log.info("计算文档哈希成功: {} -> {}", filePath, hash);
            return hash;
        } catch (Exception e) {
            log.error("计算文档哈希失败: {}", e.getMessage());
            throw new RuntimeException("计算文档哈希失败: " + e.getMessage());
        }
    }

    @Tool(name = "calculateDocumentHashFromContent", value = "从文档内容计算哈希值，输入文档内容，返回MD5哈希值")
    public String calculateDocumentHashFromContent(String content) {
        try {
            String hash = fileService.calculateHashFromContent(content);
            log.info("从内容计算哈希成功: {}", hash);
            return hash;
        } catch (Exception e) {
            log.error("从内容计算哈希失败: {}", e.getMessage());
            throw new RuntimeException("从内容计算哈希失败: " + e.getMessage());
        }
    }

    @Tool(name = "checkDocumentDuplicate", value = "检查文档是否重复，输入文档哈希值，返回是否重复及已存在文档信息")
    public Map<String, Object> checkDocumentDuplicate(String hash) {
        try {
            boolean exists = redisService.hExists(DOC_HASH_INDEX_KEY, hash);
            
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("hash", hash);
            result.put("isDuplicate", exists);
            
            if (exists) {
                @SuppressWarnings("unchecked")
                Map<String, Object> existingDoc = redisService.hGet(DOC_HASH_INDEX_KEY, hash);
                result.put("existingDoc", existingDoc);
                log.info("文档已存在，哈希: {}", hash);
            } else {
                log.info("文档不存在，哈希: {}", hash);
            }
            
            return result;
        } catch (Exception e) {
            log.error("检查文档重复失败: {}", e.getMessage());
            throw new RuntimeException("检查文档重复失败: " + e.getMessage());
        }
    }

    @Tool(name = "registerDocumentHash", value = "注册文档哈希值，输入哈希值和文档信息，将哈希与文档关联存储")
    public boolean registerDocumentHash(String hash, String docId, String fileName, Long userId) {
        try {
            Map<String, Object> docInfo = new java.util.HashMap<>();
            docInfo.put("docId", docId);
            docInfo.put("fileName", fileName);
            docInfo.put("userId", userId);
            docInfo.put("registerTime", System.currentTimeMillis());
            
            redisService.hSet(DOC_HASH_INDEX_KEY, hash, docInfo);
            log.info("注册文档哈希成功: {} -> {}", hash, docId);
            return true;
        } catch (Exception e) {
            log.error("注册文档哈希失败: {}", e.getMessage());
            return false;
        }
    }

    @Tool(name = "getDocumentByHash", value = "根据哈希值获取已存在的文档信息，输入哈希值，返回文档信息")
    public Map<String, Object> getDocumentByHash(String hash) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> docInfo = redisService.hGet(DOC_HASH_INDEX_KEY, hash);
            if (docInfo == null || docInfo.isEmpty()) {
                throw new RuntimeException("哈希对应的文档不存在: " + hash);
            }
            return docInfo;
        } catch (Exception e) {
            log.error("获取哈希对应文档失败: {}", e.getMessage());
            throw new RuntimeException("获取哈希对应文档失败: " + e.getMessage());
        }
    }

    @Tool(name = "unregisterDocumentHash", value = "取消注册文档哈希值，输入哈希值，删除哈希与文档的关联")
    public boolean unregisterDocumentHash(String hash) {
        try {
            redisService.hDelete(DOC_HASH_INDEX_KEY, hash);
            log.info("取消注册文档哈希成功: {}", hash);
            return true;
        } catch (Exception e) {
            log.error("取消注册文档哈希失败: {}", e.getMessage());
            return false;
        }
    }
}
