package com.jit.sports.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/page")
public class pageController {
    @GetMapping("/test")
    public String test1() {
        return "hellotest";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/mysports")
    public String mysports() {
        return "mysports";
    }
    @GetMapping("/detail")
    public String detail() {
        return "detail";
    }
}
