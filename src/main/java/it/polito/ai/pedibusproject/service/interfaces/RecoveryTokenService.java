package it.polito.ai.pedibusproject.service.interfaces;

import it.polito.ai.pedibusproject.database.model.RecoveryToken;

import java.util.Optional;
import java.util.UUID;

public interface RecoveryTokenService {
    //Create token and sendEmail
    RecoveryToken create(String email);

    RecoveryToken findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    Optional<RecoveryToken> findByEmail(String email);

    //If isExpired delete the confirmationToken
    boolean isExpired(RecoveryToken recoveryToken);
}
