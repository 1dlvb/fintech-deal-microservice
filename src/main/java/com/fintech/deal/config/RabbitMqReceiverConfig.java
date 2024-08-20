package com.fintech.deal.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for setting up RabbitMQ consumer.
 * <p>
 * @author Matushkin Anton
 */
@Configuration
public class RabbitMqReceiverConfig {

    @Value("${contractor.exchange.name}")
    private String contractorExchange;

    @Value("${contractor.deals.queue}")
    private String dealsContractorQueue;

    @Value("${contractor.deals.routing.key}")
    private String routingKey;

    @Value("${rabbitmq.dead-letter.exchange}")
    private String deadLetterExchange;

    @Value("${rabbitmq.dead-letter.queue}")
    private String deadLetterQueue;

    @Value("${rabbitmq.dead-letter.routing.key}")
    private String deadLetterRoutingKey;

    @Value("${rabbitmq.dead-letter.message.ttl}")
    private long deadLetterMessageTTL;

    /**
     * Defines a queue for contractor deals with dead-letter exchange settings.
     * <p>
     * @return the configured {@link Queue} for contractor deals.
     */
    @Bean
    public Queue dealsContractorQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", deadLetterExchange);
        arguments.put("x-dead-letter-routing-key", deadLetterRoutingKey);
        return new Queue(dealsContractorQueue, true, false, false, arguments);
    }

    /**
     * Defines a dead-letter queue for handling undeliverable messages.
     * <p>
     * @return the configured {@link Queue} for dead-letter messages.
     */
    @Bean
    public Queue deadLetterQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-message-ttl", deadLetterMessageTTL);
        arguments.put("x-dead-letter-exchange", contractorExchange);

        return new Queue(deadLetterQueue, true, false, false, arguments);
    }

    /**
     * Defines a topic exchange for routing messages related to contractors.
     * <p>
     * @return the configured {@link TopicExchange} for contractor messages.
     */
    @Bean
    public Exchange contractorExchange() {
        return new TopicExchange(contractorExchange);
    }

    /**
     * Defines a direct exchange for handling dead-letter messages.
     * <p>
     * @return the configured {@link DirectExchange} for dead-letter messages.
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(deadLetterExchange);
    }

    /**
     * Creates a binding between the contractor deals queue and the contractor exchange.
     * <p>
     * @return the {@link Binding} between the contractor deals queue and the contractor exchange.
     */
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(dealsContractorQueue())
                .to(contractorExchange())
                .with(routingKey)
                .noargs();
    }

    /**
     * Creates a binding between the dead-letter queue and the dead-letter exchange.
     * <p>
     * @return the {@link Binding} between the dead-letter queue and the dead-letter exchange.
     */
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(deadLetterRoutingKey);
    }

}
