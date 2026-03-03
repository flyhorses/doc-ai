package org.example.modules.document.service;

import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.infrastructure.redis.RedisService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentMetadataService {

    private final RedisService redisService;
    private static final String DOC_METADATA_KEY = "doc_ai:document:metadata";
    private static final String DOC_USER_INDEX_KEY = "doc_ai:document:user_index";

    @Tool(name = "saveDocumentMetadata", value = "保存文档元数据，输入文档ID和元数据信息，返回是否成功")
    public boolean saveDocumentMetadata(String docId, String fileName, String storagePath, 
                                         Long userId, Long fileSize, String fileType,
                                         String summary, String category, String tags) {
        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("docId", docId);
            metadata.put("fileName", fileName);
            metadata.put("storagePath", storagePath);
            metadata.put("userId", userId);
            metadata.put("fileSize", fileSize);
            metadata.put("fileType", fileType);
            metadata.put("summary", summary);
            metadata.put("category", category);
            metadata.put("tags", tags);
            metadata.put("createTime", System.currentTimeMillis());
            metadata.put("updateTime", System.currentTimeMillis());

            redisService.hSet(DOC_METADATA_KEY, docId, metadata);
            redisService.hSet(DOC_USER_INDEX_KEY, userId + ":" + docId, docId);

            log.info("保存文档元数据成功: {}", docId);
            return true;
        } catch (Exception e) {
            log.error("保存文档元数据失败: {}", e.getMessage());
            return false;
        }
    }

    @Tool(name = "getDocumentMetadata", value = "获取文档元数据，输入文档ID，返回元数据信息")
    public Map<String, Object> getDocumentMetadata(String docId) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> metadata = redisService.hGet(DOC_METADATA_KEY, docId);
            if (metadata == null || metadata.isEmpty()) {
                throw new RuntimeException("文档元数据不存在: " + docId);
            }
            return metadata;
        } catch (Exception e) {
            log.error("获取文档元数据失败: {}", e.getMessage());
            throw new RuntimeException("获取文档元数据失败: " + e.getMessage());
        }
    }

    @Tool(name = "updateDocumentMetadata", value = "更新文档元数据字段，输入文档ID、字段名、新值，返回是否成功")
    public boolean updateDocumentMetadata(String docId, String field, Object value) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> metadata = redisService.hGet(DOC_METADATA_KEY, docId);
            if (metadata == null) {
                throw new RuntimeException("文档元数据不存在: " + docId);
            }
            metadata.put(field, value);
            metadata.put("updateTime", System.currentTimeMillis());
            redisService.hSet(DOC_METADATA_KEY, docId, metadata);
            log.info("更新文档元数据成功: {} - {}", docId, field);
            return true;
        } catch (Exception e) {
            log.error("更新文档元数据失败: {}", e.getMessage());
            return false;
        }
    }

    @Tool(name = "deleteDocumentMetadata", value = "删除文档元数据，输入文档ID，返回是否成功")
    public boolean deleteDocumentMetadata(String docId) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> metadata = redisService.hGet(DOC_METADATA_KEY, docId);
            if (metadata != null && metadata.containsKey("userId")) {
                Long userId = ((Number) metadata.get("userId")).longValue();
                redisService.hDelete(DOC_USER_INDEX_KEY, userId + ":" + docId);
            }
            redisService.hDelete(DOC_METADATA_KEY, docId);
            log.info("删除文档元数据成功: {}", docId);
            return true;
        } catch (Exception e) {
            log.error("删除文档元数据失败: {}", e.getMessage());
            return false;
        }
    }

    @Tool(name = "searchDocuments", value = "搜索文档，输入关键词和用户ID，返回匹配的文档列表")
    public List<Map<String, Object>> searchDocuments(String keyword, Long userId) {
        try {
            List<Map<String, Object>> results = new ArrayList<>();
            Iterable<String> keys = redisService.findKeysByPattern(DOC_USER_INDEX_KEY + ":" + userId + ":*");

            String lowerKeyword = keyword.toLowerCase();

            for (String key : keys) {
                String docId = key.split(":")[2];
                @SuppressWarnings("unchecked")
                Map<String, Object> doc = redisService.hGet(DOC_METADATA_KEY, docId);
                
                if (doc != null) {
                    String fileName = (String) doc.getOrDefault("fileName", "");
                    String summary = (String) doc.getOrDefault("summary", "");
                    String category = (String) doc.getOrDefault("category", "");
                    String tags = (String) doc.getOrDefault("tags", "");

                    if (fileName.toLowerCase().contains(lowerKeyword) ||
                        summary.toLowerCase().contains(lowerKeyword) ||
                        category.toLowerCase().contains(lowerKeyword) ||
                        tags.toLowerCase().contains(lowerKeyword)) {
                        results.add(doc);
                    }
                }
            }

            log.info("搜索文档完成，关键词: {}, 结果数: {}", keyword, results.size());
            return results;
        } catch (Exception e) {
            log.error("搜索文档失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Tool(name = "listUserDocuments", value = "列出用户所有文档，输入用户ID，返回文档列表")
    public List<Map<String, Object>> listUserDocuments(Long userId) {
        try {
            List<Map<String, Object>> documents = new ArrayList<>();
            Iterable<String> keys = redisService.findKeysByPattern(DOC_USER_INDEX_KEY + ":" + userId + ":*");

            for (String key : keys) {
                String docId = key.split(":")[2];
                @SuppressWarnings("unchecked")
                Map<String, Object> doc = redisService.hGet(DOC_METADATA_KEY, docId);
                if (doc != null) {
                    documents.add(doc);
                }
            }

            log.info("列出用户文档完成，用户ID: {}, 文档数: {}", userId, documents.size());
            return documents;
        } catch (Exception e) {
            log.error("列出用户文档失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Tool(name = "checkDocMetadataExists", value = "检查文档元数据是否存在，输入文档ID，返回是否存在")
    public boolean checkDocMetadataExists(String docId) {
        try {
            return redisService.hExists(DOC_METADATA_KEY, docId);
        } catch (Exception e) {
            log.error("检查文档元数据失败: {}", e.getMessage());
            return false;
        }
    }
}
