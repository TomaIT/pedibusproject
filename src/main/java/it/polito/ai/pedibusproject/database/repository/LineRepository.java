package it.polito.ai.pedibusproject.database.repository;

import it.polito.ai.pedibusproject.database.model.Line;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.Set;

public interface LineRepository extends MongoRepository<Line,String> {
    Set<Line> findByIsDeleted(Boolean isDeleted);
    Set<Line> findByName(String name);
    Optional<Line> findByNameAndIsDeleted(String name,Boolean isDeleted);
    Optional<Line> findByIdOutStopBusesContains(String idStopBus);
    Optional<Line> findByIdRetStopBusesContains(String idStopBus);
}
