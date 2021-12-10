package com.example.demo.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ConsumerListener {

    @KafkaListener(topics = "test-topic1")
    public void onMessage1(String message) {
        System.out.println("我是第一个消费者:" + message);
    }

    @KafkaListener(topics = "test-topic2")
    public void onMessage2(String message) {
        System.out.println("我是第二个消费者:" + message);
    }
}