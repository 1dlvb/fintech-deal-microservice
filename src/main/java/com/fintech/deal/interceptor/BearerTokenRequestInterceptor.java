package com.fintech.deal.interceptor;

import com.fintech.deal.service.TokenService;
import com.google.common.net.HttpHeaders;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Provides token to a feign requests
 * @author Matushkin Anton
 */
@Component
@Configuration
@RequiredArgsConstructor
public class BearerTokenRequestInterceptor implements RequestInterceptor {

    private final TokenService tokenService;

    @Override
    public void apply(RequestTemplate template) {
        final String authorization = HttpHeaders.AUTHORIZATION;
        if (tokenService.getToken() != null) {
                template.header(authorization);
                template.header(authorization, "Bearer " + tokenService.getToken());
        }
    }

}
