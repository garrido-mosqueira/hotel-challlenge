package com.fran.hotel.persistence.adapter;

import com.fran.hotel.domain.model.Reservation;
import com.fran.hotel.domain.port.ReservationPaymentPort;
import com.fran.hotel.domain.port.ReservationPersistencePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitMQReservationPaymentAdapter implements ReservationPaymentPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQReservationPaymentAdapter.class);
    private static final String EXCHANGE_NAME = "reservation-payment-exchange";
    private static final String ROUTING_KEY = "reservation.payment.start";
    private static final String CANCEL_ROUTING_KEY = "reservation.payment.cancel";

    private final RabbitTemplate rabbitTemplate;
    private final ReservationPersistencePort persistencePort;

    public RabbitMQReservationPaymentAdapter(RabbitTemplate rabbitTemplate,
                                             ReservationPersistencePort persistencePort) {
        this.rabbitTemplate = rabbitTemplate;
        this.persistencePort = persistencePort;
    }

    @Override
    public Reservation executeReservationPayment(Reservation reservation) {
        log.info("Starting reservation payment via RabbitMQ '{}'", reservation.id());
        
        Reservation runningReservation = reservation.pending();
        persistencePort.save(runningReservation);

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, runningReservation);

        return reservation;
    }

    @Override
    public void cancelReservationPayment(String reservationId) {
        log.info("Publishing payment cancel message for reservation '{}'", reservationId);
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, CANCEL_ROUTING_KEY, reservationId);
    }
}
