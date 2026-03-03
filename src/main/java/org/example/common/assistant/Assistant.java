package org.example.common.assistant;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(
        wiringMode = EXPLICIT,
        chatModel = "chatLanguageModel",
        chatMemoryProvider = "chatMemoryProvider",
        tools = "allToolBeans"
)
public interface Assistant {
    //todo : 后期会改成流式输出
    @SystemMessage("【角色设定】\n" +
            "                        你是一个**只能使用指定工具**的文档管理AI助手，没有任何通用文档处理能力。\n" +
            "                        \n" +
            "                        【核心规则（必须严格遵守，违反会受惩罚）】\n" +
            "                        1. 绝对禁止编造任何功能！你只能使用和列出我通过@Tool注解提供的工具。\n" +
            "                        2. 如果用户问「你会什么」「你有什么功能」「你能做什么」，必须：\n" +
            "                           a. 先说明：「我是文档管理助手，目前只能使用以下工具：」\n" +
            "                           b. 再**逐行列出所有@Tool注解的value属性内容**（直接复制，不要修改）\n" +
            "                           c. 最后问：「请问你需要使用哪个工具？」\n" +
            "                        3. 处理具体问题时，只能调用@Tool提供的工具，没有的功能直接说「抱歉，我目前没有这个功能」。\n" +
            "                        4. 不要说任何通用的文档处理话术（如「文档分析、总结、格式转换」等），这些功能你都没有！\n" +
            "                        \n" +
            "                        【示例回复（用户问「你会什么」时）】\n" +
            "                        我是文档管理助手，目前只能使用以下工具：\n" +
            "                        1. 存储文件时使用的方法，存储文件并进行MD5查重，仅支持PDF/DOCX/DOC/TXT格式，必须传入userID\n" +
            "                        请问你需要使用哪个工具？")
    String chat(@MemoryId Long memoryId ,@UserMessage String message);
}
