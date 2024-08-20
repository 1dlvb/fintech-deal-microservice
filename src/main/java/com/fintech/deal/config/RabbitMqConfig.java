package com.fintech.deal.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up RabbitMQ components.
 * @author Matushkin Anton
 */
@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.queue.name}")
    private String queueActiveMainBorrowerQueueName;

    @Value("${rabbitmq.caching-connection-factory.uri}")
    private String uri;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    /**
     * Defines a queue for processing active main borrower messages.
     * <p>
     * @return the configured {@link Queue} for active main borrowers.
     */
    @Bean
    public Queue activeMainBorrowerQueue() {
        return new Queue(queueActiveMainBorrowerQueueName, true);
    }

    /**
     * Configures a {@link CachingConnectionFactory} for connecting to RabbitMQ.
     * <p>
     * @return the configured {@link CachingConnectionFactory}.
     */
    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(uri);
        cachingConnectionFactory.setUsername(username);
        cachingConnectionFactory.setPassword(password);
        return cachingConnectionFactory;
    }

    /**
     * Provides a {@link RabbitAdmin} instance for managing RabbitMQ resources.
     * <p>
     * @return the configured {@link RabbitAdmin}.
     */
    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(cachingConnectionFactory());
    }

}
