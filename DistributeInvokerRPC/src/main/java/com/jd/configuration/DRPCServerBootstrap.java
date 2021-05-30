package com.jd.configuration;

import com.jd.annotation.DRPCProvider;
import com.jd.registry.ZookeeperRegistry;
import com.jd.request.DRPCRequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DRPCServerBootstrap {
    private DRPCConfiguration drpcConfiguration;
    private ZookeeperRegistry zookeeperRegistry;
    private ApplicationContext ctx;

    public DRPCServerBootstrap(DRPCConfiguration drpcConfiguration, ZookeeperRegistry registry,ApplicationContext ctx) {
        this.drpcConfiguration = drpcConfiguration;
        this.ctx=ctx;
    }

    public void init(){

        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        final String RESOURCE_PATTERN = "/**/**/*.class";
        // 扫描的包名
        final String BASE_PACKAGE = drpcConfiguration.getProviderPackages();
        try {
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(BASE_PACKAGE) + RESOURCE_PATTERN;
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader reader = readerFactory.getMetadataReader(resource);
                    String className = reader.getClassMetadata().getClassName();
                    Class<?> clazz = Class.forName(className);
                    DRPCProvider annotation = clazz.getAnnotation(DRPCProvider.class);
                    if(annotation != null){
                        //这个类使用了自定义注解
                        zookeeperRegistry.registryService(clazz,drpcConfiguration.getServerPort());

                        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DRPCRequestTemplate.class)
                                .addConstructorArgValue(drpcConfiguration.getApplicationName())
                                .addConstructorArgValue(drpcConfiguration.getTimeout())
                                .addConstructorArgValue(clazz)
                                .setPrimary(false);

                        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();

                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error("读取class失败", e);
        }

    }
}
