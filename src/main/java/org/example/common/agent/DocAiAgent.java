package org.example.common.agent;


import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

/**
 * 纯AI Agent接口（无任何注解，完全灵活）
 */
public interface DocAiAgent {
    // AI交互方法
    String chat(@MemoryId Long memoryId,@UserMessage String userMessage);
}