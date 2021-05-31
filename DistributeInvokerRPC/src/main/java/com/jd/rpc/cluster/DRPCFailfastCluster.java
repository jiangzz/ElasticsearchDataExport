package com.jd.rpc.cluster;

import com.jd.rpc.loadbalancer.ILoadBalancer;
import com.jd.rpc.model.HostAndPort;
import com.jd.rpc.model.MethodInvokeContext;
import com.jd.rpc.model.ResultContext;
import com.jd.rpc.transport.netty.NettyRPCClient;

import java.util.List;

public class DRPCFailfastCluster extends DRPCCluster {
    public DRPCFailfastCluster(NettyRPCClient client) {
        super(client);
    }
    @Override
    public ResultContext invoke(MethodInvokeContext methodInvokeContext, ILoadBalancer loadBalancer, List<HostAndPort> hostAndPorts) {
        HostAndPort hostAndPort = loadBalancer.select(hostAndPorts);
        ResultContext resultContext = getClient().invoke(hostAndPort,methodInvokeContext);
        return resultContext;
    }
}
