package it.polito.ai.pedibusproject.service.interfaces;

import it.polito.ai.pedibusproject.database.model.Reservation;

import java.util.Set;

public interface ReservationService {
    Set<Reservation> findAllByIdBusRide(String idBusRide);
    void deleteById(String id);
}
