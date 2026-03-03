package org.example.common.config;

import dev.langchain4j.community.model.zhipu.ZhipuAiChatModel;
import dev.langchain4j.community.model.zhipu.ZhipuAiEmbeddingModel;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    @Value("${langchain4j.zhipu-ai.api-key}")
    private String apiKey;
    @Value("${langchain4j.zhipu-ai.chat-model.model-name}")
    private String chatModelName;
    @Value("${langchain4j.zhipu-ai.embedding-model.model-name}")
    private String embeddingModelName;

    @Bean
    public ChatModel chatLanguageModel() {
        return ZhipuAiChatModel.builder()
                .apiKey(this.apiKey)
                .model(chatModelName)
                .build();
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return ZhipuAiEmbeddingModel.builder()
                .apiKey(this.apiKey)
                .model(embeddingModelName)
                .build();
    }

    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        return userId -> MessageWindowChatMemory.builder()
                .id(userId)
                .chatMemoryStore(new InMemoryChatMemoryStore())
                .maxMessages(30)
                .build();
    }
    //todo : 后期存储向量会使用这个bean
//    @Bean
//    public EmbeddingStore<TextSegment> embeddingStore(){
//        return ChromaEmbeddingStore.builder()
//                .baseUrl("http://localhost:8080")
//                .collectionName("doc_ai")
//                .build();
//    }
}
