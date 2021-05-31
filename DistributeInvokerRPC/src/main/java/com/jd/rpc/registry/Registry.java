package com.jd.rpc.registry;

import com.jd.rpc.model.HostAndPort;
import com.jd.rpc.util.YamlParser;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.net.InetAddress;
import java.util.List;

public interface Registry {
    void retriveService(List<HostAndPort> hostAndPorts, Class serviceInterface);

    void subscribeService(List<HostAndPort> hostAndPorts, Class serviceInterface);

    void registryService(Class clazz, Integer port);

    void close();

    String getApplication();
}
