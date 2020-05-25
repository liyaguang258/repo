package com.example.demo.controller;

import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/index")
    public String index(Model model) {
        List<Map<String, Object>> list = userService.getUsersId();
        ArrayList<Object> list1 = new ArrayList<>();
        for (Map<String, Object> map : list) {
            Collection<Object> values = map.values();
            for (Object value : values) {
                list1.add(value);
            }

        }
        model.addAttribute("msg", "like");
        return "index";
    }
}
