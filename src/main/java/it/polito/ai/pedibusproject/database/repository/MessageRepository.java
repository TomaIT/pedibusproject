package it.polito.ai.pedibusproject.database.repository;

import it.polito.ai.pedibusproject.database.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Set;

public interface MessageRepository extends MongoRepository<Message,String> {
    Set<Message> findAllByIdUserTo(String idUserTo);
}
