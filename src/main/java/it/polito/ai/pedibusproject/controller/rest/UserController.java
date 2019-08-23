package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.get.*;
import it.polito.ai.pedibusproject.controller.model.post.UserPOST;
import it.polito.ai.pedibusproject.controller.model.put.UserPUT;
import it.polito.ai.pedibusproject.database.model.Line;
import it.polito.ai.pedibusproject.database.model.Role;
import it.polito.ai.pedibusproject.database.model.User;
import it.polito.ai.pedibusproject.exceptions.BadRequestException;
import it.polito.ai.pedibusproject.exceptions.ForbiddenException;
import it.polito.ai.pedibusproject.security.JwtTokenProvider;
import it.polito.ai.pedibusproject.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
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
    private JwtTokenProvider jwtTokenProvider;
    private StopBusService stopBusService;
    private LineService lineService;

    @Autowired
    public UserController(UserService userService,
                          ConfirmationTokenService confirmationTokenService,
                          ChildService childService,
                          ReservationService reservationService,
                          AvailabilityService availabilityService,
                          MessageService messageService,
                          JwtTokenProvider jwtTokenProvider,
                          StopBusService stopBusService,
                          LineService lineService) {
        this.userService=userService;
        this.confirmationTokenService=confirmationTokenService;
        this.childService=childService;
        this.reservationService=reservationService;
        this.availabilityService=availabilityService;
        this.messageService=messageService;
        this.jwtTokenProvider=jwtTokenProvider;
        this.stopBusService=stopBusService;
        this.lineService=lineService;
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
        if(userPOST.getRoles().contains(Role.ROLE_SYS_ADMIN)&&
                !jwtTokenProvider.getRoles(jwtToken).contains(Role.ROLE_SYS_ADMIN))
            throw new ForbiddenException();
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
    @ApiOperation(value = "Ritorna l'utente idUser (senza password)")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public UserGET getUserById(@RequestHeader (name="Authorization") String jwtToken,
                            @PathVariable("idUser")String idUser) {
        String username=jwtTokenProvider.getUsername(jwtToken);
        List roles=jwtTokenProvider.getRoles(jwtToken);
        if(roles.contains(Role.ROLE_SYS_ADMIN)||roles.contains(Role.ROLE_ADMIN))
            return new UserGET(this.userService.loadUserByUsername(idUser));
        if(username.equals(idUser))
            return new UserGET(this.userService.loadUserByUsername(idUser));
        throw new ForbiddenException();
    }


    @PutMapping(value = "/{idUser}/addRole",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Aggiunge ruolo all'utente idUser.")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public UserGET addRoleById(@RequestHeader (name="Authorization") String jwtToken,
                                   @PathVariable("idUser")String idUser,
                                   @RequestParam @Valid Role role) {
        if(role.equals(Role.ROLE_SYS_ADMIN)&&!jwtTokenProvider.getRoles(jwtToken).contains(Role.ROLE_SYS_ADMIN))
            throw new ForbiddenException();
        return new UserGET(userService.addRole(idUser,role));
    }

    //TODO fix bug remove Role.ROLE_ADMIN and lines...
    @PutMapping(value = "/{idUser}/removeRole",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rimuove ruolo all'utente idUser.")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public UserGET removeRoleById(@RequestHeader (name="Authorization") String jwtToken,
                                   @PathVariable("idUser")String idUser,
                                   @RequestParam @Valid Role role) {
        if(role.equals(Role.ROLE_SYS_ADMIN)&&!jwtTokenProvider.getRoles(jwtToken).contains(Role.ROLE_SYS_ADMIN))
            throw new ForbiddenException();
        return new UserGET(userService.removeRole(idUser,role));
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
        List roles=jwtTokenProvider.getRoles(jwtToken);
        if(roles.contains(Role.ROLE_SYS_ADMIN))
            return new UserGET(userService.addLine(idUser,idLine));
        if(roles.contains(Role.ROLE_ADMIN)&&userService.isAdminOfLine(jwtTokenProvider.getUsername(jwtToken),idLine))
            return new UserGET(userService.addLine(idUser,idLine));
        throw new ForbiddenException();
    }

    @PutMapping(value = "/{idUser}/removeLine",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rimuove linea all'utente idUser, " +
            "nota che il ruolo di ROLE_ADMIN persisterà anche se non ha piu linee.")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public UserGET putUserRemoveLine(@RequestHeader (name="Authorization") String jwtToken,
                                  @PathVariable("idUser")String idUser,
                                  @RequestParam @Valid String idLine) {
        List roles=jwtTokenProvider.getRoles(jwtToken);
        if(roles.contains(Role.ROLE_SYS_ADMIN))
            return new UserGET(userService.removeLine(idUser,idLine));
        if(roles.contains(Role.ROLE_ADMIN)&&userService.isAdminOfLine(jwtTokenProvider.getUsername(jwtToken),idLine))
            return new UserGET(userService.removeLine(idUser,idLine));
        throw new ForbiddenException();
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
        if(!jwtTokenProvider.getUsername(jwtToken).equals(idUser))
            throw new ForbiddenException();
        return new UserGET(
                userService.updateUser(idUser,userPUT.getPassword(),userPUT.getFirstname(),
                userPUT.getSurname(),userPUT.getBirth(),userPUT.getStreet(),userPUT.getPhoneNumber()));
    }

    @PutMapping(value = "/{idUser}/disable",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Disabilita utente idUser")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public UserGET disableUser(@RequestHeader (name="Authorization") String jwtToken,
                               @PathVariable("idUser")String idUser) {
        return new UserGET(userService.disableById(idUser));
    }

    @PutMapping(value = "/{idUser}/undisable",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Riabilita utente idUser")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public UserGET undisableUser(@RequestHeader (name="Authorization") String jwtToken,
                               @PathVariable("idUser")String idUser) {
        return new UserGET(userService.undisableById(idUser));
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
        List roles=jwtTokenProvider.getRoles(jwtToken);
        if(roles.contains(Role.ROLE_SYS_ADMIN)||roles.contains(Role.ROLE_ADMIN)||roles.contains(Role.ROLE_ESCORT)
        ||(roles.contains(Role.ROLE_PARENT)&&idUser.equals(jwtTokenProvider.getUsername(jwtToken))))
            return this.childService.findByIdUser(idUser).stream()
                    .map(x->new ChildGET(x,stopBusService,lineService)).collect(Collectors.toSet());
        throw new ForbiddenException();
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
        List roles=jwtTokenProvider.getRoles(jwtToken);
        if(roles.contains(Role.ROLE_SYS_ADMIN)||roles.contains(Role.ROLE_ADMIN)||roles.contains(Role.ROLE_ESCORT)
                ||(roles.contains(Role.ROLE_PARENT)&&idUser.equals(jwtTokenProvider.getUsername(jwtToken))))
            return this.reservationService.findAllByIdUser(idUser).stream()
                    .map(x->new ReservationGET(x,childService,stopBusService,lineService)).collect(Collectors.toSet());
        throw new ForbiddenException();
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
        List roles=jwtTokenProvider.getRoles(jwtToken);
        if(roles.contains(Role.ROLE_SYS_ADMIN)||roles.contains(Role.ROLE_ADMIN)||
                (roles.contains(Role.ROLE_ESCORT)&&idUser.equals(jwtTokenProvider.getUsername(jwtToken))))
            return this.availabilityService.findAllByIdUser(idUser).stream()
                    .map(AvailabilityGET::new).collect(Collectors.toSet());
        throw new ForbiddenException();
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
        if(!jwtTokenProvider.getUsername(jwtToken).equals(idUser))
            throw new ForbiddenException();
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
        if(!jwtTokenProvider.getUsername(jwtToken).equals(idUser))
            throw new ForbiddenException();
        return this.messageService.findAllByIdUserTo(idUser).stream()
                .filter(x->x.getReadConfirm()==null).count();
    }



}
