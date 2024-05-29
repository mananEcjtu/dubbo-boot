package com.itheima.service.impl;

import com.itheima.pojo.User;
import com.itheima.service.UserService;
import org.apache.dubbo.config.annotation.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service(timeout = 3000, retries = 2, version = "v2.0")
public class UserServiceImpl2 implements UserService {
    public String sayHello() {
        return "hello dubbo hello!";
    }

    int i = 0;
    public User findUserById(int id) {
        System.out.println("new v2");
        // 查询user对象
        User user = new User(1, "ma", "123");
        return user;
    }
}
