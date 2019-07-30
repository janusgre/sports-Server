package com.jit.sports.controller;

import com.jit.sports.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RequestMapping("/user")
@RestController
public class userController {

    @Resource
    UserService userService;


    //用户登录
    @RequestMapping("/login")
    public String login(@RequestParam(value = "userName") String userName,
                       @RequestParam(value = "password") String password) {
        if(userService.login(userName, password) == null) {
            return "false";
        }
        return "true";
    }

    //用户注册
    @RequestMapping("/reg")
    public String reg(@RequestParam(value = "userName") String userName,
                       @RequestParam(value = "password") String password) {
        //检查是否被注册
        if(userService.existUserName(userName) != null) {
            return "false";
        }
        userService.reg(userName, password);
        return "true";
    }

    @RequestMapping("/hello")
    public String test() {
        System.out.println("hello");
        return "hello";
    }

}
