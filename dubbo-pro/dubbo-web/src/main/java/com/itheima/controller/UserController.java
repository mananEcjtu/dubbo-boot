package com.itheima.controller;

import com.itheima.pojo.User;
import com.itheima.service.UserService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    // @Autowired // 本地注入

    /**
     * 1.从zookeeper获取userService的访问url
     * 2.rpc调用
     * 3.将结构封装为代理对象，给变量赋值
     */
    //@Reference(mock = "force:return null")// 不再调用userService
    @Reference(mock = "fail:return null")// 失败后调用3次返回null
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
