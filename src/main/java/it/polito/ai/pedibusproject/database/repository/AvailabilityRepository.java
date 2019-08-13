package it.polito.ai.pedibusproject.database.repository;

import it.polito.ai.pedibusproject.database.model.Availability;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Set;

public interface AvailabilityRepository extends MongoRepository<Availability,String> {
    Set<Availability> findAllByIdBusRide(String idBusRide);
    Set<Availability> findAllByIdUser(String idUser);
}
