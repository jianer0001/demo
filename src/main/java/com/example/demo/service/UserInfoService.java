package com.example.demo.service;

import com.example.demo.entity.UserInfoBean;

/**
 * @author jian
 * @version 1.0.0
 * @since 2021年12月05日 18:23:00
 */
public interface UserInfoService {
    UserInfoBean getUserInfoById(Integer id);

    UserInfoBean updateUserInfo(UserInfoBean userInfoBean);

    int detectUserInfo(Integer id);

}
