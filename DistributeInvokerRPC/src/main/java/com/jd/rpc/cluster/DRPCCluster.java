package com.jd.rpc.cluster;

import com.jd.rpc.loadbalancer.ILoadBalancer;
import com.jd.rpc.model.HostAndPort;
import com.jd.rpc.model.MethodInvokeContext;
import com.jd.rpc.model.ResultContext;
import com.jd.rpc.transport.netty.NettyRPCClient;
import lombok.Data;

import java.util.List;

@Data
public abstract class DRPCCluster {
    private NettyRPCClient client;

    public DRPCCluster(NettyRPCClient client) {
        this.client = client;
    }

    public abstract ResultContext invoke(MethodInvokeContext methodInvokeContext, ILoadBalancer loadBalancer, List<HostAndPort> hostAndPorts);
}
