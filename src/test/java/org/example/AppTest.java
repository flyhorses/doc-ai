package org.example;



import org.example.common.assistant.Assistant;
import org.example.common.tool.ToolScanner;
import org.example.modules.document.service.DocumentSkillService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@SpringBootTest
public class AppTest
{
    @Autowired
    private Assistant assistant;
    @Test
    public void aiTest(){
        String reply = assistant.chat(1L, "小AI我要上传文件，\"D:\\cacheBox\\新建 文本文档.txt\"，ID是1235112");
        System.out.println(reply);
    }

    @Autowired
    private ToolScanner toolScanner;

    @Test
    public void beanScanTest(){
        List<Object> objects = toolScanner.scanAllToolBeans();
        System.out.println(objects);
    }

    public static MultipartFile convert(String filePath, String fieldName) throws IOException {
        File file = new File(filePath);
        // 校验文件是否存在
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在：" + filePath);
        }
        // 提取文件名和后缀
        String fileName = file.getName();
        // 构造文件类型（可选，如application/pdf，也可以传null）
        String contentType = getContentType(fileName);

        // 核心：用MockMultipartFile构造MultipartFile
        return new MockMultipartFile(
                fieldName,       // 表单字段名
                fileName,        // 文件名
                contentType,     // 文件类型
                new FileInputStream(file) // 文件输入流
        );
    }

    private static String getContentType(String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return switch (suffix) {
            case "pdf" -> "application/pdf";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "doc" -> "application/msword";
            case "txt" -> "text/plain";
            default -> null;
        };
    }

    @Autowired
    private DocumentSkillService documentSkillService;
    @Test
    public void uploadTest() throws IOException {
        documentSkillService.storeAndCheckDuplicateFile(AppTest.convert("D:\\code\\java\\doc-ai\\src\\test\\resources\\新建 文本文档.txt","file"),1222L);
    }

}
