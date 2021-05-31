package com.jd.rpc.configuration;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jd.rpc.annotation.DRPCProvider;
import com.jd.rpc.model.ProviderBeanMapContext;
import com.jd.rpc.process.AbstractMessageProcessor;
import com.jd.rpc.process.impl.MethodInvokeMessageProcessor;
import com.jd.rpc.registry.Registry;
import com.jd.rpc.registry.impl.ZookeeperRegistry;
import com.jd.rpc.transport.RPCClient;
import com.jd.rpc.transport.RPCServer;
import com.jd.rpc.transport.netty.NettyRPCServer;
import com.jd.rpc.transport.netty.NettyRPCClient;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;

import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DRPCAutoConfiguration implements ApplicationListener<ApplicationReadyEvent> {

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public Registry zookeeperRegistry(DRPCConfiguration drpcConfiguration){
        return new ZookeeperRegistry(drpcConfiguration.getZookeeperServers(),drpcConfiguration.getApplicationName());
    }

    @Bean(initMethod = "start",destroyMethod = "close")
    @ConditionalOnMissingBean
    public RPCServer nettyServer(DRPCConfiguration drpcConfiguration, AbstractMessageProcessor messageProcessor) {
        return new NettyRPCServer(drpcConfiguration.getServerPort(), messageProcessor);
    }

    @Bean
    @ConditionalOnMissingBean
    public AbstractMessageProcessor messageProcessor() {
        return new MethodInvokeMessageProcessor();
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public RPCClient nettyClient(DRPCConfiguration drpcConfiguration, ApplicationContext ctx) {
        return new NettyRPCClient();
    }
    @Bean
    @ConditionalOnMissingBean
    public ThreadPoolExecutor drpcThreadPoolExecutor(DRPCConfiguration drpcConfiguration){
        return new ThreadPoolExecutor(drpcConfiguration.getCoreSize(),drpcConfiguration.getMaxPoolSize(),1000, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<Runnable>(drpcConfiguration.getQueueSize()),
                new ThreadFactoryBuilder()
                        .setDaemon(true)
                        .setNameFormat("drpc-ThreadPoolExecutor-%d")
                        .setPriority(5)
                        .build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        ConfigurableApplicationContext ctx = applicationReadyEvent.getApplicationContext();
        Registry registry = ctx.getBean(Registry.class);
        DRPCConfiguration configuration = ctx.getBean(DRPCConfiguration.class);
        AbstractMessageProcessor messageProcessor = ctx.getBean(AbstractMessageProcessor.class);

        Map<String, Object> beans = ctx.getBeansWithAnnotation(DRPCProvider.class);
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            DRPCProvider provider = entry.getValue().getClass().getAnnotation(DRPCProvider.class);
            registry.registryService(provider.targetInterface(),configuration.getServerPort());
            ProviderBeanMapContext.register(provider.targetInterface(),entry.getValue());
        }
    }
}
