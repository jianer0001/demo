package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@Document(indexName = "test")
public class User {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String username;

    @Field(type = FieldType.Integer)
    private Integer age;

    @Field(type = FieldType.Date)
    @JsonFormat(pattern="yyyy-MM-dd", timezone = "GMT+8")
    private Date birth;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String intro;

    public User(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public User() {

    }
}

