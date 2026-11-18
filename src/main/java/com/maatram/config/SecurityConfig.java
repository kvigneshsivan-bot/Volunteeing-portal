package com.maatram.config;

import com.maatram.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    UserDetailsService userDetailsService(UserRepository users) {
        return username -> users.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(basic -> {})
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/organisations/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/events").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/announcements").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/announcements/*/availability").hasRole("VOLUNTEER")
                        .requestMatchers("/api/announcements/admin/**", "/api/announcements/admin").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/contributions/*/status").hasAnyRole("ZONE_INCHARGE", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/contributions/mine/**").hasRole("VOLUNTEER")
                        .requestMatchers(HttpMethod.POST, "/api/contributions").hasRole("VOLUNTEER")
                        .requestMatchers(HttpMethod.POST, "/api/events").hasRole("ORGANISER")
                        .requestMatchers(HttpMethod.GET, "/api/events/mine").hasRole("ORGANISER")
                        .requestMatchers("/api/events/pending", "/api/events/*/status").hasRole("SUPER_ADMIN")
                        .requestMatchers("/api/health").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/notifications/broadcast").hasRole("SUPER_ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("SUPER_ADMIN")
                        .requestMatchers("/api/zone/**").hasAnyRole("ZONE_INCHARGE", "SUPER_ADMIN")
                        .anyRequest().authenticated())
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "https://kvigneshsivan-bot.github.io"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
