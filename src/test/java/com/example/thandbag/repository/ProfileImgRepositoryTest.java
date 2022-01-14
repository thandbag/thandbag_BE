package com.example.thandbag.repository;

import com.example.thandbag.model.ProfileImg;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
class ProfileImgRepositoryTest {

    @Autowired
    ProfileImgRepository profileImgRepository;

    @BeforeEach
    void setup() {

        // 테스트를 위한 데이터 생성
        Long id = 1L;
        String profileImgUrl = "test1.jpg";

        // ProfileImg1 생성
        ProfileImg profileImg1 = new ProfileImg(id, profileImgUrl);

        // ProfileImg2 생성
        id = 2L;
        profileImgUrl = "test2.jpg";
        ProfileImg profileImg2 = new ProfileImg(id, profileImgUrl);

        // DB에 저장
        profileImgRepository.save(profileImg1);
        profileImgRepository.save(profileImg2);
    }

    @Order(1)
    @DisplayName("성공 - 전체검색")
    @Test
    void saveAndFindAll() {

        //when
        List<ProfileImg> result = profileImgRepository.findAll();

        //then
        assertEquals(2, result.size());

    }

    @Order(2)
    @DisplayName("성공 - profileImgUrl검색")
    @Test
    void findByProfileImgUrl() {

        //when
        Optional<ProfileImg> result = profileImgRepository.findByProfileImgUrl("test2.jpg");

        //then
        assertNotEquals(Optional.empty(), result);
    }

    @Order(3)
    @DisplayName("결과없음 - profileImgUrl검색")
    @Test
    void findByProfileImgUrl2() {

        //when
        Optional<ProfileImg> result = profileImgRepository.findByProfileImgUrl("test3.jpg");

        //then
        assertEquals(Optional.empty(), result);
    }
}