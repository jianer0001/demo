package com.example.demo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jian
 * @version 1.0.0
 * @since 2021年12月05日 18:46:00
 */
@Data
public class UserInfoBean implements Serializable {
    private Integer id;
    private String name;
    private Integer age;
    private Boolean gender;
    private String address;
}
