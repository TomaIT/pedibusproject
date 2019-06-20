package it.polito.ai.pedibusproject.service.implementations;

import it.polito.ai.pedibusproject.controller.model.ConfirmUserView;
import it.polito.ai.pedibusproject.controller.view.ConfirmRegController;
import it.polito.ai.pedibusproject.database.model.ConfirmationToken;
import it.polito.ai.pedibusproject.database.repository.ConfirmationTokenRepository;
import it.polito.ai.pedibusproject.exceptions.InternalServerErrorException;
import it.polito.ai.pedibusproject.exceptions.NotFoundException;
import it.polito.ai.pedibusproject.service.interfaces.ConfirmationTokenService;
import it.polito.ai.pedibusproject.utility.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {
    private static final Logger LOG = LoggerFactory.getLogger(ConfirmationTokenServiceImpl.class);
    private ConfirmationTokenRepository confirmationTokenRepository;
    private EmailSender emailSender;
    @Value("${uuid.token.validitytime.seconds}")
    private long expiredTimeout;

    @Autowired
    public ConfirmationTokenServiceImpl(ConfirmationTokenRepository confirmationTokenRepository,
                                        EmailSender emailSender){
        this.confirmationTokenRepository=confirmationTokenRepository;
        this.emailSender=emailSender;
    }

    private String getLinkConfirmRegistration(UUID uuid){
        String link;
        try {
            //TODO non funziona se la richiesta non arriva da un Context Uri
            // (quindi da http)
            link= linkTo(ConfirmRegController.class,
                    ConfirmRegController.class.getMethod("getConfirmView",
                            Model.class, ConfirmUserView.class, UUID.class),
                    uuid)
                    .toUriComponentsBuilder()
                    .build().toUriString();
        }catch (Exception e){
            LOG.error("Creation Link Confirm Registration",e);
            throw new InternalServerErrorException("Create Link to Confirm Registration");
        }
        return link;
    }

    @Override
    public ConfirmationToken create(String email) {
        ConfirmationToken temp=this.confirmationTokenRepository.insert(new ConfirmationToken(email));
        String link=getLinkConfirmRegistration(temp.getUuid());
        LOG.info(link);
        this.emailSender.sendEmail("Confirm Registration",
                "Please complete your registration: "+link,
                email);
        return temp;
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
    public Optional<ConfirmationToken> findByEmail(String email) {
        return this.confirmationTokenRepository.findByEmail(email);
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
