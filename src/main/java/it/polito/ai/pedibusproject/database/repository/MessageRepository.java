package it.polito.ai.pedibusproject.database.repository;

import it.polito.ai.pedibusproject.database.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message,String> {
}
