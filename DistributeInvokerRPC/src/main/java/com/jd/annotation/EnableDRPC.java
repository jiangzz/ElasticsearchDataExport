package com.jd.annotation;

import com.jd.configuration.DRPCAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(DRPCAutoConfiguration.class)
@Documented
public @interface EnableDRPC {
}
