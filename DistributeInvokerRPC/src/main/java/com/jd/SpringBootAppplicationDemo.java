package com.jd;

import com.jd.rpc.annotation.DRPCScanner;
import com.jd.rpc.annotation.EnableDRPC;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.io.IOException;

@SpringBootApplication
@EnableDRPC(scanner = @DRPCScanner(basePackage = "com.jd.service"))
public class SpringBootAppplicationDemo {
    @SneakyThrows
    public static void main(String[] args) throws IOException {
        SpringApplication.run(SpringBootAppplicationDemo.class,args);
    }
}
