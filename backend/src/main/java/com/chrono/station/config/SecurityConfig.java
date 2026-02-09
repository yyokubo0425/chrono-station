package com.chrono.station.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // CSRF無効
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // ★ 全部許可
                )
                .formLogin(login -> login.disable()) // ★ ログイン画面も無効
                .logout(logout -> logout.disable()); // ログアウトも無効

        return http.build();
    }
}
