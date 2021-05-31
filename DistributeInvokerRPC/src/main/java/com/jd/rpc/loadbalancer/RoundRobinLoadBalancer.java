package com.jd.rpc.loadbalancer;

import com.jd.rpc.model.HostAndPort;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer  implements ILoadBalancer{
    private AtomicInteger numbers=new AtomicInteger(0);
    @Override
    public HostAndPort select(List<HostAndPort> hostAndPorts) {
        int num = numbers.getAndIncrement();
        if(num<0){
            num=0;
            numbers.set(0);
        }
        if(hostAndPorts.size()==0){
            throw new RuntimeException("当前没有可用服务！");
        }
        System.out.println(num+"\t"+num%hostAndPorts.size());
        return hostAndPorts.get(num%hostAndPorts.size());
    }
}
