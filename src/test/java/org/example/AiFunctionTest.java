package org.example;

import org.example.common.agent.DocAiAgent;
import org.example.common.utils.ToolScannerUtils;
import org.example.infrastructure.redis.RedisService;
import org.example.modules.document.service.DocumentSkillService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class AiFunctionTest {

    @Autowired
    private DocAiAgent docAiAgent;

    @Autowired
    private ToolScannerUtils toolScannerUtils;

    @Autowired
    private DocumentSkillService documentSkillService;

    @Autowired
    private RedisService redisService;

    private static final String TEST_FILE_PATH = "D:\\code\\java\\doc-ai\\src\\test\\resources\\新建 文本文档.txt";
    private static final Long TEST_USER_ID = 121212L;

    @Test
    public void testToolScan() {
        System.out.println("========== 测试工具扫描 ==========");
        List<Object> tools = toolScannerUtils.scanAllToolBeans();
        System.out.println("扫描到的工具数量: " + tools.size());
        
        List<String> metadata = toolScannerUtils.getToolMethodMetadata();
        System.out.println("\n工具方法详情:");
        metadata.forEach(System.out::println);
    }

    @Test
    public void testStoreFile() {
        System.out.println("========== 测试文件存储 ==========");
        try {
            Map<String, Object> result = documentSkillService.storeAndCheckDuplicateFile(
                    TEST_FILE_PATH, "file", TEST_USER_ID);
            System.out.println("文件存储成功!");
            System.out.println("文件MD5: " + result.get("fileMd5"));
            System.out.println("存储路径: " + result.get("storagePath"));
            System.out.println("文件名: " + result.get("fileName"));
            System.out.println("文件大小: " + result.get("fileSize") + " bytes");
        } catch (Exception e) {
            System.out.println("文件存储失败: " + e.getMessage());
        }
    }

    @Test
    public void testReadFile() {
        System.out.println("========== 测试文件读取 ==========");
        try {
            Map<String, Object> storeResult = documentSkillService.storeAndCheckDuplicateFile(
                    TEST_FILE_PATH, "file", TEST_USER_ID);
            String fileMd5 = (String) storeResult.get("fileMd5");
            
            Map<String, Object> readResult = documentSkillService.readFileByMd5(fileMd5);
            System.out.println("文件读取成功!");
            System.out.println("文件名: " + readResult.get("fileName"));
            System.out.println("文件大小: " + readResult.get("fileSize") + " bytes");
            System.out.println("编码方式: " + readResult.get("encoding"));
            
            String content = (String) readResult.get("content");
            System.out.println("内容长度: " + content.length() + " 字符");
        } catch (Exception e) {
            System.out.println("文件读取失败: " + e.getMessage());
        }
    }

    @Test
    public void testDownloadFile() {
        System.out.println("========== 测试文件下载 ==========");
        try {
            Map<String, Object> storeResult = documentSkillService.storeAndCheckDuplicateFile(
                    TEST_FILE_PATH, "file", TEST_USER_ID);
            String fileMd5 = (String) storeResult.get("fileMd5");
            
            Map<String, Object> downloadResult = documentSkillService.downloadFileByMd5(fileMd5);
            System.out.println("文件下载信息获取成功!");
            System.out.println("文件名: " + downloadResult.get("fileName"));
            System.out.println("存储路径: " + downloadResult.get("storagePath"));
            System.out.println("文件MD5: " + downloadResult.get("fileMd5"));
        } catch (Exception e) {
            System.out.println("文件下载失败: " + e.getMessage());
        }
    }

    @Test
    public void testCheckFileExists() {
        System.out.println("========== 测试文件存在检查 ==========");
        try {
//            Map<String, Object> storeResult = documentSkillService.storeAndCheckDuplicateFile(
//                    TEST_FILE_PATH, "file", TEST_USER_ID);
//            String fileMd5 = (String) redisService.hGet()et("fileMd5");
            
            Map<String, Object> existResult = documentSkillService.checkFileExists("1b11574d6666ecc0b0d413ec9bf2519e");
            System.out.println("文件存在检查完成!");
            System.out.println("文件是否存在: " + existResult.get("exists"));
            System.out.println("文件MD5: " + existResult.get("fileMd5"));
            if ((Boolean) existResult.get("exists")) {
                System.out.println("文件名: " + existResult.get("fileName"));
            }
            
            System.out.println("\n检查不存在的文件:");
            Map<String, Object> notExistResult = documentSkillService.checkFileExists("not_exist_md5");
            System.out.println("文件是否存在: " + notExistResult.get("exists"));
        } catch (Exception e) {
            System.out.println("文件检查失败: " + e.getMessage());
        }
    }

    @Test
    public void testAiChatWithFileUpload() {
        System.out.println("========== 测试AI对话-文件上传 ==========");
        String reply = docAiAgent.chat(TEST_USER_ID, 
                "请帮我上传文件：" + TEST_FILE_PATH + "，存储字段用file，用户ID是" + TEST_USER_ID);
        System.out.println("AI回复: " + reply);
    }

    @Test
    public void testAiChatWithFileRead() {
        System.out.println("========== 测试AI对话-文件读取 ==========");
        String reply = docAiAgent.chat(TEST_USER_ID, 
                "请帮我读取刚才上传的文件内容,1b11574d6666ecc0b0d413ec9bf2519e");
        System.out.println("AI回复: " + reply);
    }

    @Test
    public void testFullWorkflow() {
        System.out.println("========== 测试完整工作流程 ==========");
        
        System.out.println("\n1. 存储文件...");
        Map<String, Object> storeResult = documentSkillService.storeAndCheckDuplicateFile(
                TEST_FILE_PATH, "file", TEST_USER_ID);
        String fileMd5 = (String) storeResult.get("fileMd5");
        System.out.println("存储成功，MD5: " + fileMd5);

        System.out.println("\n2. 检查文件是否存在...");
        Map<String, Object> existResult = documentSkillService.checkFileExists(fileMd5);
        System.out.println("文件存在: " + existResult.get("exists"));

        System.out.println("\n3. 读取文件内容...");
        Map<String, Object> readResult = documentSkillService.readFileByMd5(fileMd5);
        System.out.println("读取成功，文件大小: " + readResult.get("fileSize") + " bytes");

        System.out.println("\n4. 获取下载信息...");
        Map<String, Object> downloadResult = documentSkillService.downloadFileByMd5(fileMd5);
        System.out.println("下载路径: " + downloadResult.get("storagePath"));

        System.out.println("\n========== 工作流程测试完成 ==========");
    }
}
