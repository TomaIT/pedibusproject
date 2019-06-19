package it.polito.ai.pedibusproject.database.repository;

import it.polito.ai.pedibusproject.database.model.StopBus;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StopBusRepository extends MongoRepository<StopBus,String> {
}
