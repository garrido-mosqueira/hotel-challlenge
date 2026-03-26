package com.fran.hotel.persistence.adapter;

import com.fran.hotel.domain.model.Reservation;
import com.fran.hotel.domain.port.ReservationPersistencePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class RabbitMQPaymentConsumer {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQPaymentConsumer.class);
    private static final long PAYMENT_DELAY_SECONDS = 60;
    public static final String QUEUE_NAME = "reservation-payment-queue";
    public static final String CANCEL_QUEUE_NAME = "reservation-payment-cancel-queue";

    private final ReservationPersistencePort persistencePort;
    private final TaskScheduler taskScheduler;

    public RabbitMQPaymentConsumer(ReservationPersistencePort persistencePort,
                                   TaskScheduler taskScheduler) {
        this.persistencePort = persistencePort;
        this.taskScheduler = taskScheduler;
    }

    @RabbitListener(queues = QUEUE_NAME)
    public void consume(Reservation reservation) {
        log.info("Received payment process for reservation '{}' - will confirm after {} seconds", 
                 reservation.id(), PAYMENT_DELAY_SECONDS);
        
        Instant confirmationTime = Instant.now().plus(PAYMENT_DELAY_SECONDS, ChronoUnit.SECONDS);
        taskScheduler.schedule(() -> confirmPayment(reservation), confirmationTime);
    }
    
    @RabbitListener(queues = CANCEL_QUEUE_NAME)
    public void consumeCancel(String reservationId) {
        log.info("Received payment cancel request for reservation '{}'", reservationId);
        var reservation = persistencePort.findById(reservationId);
        if (reservation.isPresent()) {
            persistencePort.save(reservation.get().cancel());
            log.info("Payment cancelled for reservation '{}'", reservationId);
        } else {
            log.info("Reservation '{}' not found for cancellation", reservationId);
        }
    }
    
    private void confirmPayment(Reservation reservation) {
        log.info("Confirming payment for reservation '{}'", reservation.id());
        persistencePort.save(reservation.confirm());
        log.info("Payment confirmed for reservation '{}'", reservation.id());
    }
}
