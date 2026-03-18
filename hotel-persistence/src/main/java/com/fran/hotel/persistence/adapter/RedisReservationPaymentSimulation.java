package com.fran.hotel.persistence.adapter;

import com.fran.hotel.domain.model.Reservation;
import com.fran.hotel.domain.model.ReservationStatus;
import com.fran.hotel.domain.model.ReservationTask;
import com.fran.hotel.domain.port.ReservationPersistencePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisReservationPaymentSimulation {

    private static final Logger log = LoggerFactory.getLogger(RedisReservationPaymentSimulation.class);
    private static final String PROGRESS_REGISTER_PREFIX = "reservation:task:";
    private static final int MAX_PROGRESS = 60;

    private final RedisTemplate<String, ReservationTask> progressRegister;
    private final ReservationPersistencePort persistencePort;

    public RedisReservationPaymentSimulation(@Qualifier("reservationTaskTemplate") RedisTemplate<String, ReservationTask> progressRegister,
                                             ReservationPersistencePort persistencePort) {
        this.progressRegister = progressRegister;
        this.persistencePort = persistencePort;
    }

    public Reservation executeTask(Reservation reservation) {
        String taskKey = PROGRESS_REGISTER_PREFIX + reservation.id();

        if (getTaskThread(taskKey) != null) {
            log.info("Reservation payment '{}' is in process", reservation.id());
            return reservation;
        }

        log.info("Starting reservation payment '{}'", reservation.id());
        Thread.ofVirtual()
                .name(reservation.id())
                .start(() -> runLoop(reservation));

        return reservation;
    }

    public void cancelTask(Long reservationId) {
        String taskKey = PROGRESS_REGISTER_PREFIX + reservationId;
        ReservationTask taskThread = getTaskThread(taskKey);

        if (taskThread != null && taskThread.reservation() != null && !taskThread.isCancelled()) {
            log.info("Cancel reservation payment '{}'", reservationId);
            progressRegister.opsForValue().set(taskKey, new ReservationTask(taskThread.reservation(), true, taskThread.progress()));
        } else {
            log.info("Reservation payment '{}' is already cancelled", reservationId);
        }
    }

    private void runLoop(Reservation reservation) {
        String taskKey = PROGRESS_REGISTER_PREFIX + reservation.id();
        Reservation runningReservation = reservation.withStatus(ReservationStatus.PENDING);

        updateTaskInRegister(taskKey, new ReservationTask(runningReservation, false, 0));
        persistencePort.save(runningReservation);

        int progress = 0;
        ReservationTask currentTask = null;

        do {
            ReservationTask updatedTask = new ReservationTask(runningReservation, false, progress);
            updateTaskInRegister(taskKey, updatedTask);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            progress++;
            currentTask = getTaskThread(taskKey);
        } while (progress <= MAX_PROGRESS && currentTask != null && !currentTask.isCancelled());

        finalizePaymentProgress(runningReservation, progress, currentTask);
        progressRegister.delete(taskKey);
        log.info("End counter for reservation task '{}'", runningReservation.id());
    }

    private void updateTaskInRegister(String taskKey, ReservationTask task) {
        progressRegister.opsForValue().set(taskKey, task);
    }

    private void finalizePaymentProgress(Reservation reservation, int finalProgress, ReservationTask currentTask) {
        if (currentTask != null && currentTask.isCancelled()) {
            Reservation cancelledReservation = reservation.withStatus(ReservationStatus.CANCELLED);
            persistencePort.save(cancelledReservation);
        } else if (finalProgress > MAX_PROGRESS) {
            Reservation confirmedReservation = reservation.withStatus(ReservationStatus.CONFIRMED);
            persistencePort.save(confirmedReservation);
        }
    }


    private ReservationTask getTaskThread(String key) {
        return progressRegister.opsForValue().get(key);
    }
}
