package com.jd.rpc.proxy;

import com.jd.rpc.annotation.DRPCConsumer;
import com.jd.rpc.annotation.DRPCConsumerMethod;
import com.jd.rpc.cluster.DRPCCluster;
import com.jd.rpc.cluster.DRPCFailfastCluster;
import com.jd.rpc.cluster.DRPCFailoverCluster;
import com.jd.rpc.loadbalancer.ILoadBalancer;
import com.jd.rpc.loadbalancer.RandomLoadBalancer;
import com.jd.rpc.loadbalancer.RoundRobinLoadBalancer;
import com.jd.rpc.model.HostAndPort;
import com.jd.rpc.model.MethodInvokeContext;
import com.jd.rpc.model.ResultContext;
import com.jd.rpc.model.DRPCContextRequest;
import com.jd.rpc.registry.Registry;
import com.jd.rpc.transport.netty.NettyRPCClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Data
public class JDKInterfaceProxy<T> implements InvocationHandler, FactoryBean<T> {
     private Long timeout=1000L;
     private TimeUnit timeUnit=TimeUnit.MICROSECONDS;

     private String loadbalance="random";
     private String cluster="failfast";
     private int retries=3;

    private DRPCCluster drpcCluster;
    private  ILoadBalancer loadBalancer;
    private ThreadPoolExecutor poolExecutor;

     private Class<? extends T> targetClass;
     private Registry registry;
     private NettyRPCClient client;
     private List<HostAndPort> hostAndPorts=new ArrayList<>();

    public JDKInterfaceProxy(Long timeout, TimeUnit timeUnit, Class<? extends T> targetClass, Registry registry, NettyRPCClient client) {
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.targetClass = targetClass;
        this.registry=registry;
        this.client=client;
        registry.subscribeService(hostAndPorts,targetClass);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        int count=0;
        while(hostAndPorts.size()==0  && count <5){
            count++;
            registry.retriveService(hostAndPorts,targetClass);
            Thread.sleep(1000);
            log.info("尝试获取服务列表 {} 次",count);
        }

        if(targetClass.isAnnotationPresent(DRPCConsumer.class)){
            DRPCConsumer consumerAnno = targetClass.getAnnotation(DRPCConsumer.class);
            loadbalance=consumerAnno.loadBalance();
            timeout=consumerAnno.timeout();
            timeUnit=consumerAnno.timeUnit();
            cluster=consumerAnno.cluster();
            retries=consumerAnno.retries();
        }
        if(method.isAnnotationPresent(DRPCConsumerMethod.class)){
            DRPCConsumerMethod consumerMethod = method.getAnnotation(DRPCConsumerMethod.class);
            loadbalance = consumerMethod.loadBalancer();
            timeout=consumerMethod.timeout();
            timeUnit=consumerMethod.timeUnit();
            cluster=consumerMethod.cluster();
            retries=consumerMethod.retries();

        }
        if(loadBalancer==null){
            switch (loadbalance){
                case "random":{
                    loadBalancer=new RandomLoadBalancer(System.currentTimeMillis());
                    break;
                }
                case "roundrobin":{
                    loadBalancer=new RoundRobinLoadBalancer();
                    break;
                }
                default:{
                    throw new RuntimeException("目前仅仅支持 roundrobin/random负载均衡策略");
                }
            }
        }
        if(drpcCluster==null){
            switch (cluster){
                case "failover":{
                    drpcCluster=new DRPCFailoverCluster(client,retries);
                    break;
                }
                case "failfast":{
                    drpcCluster=new DRPCFailfastCluster(client);
                    break;
                }
                default:{
                    throw new RuntimeException("目前仅仅支持 failover/failfast集群容错策略");
                }
            }
        }
        MethodInvokeContext invokeContext=new MethodInvokeContext()
                .setMethodName(method.getName())
                .setArgs(args)
                .setParameterTypes(method.getParameterTypes())
                .setTimeout(timeout)
                .setTimeUnit(timeUnit)
                .setAttachments(DRPCContextRequest.getAttachments())
                .setHost(InetAddress.getLocalHost().getHostAddress())
                .setTargetClass(targetClass);

        long startTime=System.currentTimeMillis();
        ResultContext context=null;
        if(poolExecutor!=null){
            Future<ResultContext> contextFuture = poolExecutor.submit(() -> drpcCluster.invoke(invokeContext, loadBalancer, hostAndPorts));
            context = contextFuture.get(timeout*retries,TimeUnit.MILLISECONDS);
        }else{
            context = drpcCluster.invoke(invokeContext, loadBalancer, hostAndPorts);
        }
        log.info("获取来自{}响应耗时 {}秒 远程服务执行 {} 秒",context.getResponseHost(),((System.currentTimeMillis()-startTime)/1000.0),context.getTook()/1000.0);

        if(context.getRuntimeError()==null){
            DRPCContextRequest.getAttachments().putAll(context.getAttachments());
            return context.getReturnValue();
         }else {
            throw  context.getRuntimeError();
         }
    }
    @Override
    public T getObject() throws Exception {
         return (T) Proxy.newProxyInstance(targetClass.getClassLoader(),new Class[]{targetClass},this);
    }
    @Override
    public Class<?> getObjectType() {
        return targetClass;
    }
}
