package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.PedibusprojectApplication;
import it.polito.ai.pedibusproject.controller.ConfirmationController;
import it.polito.ai.pedibusproject.controller.model.ConfirmUserView;
import it.polito.ai.pedibusproject.controller.model.UserPOST;
import it.polito.ai.pedibusproject.database.model.ConfirmationToken;
import it.polito.ai.pedibusproject.database.model.StopBus;
import it.polito.ai.pedibusproject.database.model.User;
import it.polito.ai.pedibusproject.exceptions.BadRequestException;
import it.polito.ai.pedibusproject.exceptions.InternalServerErrorException;
import it.polito.ai.pedibusproject.service.interfaces.ConfirmationTokenService;
import it.polito.ai.pedibusproject.service.interfaces.UserService;
import it.polito.ai.pedibusproject.utility.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/rest/users")
public class UserController {
    private UserService userService;
    private ConfirmationTokenService confirmationTokenService;
    private EmailSender emailSender;
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService,EmailSender emailSender,
                          ConfirmationTokenService confirmationTokenService){
        this.userService=userService;
        this.confirmationTokenService=confirmationTokenService;
        this.emailSender=emailSender;
    }

    private String getLinkConfirm(UUID uuid){
        try {
            String link;
            link= linkTo(ConfirmationController.class,
                    ConfirmationController.class.getMethod("getConfirmView",
                            Model.class, ConfirmUserView.class, UUID.class),
                    uuid)
                    .toUriComponentsBuilder()
                    .build().toUriString();
            return link;
        }catch (Exception e){
            LOG.error("Creation Link Confirm Registration");
            throw new InternalServerErrorException("Creation Link Confirm Registration");
        }
    }

    //Return user with password obscured
    private User createTokenAndSendEmail(User user){
        ConfirmationToken ctemp=this.confirmationTokenService.create(user);
        String link=getLinkConfirm(ctemp.getUuid());
        LOG.info(link);
        this.emailSender.sendEmail("Confirm Registration",
                "Please complete your registration: "+link,
                user.getUsername());
        user.setPassword("***");
        return user;
    }

    @PostMapping(value = "/",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Crea Nuovo Utente (invalido finchè non conferma la registrazione)")
    @ApiResponses(value = {
            @ApiResponse(code = 409, message = "Conflict"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public User postUser(@RequestBody @Valid UserPOST userPOST) {
        User temp= this.userService.create(userPOST.getEmail(),userPOST.getRoles());
        return createTokenAndSendEmail(temp);
    }


    @PostMapping(value = "/{idUser}/uuid",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Crea nuovo uuid per la registrazione e invia l'email, " +
            "se l'utente è già/ancora attivo -> BadRequest")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public User refreshUuid(@PathVariable("idUser")String idUser) {
        User temp=this.userService.loadUserByUsername(idUser);
        if(temp.isEnabled()) throw new BadRequestException("User <refreshUuid> user is already active.");
        return createTokenAndSendEmail(temp);
    }
}
