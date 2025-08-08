package com.example.cuoikyjavaa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.cuoikyjavaa", "com.example.devicemanager"})
public class CuoiKyJavaaApplication {

    public static void main(String[] args) {
        SpringApplication.run(CuoiKyJavaaApplication.class, args);
    }

}
