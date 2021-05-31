package com.jd.service;

import com.jd.rpc.annotation.DRPCConsumer;
import com.jd.rpc.annotation.DRPCConsumerMethod;
import com.jd.entity.User;

@DRPCConsumer(targetInterface = IUserService.class,timeout = 1000L,name="userServiceProxy",cluster = "failfast")
public interface IUserService {
    @DRPCConsumerMethod(timeout = 1000L,loadBalancer = "random",cluster = "failover")
    public User queryUserById(Integer id) throws InterruptedException;
}
