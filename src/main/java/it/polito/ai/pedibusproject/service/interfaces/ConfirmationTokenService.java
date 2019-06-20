package it.polito.ai.pedibusproject.service.interfaces;

import it.polito.ai.pedibusproject.database.model.ConfirmationToken;
import it.polito.ai.pedibusproject.database.model.User;

import java.util.UUID;

public interface ConfirmationTokenService {

    ConfirmationToken create(User user);

    ConfirmationToken findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    boolean existsByUuidAndUser_Username(UUID uuid,String username);

    //If isExpired delete the confirmationToken
    boolean isExpired(ConfirmationToken confirmationToken);
}
