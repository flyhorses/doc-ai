package org.example.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j接口文档配置（适配Spring Boot 3.x + Jakarta）
 * 访问地址：http://localhost:8080/doc.html
 */
@Configuration
public class Knife4jConfig {

    /**
     * 配置文档基本信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 文档标题
                .info(new Info()
                        .title("AI文档管理系统API")
                        // 文档描述
                        .description("基于AI Skill的企业级文档管理系统，支持文件上传/解析/搜索/下载")
                        // 版本
                        .version("1.0.0")
                        // 联系人信息（可选）
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@xxx.com")));
    }
}