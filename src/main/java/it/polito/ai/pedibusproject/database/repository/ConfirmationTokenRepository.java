package it.polito.ai.pedibusproject.database.repository;

import it.polito.ai.pedibusproject.database.model.ConfirmationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConfirmationTokenRepository extends MongoRepository<ConfirmationToken,String> {
    Optional<ConfirmationToken> findByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);
    boolean existsByUuidAndUser_Username(UUID uuid,String username);
}
