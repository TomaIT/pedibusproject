package it.polito.ai.pedibusproject.database.repository;

import it.polito.ai.pedibusproject.database.model.RecoveryToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecoveryTokenRepository extends MongoRepository<RecoveryToken,String> {
}
