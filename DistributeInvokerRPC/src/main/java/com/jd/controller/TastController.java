package com.jd.controller;

import com.jd.entity.User;
import com.jd.service.IUserService;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class TastController {
    @Resource(name = "userServiceProxy")
    private IUserService userService;
    @GetMapping("/test/get")
    @SneakyThrows
    public User queryUserByid(Integer id){

        return userService.queryUserById(id);
    }
}
