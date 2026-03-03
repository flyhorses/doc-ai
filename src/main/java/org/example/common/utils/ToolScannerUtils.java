package org.example.common.utils;

import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Tool扫描器
 * 扫描所有包含@Tool注解方法的Spring Bean
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ToolScannerUtils {

    private final ApplicationContext applicationContext;
    private List<Object> cachedToolBeans;

    private static final String BASE_SCAN_PACKAGE = "org.example";
    private static final String[] EXCLUDE_CLASS_KEYWORDS = {"Assistant", "ChatController", "Agent"};

    public List<Object> scanAllToolBeans() {
        if (cachedToolBeans != null) {
            return cachedToolBeans;
        }

        List<Object> toolBeans = new ArrayList<>();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Component.class));

        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(BASE_SCAN_PACKAGE);

        for (BeanDefinition bd : beanDefinitions) {
            String className = bd.getBeanClassName();
            if (className == null) continue;

            if (isExcludedClass(className)) {
                log.debug("排除类：{}", className);
                continue;
            }

            try {
                Class<?> clazz = Class.forName(className);
                if (hasToolAnnotationMethod(clazz)) {
                    Object bean = applicationContext.getBean(clazz);
                    toolBeans.add(bean);
                    log.info("找到含@Tool的类：{}", clazz.getSimpleName());
                }
            } catch (ClassNotFoundException e) {
                log.warn("忽略加载失败的类：{}", className);
            } catch (Exception e) {
                log.warn("忽略未被Spring管理的类：{}", className);
            }
        }

        cachedToolBeans = toolBeans.stream().distinct().collect(Collectors.toList());
        log.info("最终扫描到含@Tool的类实例数量：{}", cachedToolBeans.size());
        return cachedToolBeans;
    }

    public List<String> getToolBeanNames() {
        return scanAllToolBeans().stream()
                .map(bean -> bean.getClass().getSimpleName())
                .collect(Collectors.toList());
    }

    public List<String> getToolMethodMetadata() {
        List<String> metadata = new ArrayList<>();
        scanAllToolBeans().forEach(bean -> {
            Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
            for (Method method : methods) {
                if (method.isAnnotationPresent(Tool.class)) {
                    Tool tool = method.getAnnotation(Tool.class);
                    metadata.add(String.format(
                            "Bean：%s | 方法：%s | 描述：%s",
                            bean.getClass().getSimpleName(),
                            method.getName(),
                            tool.value()
                    ));
                }
            }
        });
        return metadata;
    }

    private boolean isExcludedClass(String className) {
        for (String keyword : EXCLUDE_CLASS_KEYWORDS) {
            if (className.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasToolAnnotationMethod(Class<?> clazz) {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
        for (Method method : methods) {
            if (method.isAnnotationPresent(Tool.class)) {
                return true;
            }
        }
        return false;
    }

    @jakarta.annotation.PostConstruct
    public void preScanTools() {
        scanAllToolBeans();
    }
}
