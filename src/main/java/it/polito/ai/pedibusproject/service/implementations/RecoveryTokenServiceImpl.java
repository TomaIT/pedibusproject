package it.polito.ai.pedibusproject.service.implementations;

import it.polito.ai.pedibusproject.controller.model.ConfirmUserView;
import it.polito.ai.pedibusproject.controller.model.RecoveryUserView;
import it.polito.ai.pedibusproject.controller.view.ConfirmRegistrationController;
import it.polito.ai.pedibusproject.controller.view.RecoveryController;
import it.polito.ai.pedibusproject.database.model.ConfirmationToken;
import it.polito.ai.pedibusproject.database.model.RecoveryToken;
import it.polito.ai.pedibusproject.database.repository.RecoveryTokenRepository;
import it.polito.ai.pedibusproject.exceptions.InternalServerErrorException;
import it.polito.ai.pedibusproject.exceptions.NotFoundException;
import it.polito.ai.pedibusproject.service.interfaces.RecoveryTokenService;
import it.polito.ai.pedibusproject.service.interfaces.UserService;
import it.polito.ai.pedibusproject.utility.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Service
public class RecoveryTokenServiceImpl implements RecoveryTokenService {
    private static final Logger LOG = LoggerFactory.getLogger(ConfirmationTokenServiceImpl.class);
    private RecoveryTokenRepository recoveryTokenRepository;
    private EmailSender emailSender;
    private UserService userService;
    @Value("${uuid.recoverytoken.validitytime.seconds}")
    private long expiredTimeout;
    @Value("${pedibus.uri}")
    private String uriContext;

    @Autowired
    public RecoveryTokenServiceImpl(RecoveryTokenRepository recoveryTokenRepository,
                                    EmailSender emailSender,UserService userService){
        this.recoveryTokenRepository=recoveryTokenRepository;
        this.emailSender=emailSender;
        this.userService=userService;
    }

    private String getLinkConfirmRegistration(UUID uuid){
        String link;
        try {
            link= linkTo(RecoveryController.class,
                    RecoveryController.class.getMethod("getRecoveryView",
                            Model.class, RecoveryUserView.class, UUID.class),
                    uuid)
                    .toUriComponentsBuilder()
                    .build().toUriString();

            //TODO non funziona se la richiesta non arriva da un Context Uri
            // (quindi da http)
            //Risoluzione parziale (fa schifo)
            if(link.toCharArray()[0]=='/'){ link=this.uriContext+link; }

        }catch (Exception e){
            LOG.error("Creation Link Recovery Registration",e);
            throw new InternalServerErrorException("Create Link to Recovery Registration");
        }
        return link;
    }

    @Override
    public RecoveryToken create(String email) {
        this.userService.loadUserByUsername(email);
        RecoveryToken temp=this.recoveryTokenRepository.insert(new RecoveryToken(email));
        String link=getLinkConfirmRegistration(temp.getUuid());
        LOG.info(link);
        this.emailSender.sendEmail("Recovery Registration",
                "Please click here: "+link,
                email);
        return temp;
    }

    @Override
    public RecoveryToken findByUuid(UUID uuid) {
        return this.recoveryTokenRepository.findByUuid(uuid).orElseThrow(()->new NotFoundException("RecoveryToken <findByUuid>"));
    }

    @Override
    public void deleteByUuid(UUID uuid) {
        this.recoveryTokenRepository.deleteByUuid(uuid);
    }

    @Override
    public Optional<RecoveryToken> findByEmail(String email) {
        return this.recoveryTokenRepository.findByEmail(email);
    }

    @Override
    public boolean isExpired(RecoveryToken recoveryToken) {
        long diff=System.currentTimeMillis()-recoveryToken.getCreationTime();
        if((diff / 1000)>expiredTimeout){
            this.recoveryTokenRepository.deleteById(recoveryToken.getId());
            return true;
        }
        return false;
    }
}
