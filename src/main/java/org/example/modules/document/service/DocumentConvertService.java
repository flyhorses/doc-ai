package org.example.modules.document.service;

import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.text.PDFTextStripper;
//import org.apache.poi.xwpf.usermodel.XWPFDocument;
//import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.example.infrastructure.file.FileService;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentConvertService {

    private final FileService fileService;

//    @Tool(name = "parseDocument", value = "解析文档内容，输入文件路径，返回文档文本内容")
//    public String parseDocument(String filePath) {
//        try {
//            File file = new File(filePath);
//            if (!file.exists()) {
//                throw new RuntimeException("文件不存在: " + filePath);
//            }
//
//            String fileName = file.getName().toLowerCase();
//            String content;
//
//            if (fileName.endsWith(".pdf")) {
//                content = parsePdf(filePath);
//            } else if (fileName.endsWith(".docx")) {
//                content = parseDocx(filePath);
//            } else if (fileName.endsWith(".doc")) {
//                throw new RuntimeException("暂不支持.doc格式，请转换为.docx格式");
//            } else if (fileName.endsWith(".txt")) {
//                content = fileService.readFile(filePath);
//            } else {
//                throw new RuntimeException("不支持的文件格式: " + fileName);
//            }
//
//            log.info("解析文档成功: {}", filePath);
//            return content;
//        } catch (Exception e) {
//            log.error("解析文档失败: {}", e.getMessage());
//            throw new RuntimeException("解析文档失败: " + e.getMessage());
//        }
//    }

//    @Tool(name = "parseDocumentFromBytes", value = "从字节数组解析文档内容，输入文件内容和文件名，返回文档文本内容")
//    public String parseDocumentFromBytes(byte[] content, String fileName) {
//        try {
//            String lowerFileName = fileName.toLowerCase();
//            String text;
//
//            if (lowerFileName.endsWith(".pdf")) {
//                text = parsePdfFromBytes(content);
//            } else if (lowerFileName.endsWith(".docx")) {
//                text = parseDocxFromBytes(content);
//            } else if (lowerFileName.endsWith(".txt")) {
//                text = new String(content);
//            } else {
//                throw new RuntimeException("不支持的文件格式: " + fileName);
//            }
//
//            log.info("从字节数组解析文档成功: {}", fileName);
//            return text;
//        } catch (Exception e) {
//            log.error("从字节数组解析文档失败: {}", e.getMessage());
//            throw new RuntimeException("从字节数组解析文档失败: " + e.getMessage());
//        }
//    }

    @Tool(name = "getDocumentType", value = "获取文档类型，输入文件路径，返回文档类型（PDF/DOCX/TXT等）")
    public String getDocumentType(String filePath) {
        try {
            String fileName = Paths.get(filePath).getFileName().toString().toLowerCase();
            
            if (fileName.endsWith(".pdf")) {
                return "PDF";
            } else if (fileName.endsWith(".docx")) {
                return "DOCX";
            } else if (fileName.endsWith(".doc")) {
                return "DOC";
            } else if (fileName.endsWith(".txt")) {
                return "TXT";
            } else {
                return "UNKNOWN";
            }
        } catch (Exception e) {
            log.error("获取文档类型失败: {}", e.getMessage());
            return "UNKNOWN";
        }
    }

    @Tool(name = "splitDocumentContent", value = "按段落分割文档内容，输入文档内容，返回段落列表")
    public List<String> splitDocumentContent(String content) {
        try {
            if (content == null || content.isEmpty()) {
                throw new RuntimeException("文档内容不能为空");
            }

            String[] paragraphs = content.split("\n\n+");
            List<String> paragraphList = new java.util.ArrayList<>();
            
            for (String paragraph : paragraphs) {
                String trimmed = paragraph.trim();
                if (!trimmed.isEmpty()) {
                    paragraphList.add(trimmed);
                }
            }

            log.info("分割文档内容完成，段落数: {}", paragraphList.size());
            return paragraphList;
        } catch (Exception e) {
            log.error("分割文档内容失败: {}", e.getMessage());
            throw new RuntimeException("分割文档内容失败: " + e.getMessage());
        }
    }

//    private String parsePdf(String filePath) throws Exception {
//        try (PDDocument document = PDDocument.load(new File(filePath))) {
//            PDFTextStripper stripper = new PDFTextStripper();
//            String text = stripper.getText(document);
//            return text.trim();
//        }
//    }
//
//    private String parsePdfFromBytes(byte[] content) throws Exception {
//        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(content))) {
//            PDFTextStripper stripper = new PDFTextStripper();
//            String text = stripper.getText(document);
//            return text.trim();
//        }
//    }
//
//    private String parseDocx(String filePath) throws Exception {
//        try (FileInputStream fis = new FileInputStream(filePath);
//             XWPFDocument document = new XWPFDocument(fis)) {
//            StringBuilder text = new StringBuilder();
//            List<XWPFParagraph> paragraphs = document.getParagraphs();
//
//            for (XWPFParagraph paragraph : paragraphs) {
//                String paragraphText = paragraph.getText();
//                if (paragraphText != null && !paragraphText.trim().isEmpty()) {
//                    text.append(paragraphText).append("\n");
//                }
//            }
//
//            return text.toString().trim();
//        }
//    }
//
//    private String parseDocxFromBytes(byte[] content) throws Exception {
//        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(content))) {
//            StringBuilder text = new StringBuilder();
//            List<XWPFParagraph> paragraphs = document.getParagraphs();
//
//            for (XWPFParagraph paragraph : paragraphs) {
//                String paragraphText = paragraph.getText();
//                if (paragraphText != null && !paragraphText.trim().isEmpty()) {
//                    text.append(paragraphText).append("\n");
//                }
//            }
//
//            return text.toString().trim();
//        }
//    }
}
