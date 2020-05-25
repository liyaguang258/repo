package com.example.demo.service;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserService {
    /**
     * @param name
     * @param age
     */
    int create(String name, Integer age);

    /**
     * 根据用户名删除用户
     *
     * @param name
     */
    void deleteByName(String name);

    /**
     * 获取用户总数
     *
     * @return
     */
    Integer getUsersCount();

    /**
     * 获取用户总数
     *
     * @return
     */
    List<Map<String, Object>> getUsersId();

    /**
     * 删除所有用户
     */
    void deleteAllUsers();
}
