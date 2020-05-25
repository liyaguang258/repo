package com.example.demo.dao;


import javax.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "userdetail")
public class Userdetail implements Serializable {
    @Id
    private int userid;

    private String username;

    private int age;

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
