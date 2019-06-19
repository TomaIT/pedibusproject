package it.polito.ai.pedibusproject.database.repository;

import it.polito.ai.pedibusproject.database.model.Availability;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AvailabilityRepository extends MongoRepository<Availability,String> {
}
