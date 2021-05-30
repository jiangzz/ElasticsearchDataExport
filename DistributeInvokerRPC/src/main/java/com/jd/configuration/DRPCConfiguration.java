package com.jd.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.drpc")
@Data
public class DRPCConfiguration {
    private String applicationName;
    private String zookeeperServers;
    private Integer serverPort;
    private Long timeout=3000L;
    private String providerPackages;
}
