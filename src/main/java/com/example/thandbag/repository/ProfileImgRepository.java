package com.example.thandbag.repository;

import com.example.thandbag.model.ProfileImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileImgRepository extends JpaRepository<ProfileImg, Long> {
    Optional<ProfileImg> findByProfileImgUrl(String profileImgUrl);
}
