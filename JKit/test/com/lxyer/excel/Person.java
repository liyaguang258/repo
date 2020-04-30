package com.lxyer.excel;

import java.util.Date;

/**
 * Created by liangxianyou at 2018/7/6 10:28.
 */
public class Person {
    private String name;
    private int age;
    private Date bir;

    public Person() {
    }

    public Person(String name, int age, Date bir) {
        this.name = name;
        this.age = age;
        this.bir = bir;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getBir() {
        return bir;
    }

    public void setBir(Date bir) {
        this.bir = bir;
    }
}
