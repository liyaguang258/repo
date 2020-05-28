package com.example.demo.controller;

import com.example.demo.service.UserService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/query")
    public String index(Model model) {
        List<Map<String, Object>> list = userService.getUsersId();
        ArrayList<Object> list1 = new ArrayList<>();
        for (Map<String, Object> map : list) {
            Collection<Object> values = map.values();
            for (Object value : values) {
                list1.add(value);
            }
        }
        model.addAllAttributes(list1);
        model.addAttribute("msg", "like");
        model.addAttribute("list", list1);
        return "index";
    }

    @RequestMapping("/list")
    @ResponseBody
    public Object list(HttpServletResponse request, int page, int limit){
        int before = limit * (page - 1);
        int after = page * limit;
        List<Map<String, Object>> mapList = userService.getUsersId();
//        List<Map<String,Object>>  mapList =UserService.findAllPage(before,after);
        int count= mapList.size();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("msg", "msc");
        jsonObject.put("count", count);
        jsonObject.put("data", mapList);
        return jsonObject.toString();

    }

}
