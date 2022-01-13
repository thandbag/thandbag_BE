package com.example.thandbag.validator;

import com.example.thandbag.dto.signup.SignupRequestDto;
import com.example.thandbag.model.User;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class UserValidator {

    public void checkUsername(Optional<User> foundUsername) {
        if (foundUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 이메일이 존재합니다.");
        }
    }

    public void checkNickname(Optional<User> foundNickname) {
        if (foundNickname.isPresent()) {
            throw new IllegalArgumentException("중복된 닉네임이 존재합니다.");
        }
    }

    public void checkValid(SignupRequestDto signupRequestDto) {
        // 공백 입력 관련 유효성 검사
        if (signupRequestDto.getUsername().equals("")) {
            throw new IllegalArgumentException("아이디는 필수 입력 값 입니다.");
        }
        if (signupRequestDto.getPassword().equals("")) {
            throw new IllegalArgumentException("비밀번호는 필수 입력 값 입니다.");
        }
        if (signupRequestDto.getNickname().equals("")) {
            throw new IllegalArgumentException("닉네임은 필수 입력 값 입니다.");
        }
        if (signupRequestDto.getUsername().contains(" ")) {
            throw new IllegalArgumentException("아이디는 공백을 포함할 수 없습니다.");
        }
        if (signupRequestDto.getPassword().contains(" ")) {
            throw new IllegalArgumentException("비밀번호는 공백을 포함할 수 없습니다.");
        }
        if (signupRequestDto.getNickname().contains(" ")) {
            throw new IllegalArgumentException("닉네임은 공백을 포함할 수 없습니다.");
        }

        // 이메일 형식 검사
        if (!signupRequestDto.getUsername().contains("@") || !signupRequestDto.getUsername().contains(".")) {
            throw new IllegalArgumentException("이메일 형식을 확인해주세요.");
        }

        // 비밀번호 입력 형식 유효성 검사
        if (!Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$", signupRequestDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호는 영문, 숫자, 특수문자를 모두 포함한 8자 이상으로 입력해야 합니다.");
        }

        // 닉네임 입력 형식 유효성 검사
        if (!Pattern.matches("^[a-zA-Z0-9가-힣]{2,6}$", signupRequestDto.getNickname())) {
            throw new IllegalArgumentException("닉네임은 영문, 한글, 숫자로 이루어진 2~6자로 작성해주세요.");
        }
    }

    public void checkPassword(String password) {
        if (password.contains(" ")) {
            throw new IllegalArgumentException("비밀번호는 공백을 포함할 수 없습니다.");
        }

        if (!Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$", password)) {
            throw new IllegalArgumentException("비밀번호는 영문, 숫자, 특수문자를 모두 포함한 8자 이상으로 입력해야 합니다.");
        }
    }

    public void checkNicknameIsValid(String nickname) {
        if (nickname.contains(" ")) {
            throw new IllegalArgumentException("닉네임은 공백을 포함할 수 없습니다.");
        }

        if (!Pattern.matches("^[a-zA-Z0-9가-힣]{2,6}$", nickname)) {
            throw new IllegalArgumentException("닉네임은 영문, 한글, 숫자로 이루어진 2~6자로 작성해주세요.");
        }
    }

}