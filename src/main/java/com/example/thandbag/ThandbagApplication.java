package com.example.thandbag;

import com.example.thandbag.model.LvImg;
import com.example.thandbag.model.PostImg;
import com.example.thandbag.model.ProfileImg;
import com.example.thandbag.repository.LvImgRepository;
import com.example.thandbag.repository.PostImgRepository;
import com.example.thandbag.repository.ProfileImgRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@EnableJpaAuditing
@SpringBootApplication
public class ThandbagApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThandbagApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(LvImgRepository lvImgRepository, ProfileImgRepository profileImgRepository) {
        return (args) -> {
            System.out.println("default image 저장");
            lvImgRepository.save(new LvImg("얼빡배너 기본", "https://thandbag.s3.ap-northeast-2.amazonaws.com/Lv_1+%EC%96%BC%EB%B9%A1%EB%B0%B0%EB%84%88/lv%3D1.jpg", 1));
            lvImgRepository.save(new LvImg("얼빡배너 쳐맞음", "https://thandbag.s3.ap-northeast-2.amazonaws.com/Lv_1+%EC%96%BC%EB%B9%A1%EB%B0%B0%EB%84%88/lv%3D2.jpg", 1));
            lvImgRepository.save(new LvImg("얼빡배너 터짐", "https://thandbag.s3.ap-northeast-2.amazonaws.com/Lv_1+%EC%96%BC%EB%B9%A1%EB%B0%B0%EB%84%88/lv%3D3.jpg", 1));
            lvImgRepository.save(new LvImg("얼빡배너 기본", "https://thandbag.s3.ap-northeast-2.amazonaws.com/Lv_2+%EC%96%BC%EB%B9%A1%EB%B0%B0%EB%84%88/lv%3D1.jpg", 2));
            lvImgRepository.save(new LvImg("얼빡배너 쳐맞음", "https://thandbag.s3.ap-northeast-2.amazonaws.com/Lv_2+%EC%96%BC%EB%B9%A1%EB%B0%B0%EB%84%88/lv%3D2.jpg", 2));
            lvImgRepository.save(new LvImg("얼빡배너 터짐", "https://thandbag.s3.ap-northeast-2.amazonaws.com/Lv_2+%EC%96%BC%EB%B9%A1%EB%B0%B0%EB%84%88/lv%3D3.jpg", 2));
            lvImgRepository.save(new LvImg("얼빡배너 기본", "https://thandbag.s3.ap-northeast-2.amazonaws.com/Lv_3+%EC%96%BC%EB%B9%A1%EB%B0%B0%EB%84%88/lv%3D1.jpg", 3));
            lvImgRepository.save(new LvImg("얼빡배너 쳐맞음", "https://thandbag.s3.ap-northeast-2.amazonaws.com/Lv_3+%EC%96%BC%EB%B9%A1%EB%B0%B0%EB%84%88/lv%3D2.jpg", 3));
            lvImgRepository.save(new LvImg("얼빡배너 터짐", "https://thandbag.s3.ap-northeast-2.amazonaws.com/Lv_3+%EC%96%BC%EB%B9%A1%EB%B0%B0%EB%84%88/lv%3D3.jpg", 3));

            profileImgRepository.save(new ProfileImg( "https://thandbag.s3.ap-northeast-2.amazonaws.com/%ED%94%84%EB%A1%9C%ED%95%84%EC%9D%B4%EB%AF%B8%EC%A7%80_%EC%83%81%EC%84%B8_%EA%B8%80%EC%9E%91%EC%84%B1%EC%9E%90/option%3D1.jpg"));
            profileImgRepository.save(new ProfileImg( "https://thandbag.s3.ap-northeast-2.amazonaws.com/%ED%94%84%EB%A1%9C%ED%95%84%EC%9D%B4%EB%AF%B8%EC%A7%80_%EC%83%81%EC%84%B8_%EA%B8%80%EC%9E%91%EC%84%B1%EC%9E%90/option%3D2.jpg"));
            profileImgRepository.save(new ProfileImg( "https://thandbag.s3.ap-northeast-2.amazonaws.com/%ED%94%84%EB%A1%9C%ED%95%84%EC%9D%B4%EB%AF%B8%EC%A7%80_%EC%83%81%EC%84%B8_%EA%B8%80%EC%9E%91%EC%84%B1%EC%9E%90/option%3D3.jpg"));
        };
    }

    //배포시 시간을 맞추기 위함
    @PostConstruct
    public void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

}

