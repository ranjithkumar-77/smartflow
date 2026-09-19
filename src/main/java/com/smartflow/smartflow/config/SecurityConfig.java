package com.smartflow.smartflow.config;

import com.smartflow.smartflow.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .httpBasic(httpBasic -> httpBasic.disable())

            .formLogin(formLogin -> formLogin.disable())

            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                )
                .accessDeniedHandler((request, response, accessDeniedException) ->
                    response.setStatus(HttpStatus.FORBIDDEN.value())
                )
            )

            .authorizeHttpRequests(auth -> auth

                .requestMatchers("/", "/index.html", "/style.css", "/app.js", "/favicon.ico", "/h2-console/**",
                        "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/users", "/api/login").permitAll()

                .requestMatchers(HttpMethod.POST, "/api/technicians").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/technicians/me").hasRole("TECHNICIAN")
                .requestMatchers(HttpMethod.GET, "/api/technicians/**").permitAll()

                .requestMatchers("/api/skills/**").permitAll()

                .requestMatchers("/api/customer/**").hasRole("CUSTOMER")

                .requestMatchers("/api/reviews").hasRole("CUSTOMER")

                .requestMatchers(HttpMethod.POST, "/api/service-requests").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.GET, "/api/service-requests/my").hasRole("CUSTOMER")

                .requestMatchers("/api/request-offers/**").authenticated()

                .requestMatchers("/api/service-requests/*/start").authenticated()

                .requestMatchers("/api/service-requests/*/resolve").authenticated()

                .requestMatchers("/api/service-requests/*/close").hasRole("CUSTOMER")

                .requestMatchers("/api/service-requests/**").hasRole("CUSTOMER")

                .anyRequest().authenticated()
            );

        http.addFilterBefore(
            jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}