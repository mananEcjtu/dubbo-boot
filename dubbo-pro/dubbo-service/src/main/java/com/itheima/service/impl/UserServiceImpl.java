package com.itheima.service.impl;

import com.itheima.pojo.User;
import com.itheima.service.UserService;
import org.apache.dubbo.config.annotation.Service;

import java.util.concurrent.TimeUnit;

// @Service
// 将这个类提供的方法对外发布，将访问的ip路径端口注册
@Service
// @Service(timeout = 3000, retries = 2, version = "v1.0", weight = 100) // 当前服务3s超时
public class UserServiceImpl implements UserService {

    public String sayHello() {
        return "hello dubbo hello! 1...";
    }

    int i = 0;
    public User findUserById(int id) {
        System.out.println("---1");
        // 查询user对象
        User user = new User(1, "ma", "123");
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return user;
    }
}
