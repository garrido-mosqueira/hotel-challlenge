package com.fran.hotel.persistence.adapter;

import com.fran.hotel.domain.model.Reservation;
import com.fran.hotel.domain.model.ReservationPayment;
import com.fran.hotel.domain.port.ReservationPaymentPort;
import com.fran.hotel.domain.port.ReservationPersistencePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Deprecated
public class RedisReservationPaymentSimulation implements ReservationPaymentPort {

    private static final Logger log = LoggerFactory.getLogger(RedisReservationPaymentSimulation.class);
    private static final String PROGRESS_REGISTER_PREFIX = "reservation:reservationpayment:";
    private static final int MAX_PROGRESS = 60;

    private final RedisTemplate<String, ReservationPayment> progressRegister;
    private final ReservationPersistencePort persistencePort;

    public RedisReservationPaymentSimulation(RedisTemplate<String, ReservationPayment> progressRegister,
                                             ReservationPersistencePort persistencePort) {
        this.progressRegister = progressRegister;
        this.persistencePort = persistencePort;
    }

    @Override
    public Reservation executeReservationPayment(Reservation reservation) {
        String paymentKey = PROGRESS_REGISTER_PREFIX + reservation.id();

        if (getReservationPaymentByKey(paymentKey) != null) {
            log.info("Reservation payment '{}' is in process", reservation.id());
            return reservation;
        }

        log.info("Starting reservation payment '{}'", reservation.id());
        Thread.ofVirtual().name(reservation.id()).start(() -> simulatePaymentProgress(reservation));

        return reservation;
    }

    @Override
    public void cancelReservationPayment(String reservationId) {
        String paymentKey = PROGRESS_REGISTER_PREFIX + reservationId;
        ReservationPayment reservationPayment = getReservationPaymentByKey(paymentKey);

        if (reservationPayment != null && reservationPayment.reservation() != null && !reservationPayment.isCancelled()) {
            log.info("Cancel reservation payment '{}'", reservationId);
            progressRegister.opsForValue().set(paymentKey, new ReservationPayment(reservationPayment.reservation(), true, reservationPayment.progress()));
        } else {
            log.info("Reservation payment '{}' is already cancelled", reservationId);
        }
    }

    private void simulatePaymentProgress(Reservation reservation) {
        String paymentKey = PROGRESS_REGISTER_PREFIX + reservation.id();
        Reservation runningReservation = reservation.pending();

        updateReservationStatus(paymentKey, new ReservationPayment(runningReservation, false, 0));
        persistencePort.save(runningReservation);

        int progress = 0;
        ReservationPayment currentPayment = null;
        do {
            ReservationPayment updatedReservation = new ReservationPayment(runningReservation, false, progress);
            updateReservationStatus(paymentKey, updatedReservation);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            progress++;
            currentPayment = getReservationPaymentByKey(paymentKey);
        } while (progress <= MAX_PROGRESS && currentPayment != null && !currentPayment.isCancelled());

        finalizePaymentProgress(runningReservation, progress, currentPayment);
        progressRegister.delete(paymentKey);
        log.info("End payment reservation process for '{}'", runningReservation.id());
    }

    private void updateReservationStatus(String paymentKey, ReservationPayment reservationPayment) {
        progressRegister.opsForValue().set(paymentKey, reservationPayment);
    }

    private void finalizePaymentProgress(Reservation reservation, int finalProgress, ReservationPayment currentPayment) {
        if (currentPayment != null && currentPayment.isCancelled()) {
            Reservation cancelledReservation = reservation.cancel();
            persistencePort.save(cancelledReservation);
        } else if (finalProgress > MAX_PROGRESS) {
            Reservation confirmedReservation = reservation.confirm();
            persistencePort.save(confirmedReservation);
        }
    }


    private ReservationPayment getReservationPaymentByKey(String key) {
        return progressRegister.opsForValue().get(key);
    }
}
