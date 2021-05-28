package com.jd.registry;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.Date;

@Data
@Slf4j
public class ZookeeperRegistry {
    private String application;
    private CuratorFramework client=null;

    public ZookeeperRegistry(String quorum,String application) {
        this.application=application;
      client= CuratorFrameworkFactory.newClient(quorum, new ExponentialBackoffRetry(1000,10));
      client.start();
    }
    @SneakyThrows
    public void registryService(Class clazz,Integer port){

        String basePath="/"+application+"/service/"+clazz.getCanonicalName();
        log.info("注册服务路径 {}",basePath);
        Stat stat = client.checkExists().forPath(basePath);
        if(stat==null){
            String path = client.create()
                    .creatingParentContainersIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(basePath);
            log.info("服务节点不存在,成功创建服务节点信息{}",path);
        }
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        String servicePath=basePath+"/"+hostAddress+":"+port;
        stat = client.checkExists().forPath(servicePath);
        if(stat!=null){
            client.delete().forPath(servicePath);
            log.info("服务路径 {} 已经存在，删除该服务路径",servicePath);
        }else{
            client.create().withMode(CreateMode.EPHEMERAL).forPath(servicePath);
            log.info("注册服务 {} 成功",servicePath);
        }

    }

    public static void main(String[] args) {
        ZookeeperRegistry registry = new ZookeeperRegistry("CentOS:2181","DistributeRPC");
        registry.registryService(Date.class,8990);
    }

}
