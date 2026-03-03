package org.example.common.config;

import dev.langchain4j.community.model.zhipu.ZhipuAiChatModel;
import dev.langchain4j.community.model.zhipu.ZhipuAiEmbeddingModel;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import org.example.common.agent.DocAiAgent;
import org.example.common.utils.ToolScannerUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.List;

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

    @Bean("docAiAgent")
    @DependsOn({"toolScannerUtils", "chatLanguageModel"})
    public DocAiAgent docAiAgent(
            ChatModel chatLanguageModel,
            ToolScannerUtils toolScannerUtils
    ) {
        List<Object> toolBeans = toolScannerUtils.scanAllToolBeans();
        List<String> toolBeanNames = toolScannerUtils.getToolBeanNames();
        
        String systemMessage = String.format("""
                你是专业的文档AI助手，可使用的工具类：%s
                调用工具时必须传入正确参数，缺少参数时友好提示用户补充。
                直接返回工具的执行结果，不要解释工具调用过程。
                """, toolBeanNames);

        return AiServices.builder(DocAiAgent.class)
                .chatModel(chatLanguageModel)
                .tools(toolBeans.toArray())
                .systemMessageProvider(userId -> systemMessage)
                .chatMemoryProvider(chatMemoryProvider())
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
}
