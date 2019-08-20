package it.polito.ai.pedibusproject.service.interfaces;

import it.polito.ai.pedibusproject.database.model.ConfirmationToken;

import java.util.Optional;
import java.util.UUID;

public interface ConfirmationTokenService {

    //Create token and sendEmail
    ConfirmationToken create(String email);

    ConfirmationToken findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    Optional<ConfirmationToken> findByEmail(String email);
    
    boolean isExpired(ConfirmationToken confirmationToken);
}
