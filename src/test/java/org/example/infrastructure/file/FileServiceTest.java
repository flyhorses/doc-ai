package org.example.infrastructure.file;

import lombok.extern.slf4j.Slf4j;
import org.example.infrastructure.file.FileService.FileInfo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileServiceTest {

    @Autowired
    private FileService fileService;

    private static final String BASE_TEST_DIR = "D:\\code\\java\\doc-ai\\file-test";

    @BeforeAll
    static void setupAll() {
        File baseDir = new File(BASE_TEST_DIR);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        log.info("测试目录: {}", BASE_TEST_DIR);
    }

    @AfterAll
    static void cleanupAll() {
        deleteDirectory(new File(BASE_TEST_DIR));
        log.info("测试目录已清理");
    }

    private static void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }

    @Test
    @Order(1)
    void testCreateDirectory() {
        log.info("测试: createDirectory");
        String dirPath = BASE_TEST_DIR + "\\subdir";
        boolean result = fileService.createDirectory(dirPath);
        assertTrue(result);
        log.info("结果: {}", result);
    }

    @Test
    @Order(2)
    void testWriteFile() {
        log.info("测试: writeFile");
        String filePath = BASE_TEST_DIR + "\\test.txt";
        boolean result = fileService.writeFile(filePath, "Hello World");
        assertTrue(result);
        log.info("结果: {}", result);
    }

    @Test
    @Order(3)
    void testReadFile() {
        log.info("测试: readFile");
        String filePath = BASE_TEST_DIR + "\\test.txt";
        String content = fileService.readFile(filePath);
        assertEquals("Hello World", content);
        log.info("结果: {}", content);
    }

    @Test
    @Order(4)
    void testFileExists() {
        log.info("测试: fileExists");
        String filePath = BASE_TEST_DIR + "\\test.txt";
        boolean exists = fileService.fileExists(filePath);
        assertTrue(exists);
        log.info("结果: {}", exists);
    }

    @Test
    @Order(5)
    void testGetFileInfo() {
        log.info("测试: getFileInfo");
        String filePath = BASE_TEST_DIR + "\\test.txt";
        FileInfo info = fileService.getFileInfo(filePath);
        assertNotNull(info);
        System.out.println(info);
        assertEquals("test.txt", info.getFileName());
        assertEquals("txt", info.getExtension());
        log.info("结果: fileName={}, extension={}, size={}", info.getFileName(), info.getExtension(), info.getSize());
    }

    @Test
    @Order(6)
    void testListFiles() {
        log.info("测试: listFiles");
        List<FileInfo> files = fileService.listFiles(BASE_TEST_DIR);
        System.out.println(files);
        assertTrue(files.size() >= 1);
        log.info("结果: 文件数量={}", files.size());
    }

    @Test
    @Order(7)
    void testCopyFile() {
        log.info("测试: copyFile");
        String source = BASE_TEST_DIR + "\\test.txt";
        String target = BASE_TEST_DIR + "\\test-copy.txt";
        boolean result = fileService.copyFile(source, target);
        assertTrue(result);
        log.info("结果: {}", result);
    }

    @Test
    @Order(8)
    void testMoveFile() {
        log.info("测试: moveFile");
        String source = BASE_TEST_DIR + "\\test-copy.txt";
        String target = BASE_TEST_DIR + "\\subdir\\test-moved.txt";
        boolean result = fileService.moveFile(source, target);
        assertTrue(result);
        log.info("结果: {}", result);
    }

    @Test
    @Order(9)
    void testCalculateHash() {
        log.info("测试: calculateHash");
        String filePath = BASE_TEST_DIR + "\\test.txt";
        String hash = fileService.calculateHash(filePath);
        assertNotNull(hash);
        assertEquals(32, hash.length());
        log.info("结果: {}", hash);
    }

    @Test
    @Order(10)
    void testCalculateHashFromContent() {
        log.info("测试: calculateHashFromContent");
        String hash = fileService.calculateHashFromContent("Hello World");
        assertNotNull(hash);
        assertEquals(32, hash.length());
        log.info("结果: {}", hash);
    }

    @Test
    @Order(11)
    void testDeleteFile() {
        log.info("测试: deleteFile");
        String filePath = BASE_TEST_DIR + "\\subdir\\test-moved.txt";
        boolean result = fileService.deleteFile(filePath);
        assertTrue(result);
        log.info("结果: {}", result);
    }

    @Test
    @Order(12)
    void testDeleteDirectory() {
        log.info("测试: deleteDirectory");
        String dirPath = BASE_TEST_DIR + "\\subdir";
        boolean result = fileService.deleteDirectory(dirPath);
        assertTrue(result);
        log.info("结果: {}", result);
    }
}
