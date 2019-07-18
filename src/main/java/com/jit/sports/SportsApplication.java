package com.jit.sports;

import com.jit.sports.mqtt.MySubscribe;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SportsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SportsApplication.class, args);
        MySubscribe mySubscribe = new MySubscribe();
    }

}