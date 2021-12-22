package com.example.thandbag.service;

import com.example.thandbag.dto.SignupRequestDto;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.UserRepository;
import com.example.thandbag.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;

    @Transactional
    public void userRegister(SignupRequestDto signupRequestDto) {

        Optional<User> foundUsername = userRepository.findByUsername(signupRequestDto.getUsername());
        Optional<User> foundNickname = userRepository.findByNickname(signupRequestDto.getUsername());

        //유저 아이디 중복 검사
        userValidator.checkUsername(foundUsername);
        //유저 닉네임 중복 검사
        userValidator.checkNickname(foundNickname);
        //유효성 검사
        userValidator.checkValid(signupRequestDto);

        //비밀번호 인코딩
        String password = passwordEncoder.encode(signupRequestDto.getPassword());

        //유저 저장 (회원가입 완료)
        User user = new User(signupRequestDto);
        user.setPassword(password);
        userRepository.save(user);
    }
}
