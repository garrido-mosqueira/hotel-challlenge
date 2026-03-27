package com.fran.hotel.persistence.adapter;

import com.fran.hotel.domain.model.Reservation;
import com.fran.hotel.domain.model.ReservationStatus;
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
    
    private void confirmPayment(Reservation reservation) {
        log.info("Confirming payment for reservation '{}'", reservation.id());
        var currentReservation = persistencePort.findById(reservation.id());
        if (currentReservation.isEmpty()) {
            log.info("Reservation '{}' not found during payment confirmation", reservation.id());
            return;
        }

        Reservation current = currentReservation.get();
        if (current.status() == ReservationStatus.CANCELLED || current.status() == ReservationStatus.REFUNDED) {
            log.info("Skipping payment confirmation for reservation '{}' because it is '{}'", reservation.id(), current.status());
            return;
        }

        persistencePort.save(current.confirm());
        log.info("Payment confirmed for reservation '{}'", reservation.id());
    }
}
