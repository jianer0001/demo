package com.example.demo.mapper;

import com.example.demo.entity.UserInfoBean;

/**
 * @author jian
 * @version 1.0.0
 * @since 2021年12月05日 18:22:00
 */
public interface UserInfoMapper {
    UserInfoBean getUserInfoById(Integer id);

    int updateUserInfo(UserInfoBean userInfoBean);

    int deleteUserInfo(Integer id);
}
