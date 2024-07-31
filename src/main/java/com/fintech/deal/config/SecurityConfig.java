package com.fintech.deal.config;

import com.onedlvb.jwtlib.filter.JWTAuthenticationFilter;
import com.onedlvb.jwtlib.util.Roles;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Class that configures the security settings for the application.
 * @author Matushkin Anton
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @NonNull
    private JWTAuthenticationFilter authenticationFilter;

    /**
     * Configures the security filter chain.
     * <p>
     * @param http the HTTP security configuration
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/**").hasAnyAuthority(
                                Roles.SUPERUSER.name(),
                                Roles.DEAL_SUPERUSER.name())
                        .requestMatchers(HttpMethod.GET, "/deal/**").hasAnyAuthority(
                                Roles.USER.name(),
                                Roles.CREDIT_USER.name(),
                                Roles.OVERDRAFT_USER.name(),
                                Roles.DEAL_SUPERUSER.name(),
                                Roles.CONTRACTOR_SUPERUSER.name(),
                                Roles.SUPERUSER.name(),
                                Roles.CONTRACTOR_RUS.name()
                        ).requestMatchers("/deal/search").hasAnyAuthority(
                                Roles.CREDIT_USER.name(),
                                Roles.OVERDRAFT_USER.name(),
                                Roles.SUPERUSER.name(),
                                Roles.DEAL_SUPERUSER.name()
                        )
                        .anyRequest().authenticated()
                )
                .sessionManagement(manager ->
                        manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

}
