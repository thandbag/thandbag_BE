package com.example.thandbag;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ThandbagApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThandbagApplication.class, args);
    }
}

