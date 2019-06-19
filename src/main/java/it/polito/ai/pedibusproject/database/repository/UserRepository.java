package it.polito.ai.pedibusproject.database.repository;

import it.polito.ai.pedibusproject.database.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

}
