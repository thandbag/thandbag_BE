package com.example.thandbag.repository;

import com.example.thandbag.model.LvImg;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
class LvImgRepositoryTest {

    @Autowired
    LvImgRepository lvImgRepository;

    @BeforeEach
    void setup() {

        /* 테스트를 위한 데이터 생성 */
        LvImg lvImg1 = LvImg.builder()
                .title("기본")
                .level(1)
                .lvImgUrl("default.jpg")
                .build();

        LvImg lvImg2 = LvImg.builder()
                .title("터짐")
                .level(2)
                .lvImgUrl("boom.jpg")
                .build();

        /* DB에 저장 */
        lvImgRepository.save(lvImg1);
        lvImgRepository.save(lvImg2);
    }

    @Order(1)
    @DisplayName("성공 - 전체검색")
    @Test
    void saveAndFindAll() {

        /* when */
        List<LvImg> result = lvImgRepository.findAll();

        /* then */
        assertEquals(2, result.size());
        assertEquals("기본", result.get(0).getTitle());
        assertEquals("boom.jpg", result.get(1).getLvImgUrl());

    }

    @Order(2)
    @DisplayName("성공 - 타이틀+레벨검색")
    @Test
    void findByTitleAndLevel() {

        /* when */
        LvImg result = lvImgRepository.findByTitleAndLevel("기본", 1);

        /* then */
        assertNotNull(result);
        assertEquals("default.jpg", result.getLvImgUrl());
        assertEquals(1, result.getLevel());
        assertEquals("기본", result.getTitle());
    }

    @Order(3)
    @DisplayName("결과없음 - 타이틀+레벨검색")
    @Test
    void findByTitleAndLevel2() {

        /* when */
        LvImg result = lvImgRepository.findByTitleAndLevel("기본", 2);

        /* then */
        assertNull(result);
    }

}