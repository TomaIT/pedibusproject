package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.get.*;
import it.polito.ai.pedibusproject.controller.model.post.UserPOST;
import it.polito.ai.pedibusproject.controller.model.put.UserPUT;
import it.polito.ai.pedibusproject.controller.model.put.UserRolePUT;
import it.polito.ai.pedibusproject.database.model.Role;
import it.polito.ai.pedibusproject.database.model.User;
import it.polito.ai.pedibusproject.exceptions.BadRequestException;
import it.polito.ai.pedibusproject.exceptions.NotImplementedException;
import it.polito.ai.pedibusproject.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/rest/users")
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    private UserService userService;
    private ConfirmationTokenService confirmationTokenService;
    private ChildService childService;
    private ReservationService reservationService;
    private AvailabilityService availabilityService;
    private MessageService messageService;

    @Autowired
    public UserController(UserService userService,
                          ConfirmationTokenService confirmationTokenService,
                          ChildService childService,
                          ReservationService reservationService,
                          AvailabilityService availabilityService,
                          MessageService messageService) {
        this.userService=userService;
        this.confirmationTokenService=confirmationTokenService;
        this.childService=childService;
        this.reservationService=reservationService;
        this.availabilityService=availabilityService;
        this.messageService=messageService;
    }


    @PostMapping(value = "",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Crea Nuovo Utente (invalido finchè non conferma la registrazione)")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 409, message = "Conflict"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public UserGET postUser(@RequestHeader (name="Authorization") String jwtToken,
                            @RequestBody @Valid UserPOST userPOST) {
        User temp= this.userService.create(userPOST.getEmail(),userPOST.getRoles());
        this.confirmationTokenService.create(temp.getUsername());
        return new UserGET(temp);
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
    public UserGET refreshUuid(@RequestHeader (name="Authorization") String jwtToken,
                            @PathVariable("idUser")String idUser) {
        User temp=this.userService.loadUserByUsername(idUser);
        if(temp.isEnabled()) throw new BadRequestException("User <refreshUuid> user is already active.");
        this.confirmationTokenService.create(temp.getUsername());
        return new UserGET(temp);
    }

    @GetMapping(value = "/{idUser}",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna l'utente idUser con password oscurata")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public UserGET getUserById(@RequestHeader (name="Authorization") String jwtToken,
                            @PathVariable("idUser")String idUser) {
        return new UserGET(this.userService.loadUserByUsername(idUser));
    }

    @PutMapping(value = "/{idUser}/role",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Aggiunge ruolo all'utente idUser. Nota che i ruoli validi sono tutti tranne ROLE_ADMIN. " +
            "Per gestire tale ruolo affidarsi ai metodi addLine e removeLine i quali assegnano il ruolo di ROLE_ADMIN in automatico.")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public UserGET putUserRoleById(@RequestHeader (name="Authorization") String jwtToken,
                                   @PathVariable("idUser")String idUser,
                                   @RequestParam @Valid Role role) {
        return new UserGET(userService.addRole(idUser,role));
    }

    @PutMapping(value = "/{idUser}/addLine",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Aggiunge linea all'utente idUser, nel caso non lo fosse già assegna ruolo di ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public UserGET putUserAddLine(@RequestHeader (name="Authorization") String jwtToken,
                                   @PathVariable("idUser")String idUser,
                                   @RequestParam @Valid String idLine) {
        return new UserGET(userService.addLine(idUser,idLine));
    }

    @PutMapping(value = "/{idUser}/removeLine",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rimuove linea all'utente idUser, nota che il ruolo di ROLE_ADMIN persisterà anche se non ha piu linee")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public UserGET putUserRemoveLine(@RequestHeader (name="Authorization") String jwtToken,
                                  @PathVariable("idUser")String idUser,
                                  @RequestParam @Valid String idLine) {
        return new UserGET(userService.removeLine(idUser,idLine));
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
    public UserGET putUserById(@RequestHeader (name="Authorization") String jwtToken,
                            @PathVariable("idUser")String idUser,
                            @RequestBody @Valid UserPUT userPUT) {
        if(!userPUT.getPassword().equals(userPUT.getVerifyPassword()))
            throw new BadRequestException("Update User password mismatch");
        return new UserGET(
                userService.updateUser(idUser,userPUT.getPassword(),userPUT.getFirstname(),
                userPUT.getSurname(),userPUT.getBirth(),userPUT.getStreet(),userPUT.getPhoneNumber()));
    }

    @GetMapping(value = "/{idUser}/children",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna i bambini dell'utente idUser")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<ChildGET> getChildrenById(@RequestHeader (name="Authorization") String jwtToken,
                                         @PathVariable("idUser")String idUser) {
        return this.childService.findByIdUser(idUser).stream()
                .map(ChildGET::new).collect(Collectors.toSet());
    }

    @GetMapping(value = "/{idUser}/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tutte le prenotazioni effetuate da idUser")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found User"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<ReservationGET> getReservationByUser(@RequestHeader (name="Authorization") String jwtToken,
                                                    @PathVariable("idUser")String idUser) {
        return this.reservationService.findAllByIdUser(idUser).stream()
                .map(ReservationGET::new).collect(Collectors.toSet());
    }

    @GetMapping(value = "/{idUser}/availabilities", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tutte le availability effetuate da idUser")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found User"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<AvailabilityGET> getAvailabilitiesByUser(@RequestHeader (name="Authorization") String jwtToken,
                                                        @PathVariable("idUser")String idUser) {
        return this.availabilityService.findAllByIdUser(idUser).stream()
                .map(AvailabilityGET::new).collect(Collectors.toSet());
    }

    @GetMapping(value = "/{idUser}/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tutti i messagi ricevuti per (idUser)")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found User"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<MessageGET> getReceivedMessages(@RequestHeader (name="Authorization") String jwtToken,
                                                   @PathVariable("idUser")String idUser) {
        return this.messageService.findAllByIdUserTo(idUser).stream()
                .map(MessageGET::new).collect(Collectors.toSet());
    }

    @GetMapping(value = "/{idUser}/messages/notReadCounter", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna il conteggio dei messaggi ricevuti non ancora letti")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found User"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Long getReceivedMessagesNotReadCounter(@RequestHeader (name="Authorization") String jwtToken,
                                               @PathVariable("idUser")String idUser) {
        return this.messageService.findAllByIdUserTo(idUser).stream()
                .filter(x->x.getReadConfirm()==null).count();
    }




}
