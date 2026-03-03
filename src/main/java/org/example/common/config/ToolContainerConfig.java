package org.example.common.config;

import org.example.common.tool.ToolScanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 全局Tool容器配置
 * 核心：把动态扫描的Tool列表封装成固定名称的Spring Bean，供@AiService引用
 */
@Configuration
public class ToolContainerConfig {

    /**
     * 定义全局Tool容器Bean，名称固定为「allToolBeans」
     * @param toolScanner 自动注入的Tool扫描器
     * @return 所有包含@Tool方法的Bean列表
     */
    @Bean("allToolBeans") // 关键：给Bean起固定名称，后续注解里引用这个名称
    public List<Object> allToolBeans(ToolScanner toolScanner) {
        // 调用扫描器，获取所有Tool Bean
        return toolScanner.scanAllToolBeans();
    }
}
