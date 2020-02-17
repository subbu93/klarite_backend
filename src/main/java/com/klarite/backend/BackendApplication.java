package com.klarite.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.klarite.backend.service", "com.klarite.backend.dto", "com.klarite.backend.controller"})
public class BackendApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(BackendApplication.class, args);
    }

}
