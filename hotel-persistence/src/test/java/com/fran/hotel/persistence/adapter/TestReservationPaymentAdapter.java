package com.fran.hotel.persistence.adapter;

import com.fran.hotel.domain.model.Reservation;
import com.fran.hotel.domain.model.ReservationStatus;
import com.fran.hotel.domain.port.ReservationPaymentPort;
import com.fran.hotel.domain.port.ReservationPersistencePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TestReservationPaymentAdapter implements ReservationPaymentPort {

    private static final Logger log = LoggerFactory.getLogger(TestReservationPaymentAdapter.class);
    private static final long PAYMENT_DELAY_SECONDS = 60;

    private final ReservationPersistencePort persistencePort;
    private final TaskScheduler taskScheduler;

    public TestReservationPaymentAdapter(ReservationPersistencePort persistencePort,
                                         TaskScheduler taskScheduler) {
        this.persistencePort = persistencePort;
        this.taskScheduler = taskScheduler;
    }

    @Override
    public Reservation executeReservationPayment(Reservation reservation) {
        log.info("Starting payment process (test) for reservation '{}' - will confirm after {} seconds",
                 reservation.id(), PAYMENT_DELAY_SECONDS);

        Reservation runningReservation = reservation.pending();
        persistencePort.save(runningReservation);

        Instant confirmationTime = Instant.now().plus(PAYMENT_DELAY_SECONDS, ChronoUnit.SECONDS);
        taskScheduler.schedule(() -> confirmPayment(reservation), confirmationTime);

        return reservation;
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
    }
}
