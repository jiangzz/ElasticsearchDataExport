package com.jd.rpc.cluster;

import com.jd.rpc.loadbalancer.ILoadBalancer;
import com.jd.rpc.model.HostAndPort;
import com.jd.rpc.model.MethodInvokeContext;
import com.jd.rpc.model.ResultContext;
import com.jd.rpc.transport.netty.NettyRPCClient;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j
public class DRPCFailoverCluster extends DRPCCluster {

    private int retries;
    public DRPCFailoverCluster(NettyRPCClient client, int retries) {
        super(client);
        this.retries=retries;
    }

    @Override
    public ResultContext invoke(MethodInvokeContext methodInvokeContext, ILoadBalancer loadBalancer, List<HostAndPort> hostAndPorts) {
        int count=0;
        while (count<retries){
            try {
                count++;
                HostAndPort hostAndPort = loadBalancer.select(hostAndPorts);
                return getClient().invoke(hostAndPort, methodInvokeContext);
            }catch (Exception e){
                e.printStackTrace();
                log.warn("尝试("+count+"/"+retries+")次请求！");
            }
        }
        throw new RuntimeException("已经尝试了"+count+"次之后失败！");
    }
}
