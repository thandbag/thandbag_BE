package com.example.thandbag.service;

import com.example.thandbag.dto.LoginRequestDto;
import com.example.thandbag.dto.LoginResultDto;
import com.example.thandbag.dto.SignupRequestDto;
import com.example.thandbag.model.User;
import com.example.thandbag.repository.UserRepository;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.security.jwt.JwtTokenUtils;
import com.example.thandbag.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;

    // 회원가입
    @Transactional
    public void userRegister(SignupRequestDto signupRequestDto) {

        Optional<User> foundUsername = userRepository.findByUsername(signupRequestDto.getUsername());
        Optional<User> foundNickname = userRepository.findByNickname(signupRequestDto.getNickname());

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

    // 로그인
    public LoginResultDto userLogin(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        User user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));

        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호를 확인해주세요.");
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        String token = JwtTokenUtils.generateJwtToken(userDetails);
        response.addHeader("Authorization", "Bearer " + token);
        return new LoginResultDto(user.getId(), user.getNickname());
    }
}
