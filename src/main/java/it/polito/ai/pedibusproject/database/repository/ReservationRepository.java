package it.polito.ai.pedibusproject.database.repository;

import it.polito.ai.pedibusproject.database.model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Set;

public interface ReservationRepository extends MongoRepository<Reservation,String> {
    Set<Reservation> findAllByIdBusRide(String idBusRide);
    Set<Reservation> findAllByIdChild(String idChild);
    Set<Reservation> findAllByIdUser(String idUser);
}
