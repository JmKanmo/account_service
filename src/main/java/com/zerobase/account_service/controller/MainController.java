package com.zerobase.account_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/")
public class MainController {
    @GetMapping
    public String main() {
        return "redirect:/swagger-ui/";
    }
}
