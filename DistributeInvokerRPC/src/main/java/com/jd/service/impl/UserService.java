package com.jd.service.impl;

import com.jd.annotation.DRPCProvider;
import com.jd.entity.User;
import com.jd.service.IUserService;
import org.springframework.stereotype.Service;

import javax.xml.ws.ServiceMode;

@Service
@DRPCProvider(name = "userService",targetInterface = IUserService.class)
public class UserService implements IUserService {
    @Override
    public User queryUserById(Integer id) {
        return new User().setId(id).setName("用户ID："+id);
    }
}
