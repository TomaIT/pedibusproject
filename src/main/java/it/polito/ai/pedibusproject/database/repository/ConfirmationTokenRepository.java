package it.polito.ai.pedibusproject.database.repository;

import it.polito.ai.pedibusproject.database.model.ConfirmationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfirmationTokenRepository extends MongoRepository<ConfirmationToken,String> {
}
