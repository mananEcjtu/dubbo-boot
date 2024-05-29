package com.ma.service.impl;

import com.ma.pojo.User;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

@Service(timeout = 3000, retries = 3, version = "v1.0", weight = 100)
@Component
public class UserServiceImpl implements UserService {

    @Override
    public String sayHello() {
        return "hello !";
    }

    @Override
    public User findUserById(int id) {
        System.out.println("---1");
        // 查询user对象
        User user = new User(1, "ma", "123");
//        try {
//            TimeUnit.SECONDS.sleep(3);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return user;
    }
}
