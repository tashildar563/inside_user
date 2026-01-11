package com.user.inside_user.security;

import com.user.inside_user.security.jwtAuth.AuthTokenFilter;
import com.user.inside_user.security.jwtAuth.JwtAuthEntryPoint;
import com.user.inside_user.security.user.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
/**
 * The WebSecurityConfig class is a configuration class for Spring Security.
 * It uses the @Configuration, @EnableMethodSecurity, and @EnableWebSecurity annotations to indicate that it's a configuration class and to enable method-level security.
 * The class uses field-based dependency injection to inject the UserDetailService and JwtAuthEntryPoint.
 * It includes beans for the AuthTokenFilter, PasswordEncoder, DaoAuthenticationProvider, AuthenticationManager, and SecurityFilterChain.
 * The SecurityFilterChain bean configures the security filter chain, including CSRF protection, exception handling, session management, and request authorization.
 * The class also configures the authentication provider and adds the AuthTokenFilter before the UsernamePasswordAuthenticationFilter in the filter chain.
 */
@Configuration
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired
    private UserDetailService userDetailsService;
    @Autowired
    private JwtAuthEntryPoint jwtAuthEntryPoint;

    public WebSecurityConfig() {
    }

    @Bean
    public AuthTokenFilter authenticationTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(this.userDetailsService);
        authProvider.setPasswordEncoder(this.passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling((exception) -> {
                    exception.authenticationEntryPoint(this.jwtAuthEntryPoint);
                })
                .sessionManagement((session) -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .authorizeHttpRequests((auth) -> {
                    auth.requestMatchers("/authentication/login").permitAll()
                            .requestMatchers("/authentication/register-user").hasAnyRole("ADMIN")
                            .requestMatchers("/authentication/update").hasAnyRole("ADMIN","USER_ONE")
                            .requestMatchers("/authentication/delete/**").hasAnyRole("ADMIN")
                            .requestMatchers("/users/delete/**").hasAnyRole("ADMIN")
                            .requestMatchers("/users/**").hasAnyRole("ADMIN","USER_ONE")
                            .requestMatchers("/inside_user/roles/delete/**").hasAnyRole("ADMIN")
                            .requestMatchers("/inside_user/roles/all-roles").hasAnyRole("ADMIN","USER_ONE")
                            .anyRequest().authenticated();  // All other requests require authentication
                });
        http.authenticationProvider(this.authenticationProvider());
        http.addFilterBefore(this.authenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return (SecurityFilterChain)http.build();
    }
}
