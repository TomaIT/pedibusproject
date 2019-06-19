package it.polito.ai.pedibusproject.database.repository;

import it.polito.ai.pedibusproject.database.model.BusRide;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BusRideRepository extends MongoRepository<BusRide,String> {
}
