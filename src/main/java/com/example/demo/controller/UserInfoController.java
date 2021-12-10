package com.example.demo.controller;

import com.example.demo.entity.UserInfoBean;
import com.example.demo.service.UserInfoService;
import org.springframework.web.bind.annotation.*;

/**
 * @author jian
 * @version 1.0.0
 * @since 2021年12月05日 18:58:00
 */
@RestController
@RequestMapping("/user")
public class UserInfoController {

    private final UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @GetMapping("/id")
    public UserInfoBean getUserInfo(int id) {
        return userInfoService.getUserInfoById(id);
    }

    @PutMapping("/update")
    public UserInfoBean updateUserInfo(UserInfoBean userInfoBean) {
        return userInfoService.updateUserInfo(userInfoBean);
    }

    @DeleteMapping("/delete")
    public int deleteUserInfo(Integer id) {
        return userInfoService.detectUserInfo(id);
    }
}
