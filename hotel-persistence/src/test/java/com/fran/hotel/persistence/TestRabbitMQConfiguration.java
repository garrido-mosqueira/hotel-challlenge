package com.fran.hotel.persistence;

import com.fran.hotel.domain.port.ReservationPaymentPort;
import com.fran.hotel.domain.port.ReservationPersistencePort;
import com.fran.hotel.persistence.adapter.TestReservationPaymentAdapter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@TestConfiguration
public class TestRabbitMQConfiguration {

    @Bean
    @Primary
    public TaskScheduler paymentTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("test-payment-");
        scheduler.initialize();
        return scheduler;
    }

    @Bean
    @Primary
    public ReservationPaymentPort testReservationPayment(ReservationPersistencePort persistencePort,
                                                         TaskScheduler paymentTaskScheduler) {
        return new TestReservationPaymentAdapter(persistencePort, paymentTaskScheduler);
    }
}
