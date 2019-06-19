package it.polito.ai.pedibusproject.database.repository;

import it.polito.ai.pedibusproject.database.model.Child;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChildRepository extends MongoRepository<Child,String> {
}
