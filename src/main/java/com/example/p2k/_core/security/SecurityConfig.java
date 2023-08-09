package com.example.p2k._core.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration // spring security filter(SecurityConfig)가 스프링 필터체인에 등록된다.
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.headers().frameOptions().sameOrigin(); // h2-console 접속위해서 필요한 설정
        httpSecurity.csrf().disable()
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers(new AntPathRequestMatcher("/h2-console/**"),
                                                new AntPathRequestMatcher("/css/**"),
                                                new AntPathRequestMatcher("/js/**"),
                                                new AntPathRequestMatcher("/user/check/**"),
                                                new AntPathRequestMatcher("/assets/**"),
                                                new AntPathRequestMatcher("/user/join/**")).permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(
                        formlogin -> formlogin
                                .loginPage("/user/login")
                                .loginProcessingUrl("/user/login")
                                .usernameParameter("email")
                                .defaultSuccessUrl("/vm", true)
                                .permitAll()
                )
                .logout(
                        Logout -> Logout
                                .logoutUrl("/user/logout")
                                .logoutSuccessUrl("/user/login")
                );

        return httpSecurity.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
