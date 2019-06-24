package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.post.LoginPOST;
import it.polito.ai.pedibusproject.controller.model.post.RecoverPOST;
import it.polito.ai.pedibusproject.controller.model.post.UserPOST;
import it.polito.ai.pedibusproject.controller.model.put.UserPUT;
import it.polito.ai.pedibusproject.controller.model.put.UserRolePUT;
import it.polito.ai.pedibusproject.database.model.*;
import it.polito.ai.pedibusproject.exceptions.BadRequestException;
import it.polito.ai.pedibusproject.exceptions.NotImplementedException;
import it.polito.ai.pedibusproject.service.interfaces.ConfirmationTokenService;
import it.polito.ai.pedibusproject.service.interfaces.RecoveryTokenService;
import it.polito.ai.pedibusproject.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/rest/users")
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    private UserService userService;
    private ConfirmationTokenService confirmationTokenService;
    private RecoveryTokenService recoveryTokenService;

    @Autowired
    public UserController(UserService userService,
                          ConfirmationTokenService confirmationTokenService,
                          RecoveryTokenService recoveryTokenService){
        this.userService=userService;
        this.confirmationTokenService=confirmationTokenService;
        this.recoveryTokenService=recoveryTokenService;
    }

    private static User obscure(User user){
        user.setPassword("***");
        return user;
    }


    @PostMapping(value = "",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Crea Nuovo Utente (invalido finchè non conferma la registrazione)")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 409, message = "Conflict"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public User postUser(@RequestHeader (name="Authorization") String jwtToken,
                         @RequestBody @Valid UserPOST userPOST) {
        User temp= this.userService.create(userPOST.getEmail(),userPOST.getRoles());
        this.confirmationTokenService.create(temp.getUsername());
        return obscure(temp);
    }


    @PostMapping(value = "/{idUser}/uuid",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Crea nuovo uuid per la registrazione e invia l'email, " +
            "se l'utente è già/ancora attivo -> BadRequest")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public User refreshUuid(@RequestHeader (name="Authorization") String jwtToken,
                            @PathVariable("idUser")String idUser) {
        User temp=this.userService.loadUserByUsername(idUser);
        if(temp.isEnabled()) throw new BadRequestException("User <refreshUuid> user is already active.");
        this.confirmationTokenService.create(temp.getUsername());
        return obscure(temp);
    }

    @GetMapping(value = "/{idUser}",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna l'utente idUser con password oscurata")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public User getUserById(@RequestHeader (name="Authorization") String jwtToken,
                            @PathVariable("idUser")String idUser) {
        User temp=this.userService.loadUserByUsername(idUser);
        return obscure(temp);
    }

    @PutMapping(value = "/{idUser}/role",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Modifica ruolo Utente idUser")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public User putUserRoleById(@RequestHeader (name="Authorization") String jwtToken,
                                @PathVariable("idUser")String idUser,
                                @RequestBody @Valid UserRolePUT userRolePUT) {
        //TODO
        throw new NotImplementedException();
    }

    @PutMapping(value = "/{idUser}",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Modifica Parametri User idUser")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public User putUserById(@RequestHeader (name="Authorization") String jwtToken,
                            @PathVariable("idUser")String idUser,
                            @RequestBody @Valid UserPUT userPUT) {
        //TODO
        throw new NotImplementedException();
    }

    @GetMapping(value = "/{idUser}/children",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna i bambini dell'utente idUser")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<Child> getChildrenById(@RequestHeader (name="Authorization") String jwtToken,
                                      @PathVariable("idUser")String idUser) {
        //TODO
        throw new NotImplementedException();
    }

    @GetMapping(value = "/{idUser}/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tutte le prenotazioni effetuate da idUser")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found User"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<Reservation> getReservationByUser(@RequestHeader (name="Authorization") String jwtToken,
                                                 @PathVariable("idUser")String idUser) {
        //TODO
        throw new NotImplementedException();
    }

    @GetMapping(value = "/{idUser}/availabilities", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tutte le availability effetuate da idUser")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found User"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<Availability> getAvailabilitiesByUser(@RequestHeader (name="Authorization") String jwtToken,
                                                     @PathVariable("idUser")String idUser) {
        //TODO
        throw new NotImplementedException();
    }


}
