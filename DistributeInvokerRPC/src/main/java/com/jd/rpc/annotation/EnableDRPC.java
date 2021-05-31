package com.jd.rpc.annotation;

import com.jd.rpc.configuration.DRPCAutoConfiguration;
import com.jd.rpc.configuration.DRPCConfiguration;
import com.jd.rpc.configuration.ProxyBeanImport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ConditionalOnProperty(prefix = "spring.drpc",name = "enable",havingValue = "true")
@Import({DRPCAutoConfiguration.class,ProxyBeanImport.class})
@EnableConfigurationProperties(value = {DRPCConfiguration.class})
@Documented
public @interface EnableDRPC {
    public DRPCScanner scanner();
}
