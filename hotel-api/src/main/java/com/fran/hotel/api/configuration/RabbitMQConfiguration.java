package com.fran.hotel.api.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fran.hotel.domain.port.ReservationPersistencePort;
import com.fran.hotel.persistence.adapter.RabbitMQPaymentConsumer;
import com.fran.hotel.persistence.adapter.RabbitMQReservationPaymentAdapter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class RabbitMQConfiguration {

    public static final String QUEUE_NAME = "reservation-payment-queue";
    public static final String EXCHANGE_NAME = "reservation-payment-exchange";
    public static final String ROUTING_KEY = "reservation.payment.start";
    
    public static final String CANCEL_QUEUE_NAME = "reservation-payment-cancel-queue";
    public static final String CANCEL_ROUTING_KEY = "reservation.payment.cancel";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, false);
    }
    
    @Bean
    public Queue cancelQueue() {
        return new Queue(CANCEL_QUEUE_NAME, false);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
    
    @Bean
    public Binding cancelBinding(Queue cancelQueue, TopicExchange exchange) {
        return BindingBuilder.bind(cancelQueue).to(exchange).with(CANCEL_ROUTING_KEY);
    }

    @Bean
    @ConditionalOnBean(ConnectionFactory.class)
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    @ConditionalOnBean(ConnectionFactory.class)
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public TaskScheduler paymentTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("payment-");
        scheduler.initialize();
        return scheduler;
    }

    @Bean
    @ConditionalOnBean(ConnectionFactory.class)
    public RabbitMQReservationPaymentAdapter rabbitMQReservationPaymentAdapter(RabbitTemplate rabbitTemplate,
                                                                               ReservationPersistencePort persistencePort) {
        return new RabbitMQReservationPaymentAdapter(rabbitTemplate, persistencePort);
    }

    @Bean
    @ConditionalOnBean(ConnectionFactory.class)
    public RabbitMQPaymentConsumer rabbitMQPaymentConsumer(ReservationPersistencePort persistencePort,
                                                           TaskScheduler paymentTaskScheduler) {
        return new RabbitMQPaymentConsumer(persistencePort, paymentTaskScheduler);
    }
}
