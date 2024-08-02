package com.fintech.deal.feign.config;

import com.fintech.deal.interceptor.BearerTokenRequestInterceptor;
import com.fintech.deal.service.TokenService;
import feign.Client;
import feign.codec.Encoder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Feign clients.
 * This configuration class defines beans required for setting up Feign clients,
 * including an encoder and an HTTP client.
 * @author Matushkin Anton
 */
@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    @NonNull
    private final ObjectFactory<HttpMessageConverters> messageConverters;

    /**
     * Provides a Feign encoder bean.
     * @return the Feign encoder
     */
    @Bean
    public Encoder feignEncoder() {
        return new SpringEncoder(this.messageConverters);
    }

    /**
     * Provides a Feign HTTP client bean.
     * @return the Feign HTTP client
     */
    @Bean
    public Client feignClient() {
        CloseableHttpClient httpClient = HttpClients.custom().build();
        return new feign.httpclient.ApacheHttpClient(httpClient);
    }

    /**
     * Provides a JWT request interceptor bean.
     * @return the JWT request interceptor
     */
    @Bean
    public BearerTokenRequestInterceptor jwtRequestInterceptor(TokenService tokenService) {
        return new BearerTokenRequestInterceptor(tokenService);
    }

}
