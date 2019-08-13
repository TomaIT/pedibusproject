package it.polito.ai.pedibusproject.service.interfaces;

import it.polito.ai.pedibusproject.database.model.Reservation;
import it.polito.ai.pedibusproject.database.model.ReservationState;

import java.util.Set;

public interface ReservationService {
    Set<Reservation> findAllByIdBusRide(String idBusRide);
    Set<Reservation> findAllByIdChild(String idChild);
    Set<Reservation> findAllByIdUser(String idUser);
    Reservation findById(String id);
    Reservation create(String idBusRide,String idChild,String idStopBus,String idUser);
    Reservation updateGetIn(String id,ReservationState reservationState);
    Reservation updateGetOut(String id,ReservationState reservationState);
    void deleteById(String id);
}
