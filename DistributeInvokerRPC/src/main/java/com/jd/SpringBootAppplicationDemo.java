package com.jd;

import com.jd.annotation.EnableDRPC;
import com.jd.service.IUserService;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


import java.io.IOException;

@SpringBootApplication
@EnableDRPC
public class SpringBootAppplicationDemo implements ApplicationContextAware {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(SpringBootAppplicationDemo.class,args);

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        IUserService userServiceProxy = applicationContext.getBean("userServiceProxy",IUserService.class);
//        userServiceProxy.queryUserById(1);
    }
}
