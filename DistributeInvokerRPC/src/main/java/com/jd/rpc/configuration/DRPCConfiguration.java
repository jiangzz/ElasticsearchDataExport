package com.jd.rpc.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.drpc")
@Data
public class DRPCConfiguration {
    //服务基本信息
    private String applicationName;
    private String zookeeperServers;
    private Integer serverPort;
    private Long timeout=3000L;

    //执行线程池
    private int coreSize=8;
    private int queueSize=1000;
    private int maxPoolSize=16;
}
