package org.example.common.tool;

import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
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
 * 【修正版】Tool扫描器（添加过滤器，解决扫描不到的问题）
 * 核心修正：给ClassPathScanningCandidateComponentProvider添加@Component注解过滤器
 */
@Component
@RequiredArgsConstructor
public class ToolScanner {

    private final ApplicationContext applicationContext;
    private List<Object> cachedToolBeans;

    // ========== 核心配置（根据你的项目调整） ==========
    private static final String BASE_SCAN_PACKAGE = "org.example.modules";
    private static final String[] EXCLUDE_CLASS_KEYWORDS = {"Assistant", "ChatController"};
    private static final boolean DEBUG_MODE = true;

    // ========== 核心方法 ==========
    public List<Object> scanAllToolBeans() {
        if (cachedToolBeans != null) {
            return cachedToolBeans;
        }

        List<Class<?>> toolClasses = new ArrayList<>();
        // -------------------------- 【核心修正1】添加@Component注解过滤器 --------------------------
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        // 关键：添加过滤器，只扫描带@Component注解的类（@Service/@Controller等都包含@Component）
        scanner.addIncludeFilter(new AnnotationTypeFilter(Component.class));
        // ----------------------------------------------------------------------------------------

        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(BASE_SCAN_PACKAGE);

        for (BeanDefinition bd : beanDefinitions) {
            String className = bd.getBeanClassName();
            if (className == null) continue;

            if (isExcludedClass(className)) {
                if (DEBUG_MODE) System.out.println("❌ 排除类（循环依赖）：" + className);
                continue;
            }

            try {
                Class<?> clazz = Class.forName(className);
                if (hasToolAnnotationMethod(clazz)) {
                    toolClasses.add(clazz);
                    if (DEBUG_MODE) System.out.println("✅ 找到含@Tool的类：" + clazz.getSimpleName());
                }
            } catch (ClassNotFoundException e) {
                if (DEBUG_MODE) System.out.println("⚠️  忽略加载失败的类：" + className);
                continue;
            }
        }

        List<Object> toolBeans = new ArrayList<>();
        for (Class<?> toolClass : toolClasses) {
            try {
                Object bean = applicationContext.getBean(toolClass);
                toolBeans.add(bean);
            } catch (Exception e) {
                if (DEBUG_MODE) System.out.println("⚠️  忽略未被Spring管理的类：" + toolClass.getSimpleName());
                continue;
            }
        }

        cachedToolBeans = toolBeans.stream().distinct().collect(Collectors.toList());
        System.out.println("\n🎉 最终扫描到含@Tool的类实例数量：" + cachedToolBeans.size());
        return cachedToolBeans;
    }

    // ========== 辅助方法（不变） ==========
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
}