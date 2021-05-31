package com.jd.rpc.loadbalancer;

import com.jd.rpc.model.HostAndPort;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer implements ILoadBalancer{
    private Random random;
    public RandomLoadBalancer(long seed) {
        random=new Random(seed);
    }

    @Override
    public HostAndPort select(List<HostAndPort> hostAndPorts) {
        if(hostAndPorts.size()==0){
            throw new RuntimeException("当前没有可用服务！");
        }
        return hostAndPorts.get(random.nextInt(hostAndPorts.size()));
    }
}
