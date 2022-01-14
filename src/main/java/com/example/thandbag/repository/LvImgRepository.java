package com.example.thandbag.repository;

import com.example.thandbag.model.LvImg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LvImgRepository extends JpaRepository<LvImg, Long> {
    LvImg findByTitleAndLevel(String title, int level);
}
