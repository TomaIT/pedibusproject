package it.polito.ai.pedibusproject.service.implementations;

import it.polito.ai.pedibusproject.database.model.ConfirmationToken;
import it.polito.ai.pedibusproject.database.model.User;
import it.polito.ai.pedibusproject.database.repository.ConfirmationTokenRepository;
import it.polito.ai.pedibusproject.exceptions.NotFoundException;
import it.polito.ai.pedibusproject.service.interfaces.ConfirmationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Value("${uuid.token.validitytime.seconds}")
    private long expiredTimeout;

    @Autowired
    public ConfirmationTokenServiceImpl(ConfirmationTokenRepository confirmationTokenRepository){
        this.confirmationTokenRepository=confirmationTokenRepository;
    }

    @Override
    public ConfirmationToken create(User user) {
        return this.confirmationTokenRepository.insert(new ConfirmationToken(user));
    }

    @Override
    public ConfirmationToken findByUuid(UUID uuid) {
        return this.confirmationTokenRepository.findByUuid(uuid)
                .orElseThrow(()->new NotFoundException("ConfirmationToken"));
    }

    @Override
    public void deleteByUuid(UUID uuid) {
        this.confirmationTokenRepository.deleteByUuid(uuid);
    }

    @Override
    public boolean existsByUuidAndUser_Username(UUID uuid, String username) {
        return this.confirmationTokenRepository.existsByUuidAndUser_Username(uuid,username);
    }

    @Override
    public boolean isExpired(ConfirmationToken confirmationToken) {
        long diff=System.currentTimeMillis()-confirmationToken.getCreationTime();
        if((diff / 1000)>expiredTimeout){
            this.confirmationTokenRepository.deleteById(confirmationToken.getId());
            return true;
        }
        return false;
    }
}
