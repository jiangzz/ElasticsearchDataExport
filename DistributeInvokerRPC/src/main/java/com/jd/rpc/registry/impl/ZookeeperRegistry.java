package com.jd.rpc.registry.impl;

import com.jd.rpc.model.HostAndPort;
import com.jd.rpc.registry.Registry;
import com.jd.rpc.util.YamlParser;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.net.InetAddress;
import java.util.List;
@Slf4j
public class ZookeeperRegistry implements Registry {

    private String application;
    private CuratorFramework client;

    public ZookeeperRegistry(String quorum,String application) {
        this.application=application;
      client= CuratorFrameworkFactory.newClient(quorum, new ExponentialBackoffRetry(1000,10));
      client.start();
    }

    @Override
    @SneakyThrows
    public void retriveService(List<HostAndPort> hostAndPorts, Class serviceInterface){
        hostAndPorts.clear();
        String base_path="/drpc/"+application+"/service/"+serviceInterface.getCanonicalName();
        Stat stat = client.checkExists().forPath(base_path);
        if(stat==null){
            String path = client.create()
                    .creatingParentContainersIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(base_path);
            log.info("服务节点不存在,成功创建服务节点信息{}",path);
        }
        List<String> childrenPath = client.getChildren().forPath(base_path);
        if(childrenPath!=null && childrenPath.size()>0){
            for (int i = 0; i < childrenPath.size(); i++) {
                HostAndPort hostAndPort = YamlParser.loadObject(new String(client.getData().forPath(base_path + "/" + childrenPath.get(i))), HostAndPort.class);
                hostAndPorts.add(hostAndPort);
            }
        }
    }

    @Override
    @SneakyThrows
    public void subscribeService(List<HostAndPort> hostAndPorts, Class serviceInterface){
        String base_path="/drpc/"+application+"/service/"+serviceInterface.getCanonicalName();
        Stat stat = client.checkExists().forPath(base_path);
        if(stat==null){
            String path = client.create()
                    .creatingParentContainersIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(base_path);
            log.info("服务节点不存在,成功创建服务节点信息{}",path);
        }
        PathChildrenCache   nodeCache = new PathChildrenCache(client, base_path,true);
        nodeCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        nodeCache.getListenable().addListener(new PathChildrenCacheListener() {

            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                List<String> children = client.getChildren().forPath(base_path);
                hostAndPorts.clear();
                for (String child : children) {
                    byte[] bytes = client.getData().forPath(base_path + "/" + child);
                    HostAndPort hostAndPort = YamlParser.loadObject(new String(bytes), HostAndPort.class);
                    hostAndPorts.add(hostAndPort);
                }
                log.info("更新节点{} 信息,共计更新了 {} 个",base_path,hostAndPorts.size());
            }
        });
    }

    @Override
    @SneakyThrows
    public void registryService(Class clazz,Integer port){
        String base_path="/drpc/"+application+"/service/"+clazz.getCanonicalName();
        Stat stat = client.checkExists().forPath(base_path);
        if(stat==null){
            String path = client.create()
                    .creatingParentContainersIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(base_path);

            log.info("服务节点不存在,成功创建服务节点信息{}",path);
        }

        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        String servicePath=base_path+"/"+hostAddress+":"+port;
        stat = client.checkExists().forPath(servicePath);

        if(stat!=null){
            client.delete().forPath(servicePath);
        }
        client.create().withMode(CreateMode.EPHEMERAL).forPath(servicePath, YamlParser.dumpObject(new HostAndPort(hostAddress,port)).getBytes());
        log.info("注册服务节点{}成功",servicePath);
    }
    @Override
    public void close(){
        client.close();
    }
    @Override
    public String getApplication() {
        return application;
    }
}
