package com.jd.configuration;

import com.jd.annotation.DRPCProvider;
import com.jd.model.MethodInvokeContext;
import com.jd.model.ResultContext;
import com.jd.process.AbstractMessageProcessor;
import com.jd.process.DefaultMessageProcessor;
import com.jd.registry.ZookeeperRegistry;
import com.jd.request.DRPCRequestTemplate;
import com.jd.server.NetestNettyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;


@Configuration
@ConditionalOnProperty(prefix = "spring.drpc",name = "enable",havingValue = "true")
@EnableConfigurationProperties(value = {DRPCConfiguration.class})
@Slf4j
public class DRPCAutoConfiguration  {

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public ZookeeperRegistry zookeeperRegistry(DRPCConfiguration drpcConfiguration){
        log.info("创建注册中心ZookeeperRegistry");
        return  new ZookeeperRegistry(drpcConfiguration.getZookeeperServers(), drpcConfiguration.getApplicationName());
    }

    @Bean(destroyMethod = "close",initMethod = "start")
    @ConditionalOnMissingBean
    public NetestNettyServer netestNettyServer(DRPCConfiguration drpcConfiguration,AbstractMessageProcessor<MethodInvokeContext, ResultContext> messageProcessor){
        return new NetestNettyServer(drpcConfiguration.getServerPort(),messageProcessor);
    }

    @Bean
    @ConditionalOnMissingBean
    public AbstractMessageProcessor<MethodInvokeContext,ResultContext> messageProcessor(ApplicationContext ctx){
        return new DefaultMessageProcessor(ctx);
    }
    @Bean(initMethod = "init")
    @DependsOn(value = {"zookeeperRegistry"})
    public DRPCServerBootstrap drpcServerBootstrap(@Autowired DRPCConfiguration drpcConfiguration,@Autowired ZookeeperRegistry registry,@Autowired  ApplicationContext ctx){
        return new DRPCServerBootstrap(drpcConfiguration,registry,ctx);
    }

}
