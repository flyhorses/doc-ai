package org.example.modules.document.service;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentAnalysisService {

    private final ChatModel chatModel;
//
//    @Tool(name = "generateDocumentSummary", value = "生成文档摘要，输入文档内容，返回摘要文本")
//    public String generateDocumentSummary(String content) {
//        try {
//            if (content == null || content.isEmpty()) {
//                throw new RuntimeException("文档内容不能为空");
//            }
//
//            String truncatedContent = truncateContent(content, 3000);
//            String prompt = "请为以下文档内容生成一个简洁的摘要（不超过200字）：\n\n" + truncatedContent + "\n\n请直接输出摘要，不要有其他内容。";
//
//            String summary = chatModel.generate(
//                    SystemMessage.from("你是一个专业的文档摘要助手"),
//                    UserMessage.from(prompt)
//            ).content().text();
//
//            log.info("生成文档摘要成功，长度: {}", summary.length());
//            return summary.trim();
//        } catch (Exception e) {
//            log.error("生成文档摘要失败: {}", e.getMessage());
//            throw new RuntimeException("生成文档摘要失败: " + e.getMessage());
//        }
//    }
//
//    @Tool(name = "extractDocumentKeyInfo", value = "从文档内容中提取关键信息，输入文档内容，返回关键信息")
//    public String extractDocumentKeyInfo(String content) {
//        try {
//            if (content == null || content.isEmpty()) {
//                throw new RuntimeException("文档内容不能为空");
//            }
//
//            String truncatedContent = truncateContent(content, 4000);
//            String prompt = """
//                请从以下文档内容中提取关键信息：
//
//                文档内容：
//                """ + truncatedContent + """
//
//                请提取并整理以下信息（如果存在）：
//                1. 文档类型
//                2. 涉及的主要人物/部门
//                3. 关键日期
//                4. 金额/数量（如有）
//                5. 主要事项
//
//                请以简洁的列表形式输出。
//                """;
//
//            String keyInfo = chatModel.generate(
//                    SystemMessage.from("你是一个专业的信息提取助手"),
//                    UserMessage.from(prompt)
//            ).content().text();
//
//            log.info("提取关键信息成功");
//            return keyInfo.trim();
//        } catch (Exception e) {
//            log.error("提取关键信息失败: {}", e.getMessage());
//            throw new RuntimeException("提取关键信息失败: " + e.getMessage());
//        }
//    }
//
//    @Tool(name = "suggestDocumentFileName", value = "根据文档内容建议合适的文件名，输入文档内容和原始文件名，返回建议的文件名")
//    public String suggestDocumentFileName(String content, String originalName) {
//        try {
//            if (content == null || content.isEmpty()) {
//                throw new RuntimeException("文档内容不能为空");
//            }
//
//            String truncatedContent = truncateContent(content, 2000);
//            String prompt = """
//                请根据以下文档内容，生成一个合适的文件名。
//                要求：
//                1. 文件名要简洁明了，能体现文档主题
//                2. 不要包含特殊字符
//                3. 不要包含文件扩展名
//                4. 长度控制在20字以内
//
//                文档内容：
//                """ + truncatedContent + "\n\n请直接输出文件名，不要有其他内容。";
//
//            String suggestedName = chatModel.generate(
//                    SystemMessage.from("你是一个专业的文件命名助手"),
//                    UserMessage.from(prompt)
//            ).content().text().trim();
//
//            String extension = "";
//            if (originalName != null && originalName.contains(".")) {
//                extension = originalName.substring(originalName.lastIndexOf("."));
//            }
//
//            String finalName = suggestedName + extension;
//            log.info("建议文件名: {}", finalName);
//            return finalName;
//        } catch (Exception e) {
//            log.error("建议文件名失败: {}", e.getMessage());
//            throw new RuntimeException("建议文件名失败: " + e.getMessage());
//        }
//    }
//
//    @Tool(name = "classifyDocument", value = "对文档进行分类，输入文档内容，返回分类和标签")
//    public Map<String, String> classifyDocument(String content) {
//        try {
//            if (content == null || content.isEmpty()) {
//                throw new RuntimeException("文档内容不能为空");
//            }
//
//            String truncatedContent = truncateContent(content, 3000);
//            String prompt = """
//                请分析以下文档内容，返回合适的分类信息。
//
//                文档内容：
//                """ + truncatedContent + """
//
//                请按以下格式输出：
//                - 主分类：[从 合同、报告、方案、通知、记录、其他 中选择一个]
//                - 子分类：[根据内容细分，如：采购合同、工作报告等]
//                - 标签：[用逗号分隔的标签，如：重要,紧急,财务]
//                """;
//
//            String classification = chatModel.generate(
//                    SystemMessage.from("你是一个专业的文档分类助手"),
//                    UserMessage.from(prompt)
//            ).content().text();
//
//            Map<String, String> result = parseClassification(classification);
//            log.info("文档分类完成: {}", result);
//            return result;
//        } catch (Exception e) {
//            log.error("文档分类失败: {}", e.getMessage());
//            throw new RuntimeException("文档分类失败: " + e.getMessage());
//        }
//    }
//
//    @Tool(name = "extractDocumentKeywords", value = "从文档内容中提取关键词，输入文档内容，返回关键词列表")
//    public String extractDocumentKeywords(String content) {
//        try {
//            if (content == null || content.isEmpty()) {
//                throw new RuntimeException("文档内容不能为空");
//            }
//
//            String truncatedContent = truncateContent(content, 3000);
//            String prompt = "请从以下文档内容中提取5-10个关键词，用逗号分隔输出：\n\n" + truncatedContent;
//
//            String keywords = chatModel.generate(
//                    SystemMessage.from("你是一个专业的关键词提取助手"),
//                    UserMessage.from(prompt)
//            ).content().text();
//
//            log.info("提取关键词成功: {}", keywords);
//            return keywords.trim();
//        } catch (Exception e) {
//            log.error("提取关键词失败: {}", e.getMessage());
//            throw new RuntimeException("提取关键词失败: " + e.getMessage());
//        }
//    }
//
//    @Tool(name = "analyzeDocument", value = "综合分析文档，输入文档内容和原始文件名，返回包含摘要、分类、建议文件名等所有分析结果")
//    public Map<String, Object> analyzeDocument(String content, String originalName) {
//        try {
//            if (content == null || content.isEmpty()) {
//                throw new RuntimeException("文档内容不能为空");
//            }
//
//            Map<String, Object> result = new HashMap<>();
//            result.put("originalName", originalName);
//
//            String summary = generateDocumentSummary(content);
//            result.put("summary", summary);
//
//            String suggestedName = suggestDocumentFileName(content, originalName);
//            result.put("suggestedName", suggestedName);
//
//            Map<String, String> classification = classifyDocument(content);
//            result.put("category", classification.get("主分类"));
//            result.put("subCategory", classification.get("子分类"));
//            result.put("tags", classification.get("标签"));
//
//            String keyInfo = extractDocumentKeyInfo(content);
//            result.put("keyInfo", keyInfo);
//
//            String keywords = extractDocumentKeywords(content);
//            result.put("keywords", keywords);
//
//            log.info("综合分析文档完成");
//            return result;
//        } catch (Exception e) {
//            log.error("综合分析文档失败: {}", e.getMessage());
//            throw new RuntimeException("综合分析文档失败: " + e.getMessage());
//        }
//    }
//
//    private String truncateContent(String content, int maxLength) {
//        if (content.length() <= maxLength) {
//            return content;
//        }
//        return content.substring(0, maxLength) + "...(内容已截断)";
//    }
//
//    private Map<String, String> parseClassification(String classification) {
//        Map<String, String> result = new HashMap<>();
//        String[] lines = classification.split("\n");
//
//        for (String line : lines) {
//            line = line.trim();
//            if (line.startsWith("- 主分类") || line.startsWith("主分类")) {
//                result.put("主分类", extractValue(line));
//            } else if (line.startsWith("- 子分类") || line.startsWith("子分类")) {
//                result.put("子分类", extractValue(line));
//            } else if (line.startsWith("- 标签") || line.startsWith("标签")) {
//                result.put("标签", extractValue(line));
//            }
//        }
//
//        return result;
//    }
//
//    private String extractValue(String line) {
//        int colonIndex = line.indexOf("：");
//        if (colonIndex == -1) {
//            colonIndex = line.indexOf(":");
//        }
//        if (colonIndex != -1) {
//            return line.substring(colonIndex + 1).trim()
//                    .replace("[", "")
//                    .replace("]", "");
//        }
//        return "";
//    }
}
