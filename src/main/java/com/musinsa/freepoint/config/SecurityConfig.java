
package com.musinsa.freepoint.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs.yaml",
            "/h2-console/**",
            "/webjars/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults());
        return http.build();
    }
    /*

    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // NOTE: For demo, endpoints are permitted (JWT wiring can be added easily if needed)
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(reg -> reg
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/actuator/**", "/h2-console/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/**").permitAll()
                .anyRequest().permitAll())
            .headers(h -> h.frameOptions(f -> f.disable())) // H2 console
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }*/
}
