package com.example.thandbag.security.provider;

import com.example.thandbag.model.User;
import com.example.thandbag.repository.UserRepository;
import com.example.thandbag.security.UserDetailsImpl;
import com.example.thandbag.security.jwt.JwtDecoder;
import com.example.thandbag.security.jwt.JwtPreProcessingToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTAuthProvider implements AuthenticationProvider {
    private final JwtDecoder jwtDecoder;

    private final UserRepository userRepository;

    // JWT 토큰 유효성 검사
    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String token = (String) authentication.getPrincipal();
        String username = jwtDecoder.decodeUsername(token);

        //  API 사용시마다 매번 User DB 조회 필요
        //  -> 해결을 위해서는 UserDetailsImpl 에 User 객체를 저장하지 않도록 수정
        //  ex) UserDetailsImpl 에 userId, username, role 만 저장
        //      -> JWT 에 userId, username, role 정보를 암호화/복호화하여 사용
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("토큰 정보가 존재하지 않습니다."));
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtPreProcessingToken.class.isAssignableFrom(authentication);
    }
//
//    @Value("${spring.jwt.secret}")
//    private String secretKey;
//
//    private long tokenValidMilisecond = 1000L * 60 * 60; // 1시간만 토큰 유효
//
//    /**
//     * 이름으로 Jwt Token을 생성한다.
//     */
//    public String generateToken(String name) {
//        Date now = new Date();
//        return Jwts.builder()
//                .setId(name)
//                .setIssuedAt(now) // 토큰 발행일자
//                .setExpiration(new Date(now.getTime() + tokenValidMilisecond)) // 유효시간 설정
//                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
//                .compact();
//    }

    /**
     * Jwt Token을 복호화 하여 이름을 얻는다.
     */
//    public String getUserNameFromJwt(String jwt) {
//        return getClaims(jwt).getBody().getId();
//    }
//
//    /**
//     * Jwt Token의 유효성을 체크한다.
//     */
//    public boolean validateToken(String jwt) {
//        return this.getClaims(jwt) != null;
//    }
//
//    private Jws<Claims> getClaims(String jwt) {
//        try {
//            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
//        } catch (SignatureException ex) {
//            log.error("Invalid JWT signature");
//            throw ex;
//        } catch (MalformedJwtException ex) {
//            log.error("Invalid JWT token");
//            throw ex;
//        } catch (ExpiredJwtException ex) {
//            log.error("Expired JWT token");
//            throw ex;
//        } catch (UnsupportedJwtException ex) {
//            log.error("Unsupported JWT token");
//            throw ex;
//        } catch (IllegalArgumentException ex) {
//            log.error("JWT claims string is empty.");
//            throw ex;
//        }
//    }
}