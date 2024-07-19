package com.fintech.deal.feign.config;

import feign.Client;
import feign.codec.Encoder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    @NonNull
    private final ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    public Encoder feignEncoder() {
        return new SpringEncoder(this.messageConverters);
    }

    @Bean
    public Client feignClient() {
        CloseableHttpClient httpClient = HttpClients.custom().build();
        return new feign.httpclient.ApacheHttpClient(httpClient);
    }
//
//    @Bean
//    public ErrorDecoder errorDecoder() {
//        return new CustomErrorDecoder();
//    }

}
