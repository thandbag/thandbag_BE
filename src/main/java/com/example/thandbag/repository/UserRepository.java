package com.example.thandbag.repository;

import com.example.thandbag.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
