package it.polito.ai.pedibusproject.service.interfaces;

import it.polito.ai.pedibusproject.database.model.Reservation;
import it.polito.ai.pedibusproject.database.model.ReservationState;

import java.util.Set;

public interface ReservationService {
    Set<Reservation> findAllByIdBusRide(String idBusRide);
    Set<Reservation> findAllByIdChild(String idChild);
    Set<Reservation> findAllByIdUser(String idUser);
    Set<Reservation> findAllByIdBusRideAndIdStopBus(String idBusRide,String idStopBus);
    Set<Reservation> findAllByIdBusRideAndIdChild(String idBusRide, String idUser);
    Reservation findById(String id);
    Reservation create(String idBusRide,String idChild,String idStopBus,String idUser);
    Reservation create(Reservation reservation);

    Reservation updateGetIn(String id,ReservationState reservationState);

    //Only Outward
    Reservation updateGetIn(String id,String idBusRide,String idStopBus,
                            String idUser,ReservationState reservationState);

    Reservation updateGetOut(String id,ReservationState reservationState);

    //Non è chiaro a cosa serva, da spiegare bene :D
    // Ora va a settare il campo absent, per poter distingue un child il cui stato non è ancora stato settato da uno assente nel frontend
    Reservation updateAbsent(String id,ReservationState reservationState);

    void deleteById(String id);
}
