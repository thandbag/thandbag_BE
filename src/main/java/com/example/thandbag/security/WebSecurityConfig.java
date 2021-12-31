package com.example.thandbag.security;

import com.example.thandbag.security.filter.JwtAuthFilter;
import com.example.thandbag.security.jwt.HeaderTokenExtractor;
import com.example.thandbag.security.provider.JWTAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JWTAuthProvider jwtAuthProvider;
    private final HeaderTokenExtractor headerTokenExtractor;

    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }


    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // CustomAuthenticationProvider()를 호출하기 위해서 Overriding
        auth
                .authenticationProvider(jwtAuthProvider);

        auth.inMemoryAuthentication()
                .withUser("happydaddy")
                .password("{noop}1234")
                .roles("USER")
                .and()
                .withUser("angrydaddy")
                .password("{noop}1234")
                .roles("USER")
                .and()
                .withUser("guest")
                .password("{noop}1234")
                .roles("GUEST");
    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                //H2-Console 허용
                .antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/user/signup").permitAll()
                .antMatchers("/api/user/login").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/chat/**").permitAll()
                .antMatchers("/chat/room/**").permitAll()
                .antMatchers("/sub/chat/room/**").permitAll()
                .antMatchers("/pub/chat/room/**").permitAll()
                .antMatchers("/ws-stomp/sub/chat/room/**").permitAll()
                .antMatchers("/ws-stomp/pub/chat/room/**").permitAll()
                .antMatchers("**/pub/chat/room/**").permitAll()
                .antMatchers("**/sub/chat/room/**").permitAll()
                .antMatchers(
                        "/v2/api-docs",
                        "/swagger-resources/**",
                        "**/swagger-resources/**",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/swagger/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/health"
                ).permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/chat/**").hasRole("USER") // chat으로 시작하는 리소스에 대한 접근 권한 설정
                .anyRequest().permitAll()
//                // [로그아웃 기능]
//                .logout()
//                // 로그아웃 요청 처리 URL
//                .logoutUrl("/user/logout")
//                .permitAll()
                .and()
                .exceptionHandling();

        http
                .csrf().disable()
                .cors().and()// 기본값이 on인 csrf 취약점 보안을 해제한다. on으로 설정해도 되나 설정할경우 웹페이지에서 추가처리가 필요함.
                .headers()
                .frameOptions().sameOrigin(); // SockJS는 기본적으로 HTML iframe 요소를 통한 전송을 허용하지 않도록 설정되는데 해당 내용을 해제한다.

        // 서버에서 인증은 JWT로 인증하기 때문에 Session의 생성을 막습니다.
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    // JwtFilter : 서버에 접근시 JWT 확인 후 인증을 실시합니다.
    private JwtAuthFilter jwtFilter() throws Exception {
        List<String> skipPathList = new ArrayList<>();

        // 회원 관리 API 허용
        skipPathList.add("POST,/api/user/signup");
        skipPathList.add("POST,/api/user/login");
        // h2-console 허용
        skipPathList.add("GET,/h2-console/**");
        skipPathList.add("POST,/h2-console/**");
        // Swagger 허용
        skipPathList.add("GET,/swagger-ui.html");
        skipPathList.add("GET,/swagger/**");
        skipPathList.add("GET,/swagger-resources/**");
        skipPathList.add("GET,/webjars/**");
        skipPathList.add("GET,/v2/api-docs");
        skipPathList.add("GET,configuration/ui");
        skipPathList.add("GET,/configuration/security");
        skipPathList.add("GET,/health");
        // 기본 허용 사항들
        skipPathList.add("GET,/");
        skipPathList.add("GET,/favicon.ico");
        // 채팅
        skipPathList.add("GET,/chat/room/**");
        skipPathList.add("GET,/sub/chat/room/**");
        skipPathList.add("GET,/pub/chat/room/**");
        skipPathList.add("GET,/ws-stomp/pub/chat/room/**");
        skipPathList.add("GET,**/pub/chat/room/**");
        skipPathList.add("GET,**/sub/chat/room/**");
        skipPathList.add("GET,/ws-stomp/**");



        FilterSkipMatcher matcher = new FilterSkipMatcher(
                skipPathList,
                "/**"
        );

        JwtAuthFilter filter = new JwtAuthFilter(
                matcher,
                headerTokenExtractor
        );
        filter.setAuthenticationManager(super.authenticationManagerBean());

        return filter;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}