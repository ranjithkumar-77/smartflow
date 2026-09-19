package com.smartflow.smartflow.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "Welcome to SmartFlow!";
    }

    @GetMapping("/api/customer/test")
    public String customerTest() {
        return "Customer access successful!";
    }
}