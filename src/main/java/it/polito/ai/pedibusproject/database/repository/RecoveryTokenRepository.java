package it.polito.ai.pedibusproject.database.repository;

import it.polito.ai.pedibusproject.database.model.RecoveryToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface RecoveryTokenRepository extends MongoRepository<RecoveryToken,String> {
    Optional<RecoveryToken> findByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);
    Optional<RecoveryToken> findByEmail(String email);
}
