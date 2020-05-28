package com.example.demo.impl;

import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;//Spring的JdbcTemplate是自动配置的，可直接使用

    @Override
    public int create(String name, Integer age) {
        return 0;
    }

    @Override
    public void deleteByName(String name) {

    }

    @Override
    public Integer getUsersCount() {
        return jdbcTemplate.queryForObject("select count(1) from userdetail", Integer.class);
    }

    @Override
    public List<Map<String, Object>> getUsersId() {
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select userid,username,gender,age,mobile,userno,regtime from userdetail");
        return list;
    }

    @Override
    public void deleteAllUsers() {

    }
}
