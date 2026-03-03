package org.example.modules.document.service;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentStorageService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    @Tool(name = "embedDocument", value = "将文档内容向量化存储，输入文档ID和文档内容，返回是否成功")
    public boolean embedDocument(String docId, String content) {
        try {
            if (content == null || content.isEmpty()) {
                throw new RuntimeException("文档内容不能为空");
            }

            TextSegment segment = TextSegment.from(content);
            Embedding embedding = embeddingModel.embed(segment).content();
            
            embeddingStore.add(embedding, segment);

            log.info("文档向量化存储成功: {}", docId);
            return true;
        } catch (Exception e) {
            log.error("文档向量化存储失败: {}", e.getMessage());
            throw new RuntimeException("文档向量化存储失败: " + e.getMessage());
        }
    }
//
//    @Tool(name = "searchSimilarDocuments", value = "搜索相似文档，输入查询文本和返回数量，返回相似文档列表")
//    public List<Map<String, Object>> searchSimilarDocuments(String query, int topK) {
//        try {
//            if (query == null || query.isEmpty()) {
//                throw new RuntimeException("查询文本不能为空");
//            }
//
//            TextSegment querySegment = TextSegment.from(query);
//            Embedding queryEmbedding = embeddingModel.embed(querySegment).content();
//
//            List<EmbeddingMatch<TextSegment>> matches = embeddingStore.findRelevant(queryEmbedding, topK);
//
//            List<Map<String, Object>> results = matches.stream()
//                    .map(match -> {
//                        Map<String, Object> result = new java.util.HashMap<>();
//                        result.put("score", match.score());
//                        result.put("content", match.embedded().text());
//                        result.put("embeddingId", match.embeddingId());
//                        return result;
//                    })
//                    .collect();
//
//            log.info("搜索相似文档完成，查询: {}, 结果数: {}", query, results.size());
//            return results;
//        } catch (Exception e) {
//            log.error("搜索相似文档失败: {}", e.getMessage());
//            throw new RuntimeException("搜索相似文档失败: " + e.getMessage());
//        }
//    }

    @Tool(name = "deleteDocumentEmbedding", value = "删除文档向量索引，输入嵌入ID，返回是否成功")
    public boolean deleteDocumentEmbedding(String embeddingId) {
        try {
            embeddingStore.remove(embeddingId);
            log.info("删除文档向量索引成功: {}", embeddingId);
            return true;
        } catch (Exception e) {
            log.error("删除文档向量索引失败: {}", e.getMessage());
            return false;
        }
    }

    @Tool(name = "embedDocumentWithMetadata", value = "将文档内容向量化存储并附带元数据，输入文档ID、内容、元数据，返回是否成功")
    public boolean embedDocumentWithMetadata(String docId, String content, Map<String, String> metadata) {
        try {
            if (content == null || content.isEmpty()) {
                throw new RuntimeException("文档内容不能为空");
            }

            StringBuilder contentWithMeta = new StringBuilder();
            contentWithMeta.append("文档ID: ").append(docId).append("\n");
            
            if (metadata != null) {
                metadata.forEach((k, v) -> contentWithMeta.append(k).append(": ").append(v).append("\n"));
            }
            
            contentWithMeta.append("\n内容:\n").append(content);

            TextSegment segment = TextSegment.from(contentWithMeta.toString());
            Embedding embedding = embeddingModel.embed(segment).content();
            
            embeddingStore.add(embedding, segment);

            log.info("文档向量化存储成功（带元数据）: {}", docId);
            return true;
        } catch (Exception e) {
            log.error("文档向量化存储失败: {}", e.getMessage());
            throw new RuntimeException("文档向量化存储失败: " + e.getMessage());
        }
    }
}
