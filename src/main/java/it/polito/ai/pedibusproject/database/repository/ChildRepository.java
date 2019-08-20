package it.polito.ai.pedibusproject.database.repository;

import it.polito.ai.pedibusproject.database.model.Child;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Set;

public interface ChildRepository extends MongoRepository<Child,String> {
    Set<Child> findByIdUser(String idUser);
    Set<Child> findAllByIdStopBusOutDef(String idStopBusOutDef);
    Set<Child> findAllByIdStopBusRetDef(String idStopBusRetDef);
}
