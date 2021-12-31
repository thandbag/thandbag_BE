package com.example.thandbag;

import com.example.thandbag.model.LvImg;
import com.example.thandbag.repository.LvImgRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ThandbagApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThandbagApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(LvImgRepository lvImgRepository) {
        return (args) -> {
            System.out.println("test test");
            lvImgRepository.save(new LvImg("image1.jpg"));
            lvImgRepository.save(new LvImg("image2.jpg"));
        };
    }

}

