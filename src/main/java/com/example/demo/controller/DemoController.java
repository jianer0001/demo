package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jian
 * @version 1.0.0
 * @since 2021年12月05日 14:59:00
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping("/name")
    public String demo(String name) {
        return name;
    }
}
