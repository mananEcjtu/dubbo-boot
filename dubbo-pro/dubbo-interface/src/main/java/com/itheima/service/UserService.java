package com.itheima.service;

import com.itheima.pojo.User;

public interface UserService {

    String sayHello();

    /*
    * 查询用户
    * */
    User findUserById(int id);

}
