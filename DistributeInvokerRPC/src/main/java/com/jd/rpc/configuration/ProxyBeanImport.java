package com.jd.rpc.configuration;

import com.jd.rpc.annotation.DRPCConsumer;
import com.jd.rpc.annotation.EnableDRPC;
import com.jd.rpc.proxy.JDKInterfaceProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;

@Slf4j
public class ProxyBeanImport implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes mapperScanAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableDRPC.class.getName()));
        AnnotationAttributes scannerAttributes= (AnnotationAttributes) mapperScanAttrs.get("scanner");
        String basePackage = (String) scannerAttributes.get("basePackage");
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        String RESOURCE_PATTERN = "/**/*.class";
        String BASE_PACKAGE =basePackage;

        try {
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(BASE_PACKAGE) + RESOURCE_PATTERN;
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader reader = readerFactory.getMetadataReader(resource);
                    String className = reader.getClassMetadata().getClassName();
                    Class<?> clazz = Class.forName(className);
                    DRPCConsumer annotation = clazz.getAnnotation(DRPCConsumer.class);
                    if(annotation != null){

                         BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(JDKInterfaceProxy.class)
                                .addConstructorArgValue(annotation.timeout())
                                .addConstructorArgValue(annotation.timeUnit())
                                .addConstructorArgValue(clazz)
                                 .addConstructorArgReference("zookeeperRegistry")
                                 .addConstructorArgReference("nettyClient")
                                 .addPropertyReference("poolExecutor","drpcThreadPoolExecutor")
                                 .setPrimary(false);

                        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
                        registry.registerBeanDefinition(annotation.name(),beanDefinition);
                        log.info("注冊接口 {} 代理bean {} 成功!",clazz,annotation.name());
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error("加载类信息失败", e);
        }
    }
}
