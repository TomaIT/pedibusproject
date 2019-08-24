package it.polito.ai.pedibusproject.service.interfaces;

import it.polito.ai.pedibusproject.database.model.Reservation;
import it.polito.ai.pedibusproject.database.model.ReservationState;

import java.util.Set;

public interface ReservationService {
    Set<Reservation> findAllByIdBusRide(String idBusRide);
    Set<Reservation> findAllByIdChild(String idChild);
    Set<Reservation> findAllByIdUser(String idUser);
    Set<Reservation> findAllByIdBusRideAndIdStopBus(String idBusRide,String idStopBus);
    Reservation findById(String id);
    Reservation create(String idBusRide,String idChild,String idStopBus,String idUser);

    Reservation updateGetIn(String id,ReservationState reservationState);

    //TODO controllo che getIn sia già stato settato
    Reservation updateGetOut(String id,ReservationState reservationState);

    //Non è chiaro a cosa serva, da spiegare bene :D
    Reservation updateAbsent(String id,ReservationState reservationState);

    void deleteById(String id);
}
