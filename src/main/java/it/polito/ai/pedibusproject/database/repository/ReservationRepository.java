package it.polito.ai.pedibusproject.database.repository;

import it.polito.ai.pedibusproject.database.model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReservationRepository extends MongoRepository<Reservation,String> {
}
