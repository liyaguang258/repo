package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
public class HelloController {

  /*  @RequestMapping("/index")
    @ResponseBody
    public String getHello() {
        return "222";
    }*/

    @RequestMapping("/")
    public String index(Model model){
        model.addAttribute("msg","like");
        return "index";
    }

}
