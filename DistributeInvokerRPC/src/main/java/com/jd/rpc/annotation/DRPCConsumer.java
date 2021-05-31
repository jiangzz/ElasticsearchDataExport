package com.jd.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author jiangzhongzhou
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DRPCConsumer {
    String name();
    Class targetInterface();
    long timeout();
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
    String loadBalance() default "random";
    String cluster() default "failfast";
    int retries() default 3;

}
