package com.ma.controller;

import com.ma.pojo.User;
import com.ma.service.impl.UserService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference(mock = "fail:return null")
    private UserService userService;

    @RequestMapping("/sayHello")
    public String sayHello() {
        return userService.sayHello();
    }

    @RequestMapping("/find")
    public User find(int id) {
        return userService.findUserById(id);
    }

}
