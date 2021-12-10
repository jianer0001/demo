package com.example.demo.service.impl;

import com.example.demo.entity.UserInfoBean;
import com.example.demo.mapper.UserInfoMapper;
import com.example.demo.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author jian
 * @version 1.0.0
 * @since 2021年12月05日 18:23:00
 */
@Service
@CacheConfig(cacheNames = "users")
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoMapper userInfoMapper;

    public UserInfoServiceImpl(UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

    @Override
    @Cacheable(key = "#id")
    public UserInfoBean getUserInfoById(Integer id) {
        log.info("查询数据库");
        return userInfoMapper.getUserInfoById(id);
    }

    @Override
    @CachePut(key = "#userInfoBean.id",condition = "#result != null")
    public UserInfoBean updateUserInfo(UserInfoBean userInfoBean) {
        int i = userInfoMapper.updateUserInfo(userInfoBean);
        if (i >0) {
            return userInfoBean;
        }else {
            return null;
        }
    }

    @Override
    @CacheEvict
    public int detectUserInfo(Integer id) {
         return userInfoMapper.deleteUserInfo(id);
    }

}
