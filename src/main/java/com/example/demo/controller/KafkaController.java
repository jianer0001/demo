package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaController(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/send")
    public boolean send(@RequestParam String message) {
        try {
            kafkaTemplate.send("test-topic1", message);
            kafkaTemplate.send("test-topic2", message);
            System.out.println("消息发送成功...");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    @GetMapping("/test")
    public String test() {
        System.out.println("hello world!");
        return "ok";
    }
}