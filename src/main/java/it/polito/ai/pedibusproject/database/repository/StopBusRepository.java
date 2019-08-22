package it.polito.ai.pedibusproject.database.repository;

import it.polito.ai.pedibusproject.database.model.StopBus;
import it.polito.ai.pedibusproject.database.model.StopBusType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Set;

public interface StopBusRepository extends MongoRepository<StopBus,String> {
    Set<StopBus> findAllByStopBusType(StopBusType stopBusType);
}
