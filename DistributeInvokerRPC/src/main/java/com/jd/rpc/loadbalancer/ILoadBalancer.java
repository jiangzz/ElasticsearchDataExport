package com.jd.rpc.loadbalancer;

import com.jd.rpc.model.HostAndPort;

import java.util.List;

public interface ILoadBalancer {
    public HostAndPort select(List<HostAndPort> hostAndPorts);
}
