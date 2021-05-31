package com.jd.service.impl;

import com.jd.rpc.annotation.DRPCProvider;
import com.jd.entity.User;
import com.jd.service.IUserService;
import org.springframework.stereotype.Service;

@Service
@DRPCProvider(targetInterface = IUserService.class,timeout = 1000L)
public class UserService implements IUserService {
    @Override
    public User queryUserById(Integer id) throws InterruptedException {
        return new User().setId(id).setName("用户ID："+id);
    }
}