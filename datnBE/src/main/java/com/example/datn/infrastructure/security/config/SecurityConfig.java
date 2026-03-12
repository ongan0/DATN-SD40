package com.example.datn.infrastructure.security.config;

import com.example.datn.infrastructure.constant.MappingConstants;
import com.example.datn.infrastructure.constant.RoleConstant;
import com.example.datn.infrastructure.security.exception.RestAuthenticationEntryPoint;
import com.example.datn.infrastructure.security.filter.TokenAuthenticationFilter;
import com.example.datn.infrastructure.security.oauth2.CustomOAuth2UserService;
import com.example.datn.infrastructure.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.example.datn.infrastructure.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.example.datn.infrastructure.security.oauth2.OAuth2AuthenticationSuccessHandler;

import com.example.datn.infrastructure.security.service.CustomUserDetailsService;
import com.example.datn.utils.Helper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Setter(onMethod = @__({@Autowired}))
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Setter(onMethod = @__({@Autowired}))
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Setter(onMethod = @__({@Autowired}))
    private CustomOAuth2UserService customOAuth2UserService;

    @Setter(onMethod = @__({@Autowired}))
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Value("${frontend.url}")
    private String allowedOrigin;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManager(
            PasswordEncoder passwordEncoder,
            CustomUserDetailsService userDetailsService
    ) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));
        config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "*"));
        config.setAllowedOrigins(Collections.singletonList(allowedOrigin));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Authorization"));
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource, TokenAuthenticationFilter tokenAuthenticationFilter) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(e ->
                        e.authenticationEntryPoint(new RestAuthenticationEntryPoint())
                )
                .authorizeHttpRequests(auth -> auth
                        // Upload & static files
                        .requestMatchers("/api/upload/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        // Auth endpoints (login, register, forgot-password, verify-otp, refresh)
                        .requestMatchers(MappingConstants.API_AUTH_PREFIX + "/**").permitAll()
                        // Public product/client endpoints
                        .requestMatchers(MappingConstants.API_COMMON + "/**").permitAll()
                        .requestMatchers("/api/v1/product-image/**").permitAll()
                        .requestMatchers("/api/v1/admin/product-image/**").permitAll()
                        // OAuth2 endpoints
                        .requestMatchers("/oauth2/**").permitAll()
                        // Public READ-ONLY: catalog data accessible by everyone (CUSTOMER, guest)
                        .requestMatchers(HttpMethod.GET, MappingConstants.API_ADMIN_PREFIX + "/product-category/**").permitAll()
                        .requestMatchers(HttpMethod.GET, MappingConstants.API_ADMIN_PREFIX + "/product/**").permitAll()
                        .requestMatchers(HttpMethod.GET, MappingConstants.API_ADMIN_PREFIX + "/tech-spec/**").permitAll()
                        .requestMatchers(HttpMethod.GET, MappingConstants.API_ADMIN_PREFIX + "/banner/**").permitAll()
                        .requestMatchers(HttpMethod.GET, MappingConstants.API_ADMIN_PREFIX + "/products/**").permitAll()
                        // Admin endpoints — chỉ ADMIN và STAFF
                        .requestMatchers(MappingConstants.API_ADMIN_PREFIX + "/**")
                            .hasAnyAuthority(RoleConstant.ADMIN.name(), RoleConstant.STAFF.name())
                        // Customer endpoints — chỉ CUSTOMER
                        .requestMatchers(MappingConstants.API_VERSION_PREFIX + "/customer/**")
                            .hasAuthority(RoleConstant.CUSTOMER.name())
                        // Tất cả request còn lại phải authenticated
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(
                tokenAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}
