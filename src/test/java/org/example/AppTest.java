package org.example;

import org.example.common.agent.DocAiAgent;
import org.example.common.utils.ToolScannerUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AppTest {
    @Autowired
    private DocAiAgent docAiAgent;

    @Test
    public void aiTest() {
        String reply = docAiAgent.chat(1L, "小AI，我要上传文件，D:\\code\\java\\doc-ai\\src\\test\\resources\\新建 文本文档.txt,存储字段file就行，ID是121212,记得告诉我结果是否成功");
        System.out.println(reply);
    }

    @Autowired
    private ToolScannerUtils toolScannerUtils;

    @Test
    public void beanScanTest() {
        List<Object> objects = toolScannerUtils.scanAllToolBeans();
        System.out.println(objects);
    }
}
