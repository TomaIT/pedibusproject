package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.get.ChildGET;
import it.polito.ai.pedibusproject.controller.model.get.EnumChildGET;
import it.polito.ai.pedibusproject.controller.model.get.ReservationGET;
import it.polito.ai.pedibusproject.controller.model.post.ChildPOST;
import it.polito.ai.pedibusproject.controller.model.put.ReservationPUT;
import it.polito.ai.pedibusproject.database.model.*;
import it.polito.ai.pedibusproject.exceptions.BadRequestException;
import it.polito.ai.pedibusproject.exceptions.DuplicateKeyException;
import it.polito.ai.pedibusproject.exceptions.ForbiddenException;
import it.polito.ai.pedibusproject.exceptions.NotImplementedException;
import it.polito.ai.pedibusproject.security.JwtTokenProvider;
import it.polito.ai.pedibusproject.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/rest/children")
public class ChildController {
    private ChildService childService;
    private JwtTokenProvider jwtTokenProvider;
    private ReservationService reservationService;
    private StopBusService stopBusService;
    private LineService lineService;
    private BusRideService busRideService;

    @Autowired
    public ChildController(ChildService childService, JwtTokenProvider jwtTokenProvider,
                           ReservationService reservationService,
                           StopBusService stopBusService,
                           LineService lineService,BusRideService busRideService){
        this.childService=childService;
        this.reservationService=reservationService;
        this.jwtTokenProvider=jwtTokenProvider;
        this.stopBusService=stopBusService;
        this.lineService=lineService;
        this.busRideService=busRideService;
    }

    @GetMapping(value = "/genders",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna i possibili valori di Gender")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<String> getGenderValues() {
        return Arrays.stream(Gender.values()).map(Enum::name).collect(Collectors.toSet());
    }

    @GetMapping(value = "/{idChild}",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna il bambino idChild")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ChildGET getChildById(@RequestHeader (name="Authorization") String jwtToken,
                                 @PathVariable("idChild")String idChild) {
        List roles = jwtTokenProvider.getRoles(jwtToken);
        if(roles.contains(Role.ROLE_ADMIN)||
                roles.contains(Role.ROLE_SYS_ADMIN)||
                roles.contains(Role.ROLE_ESCORT)
        )
            return new ChildGET(
                    this.childService.findById(idChild),
                    stopBusService,lineService
            );
        String username=jwtTokenProvider.getUsername(jwtToken);
        if(childService.findByIdUser(username).stream().map(Child::getId).anyMatch(x->x.equals(idChild)))
            return new ChildGET(
                    this.childService.findById(idChild),
                    stopBusService,lineService
            );
        throw new ForbiddenException();
    }


    @PostMapping(value = "/{idUser}",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Crea bambino")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ChildGET postChildrenById(@RequestHeader (name="Authorization") String jwtToken,
                                  @PathVariable("idUser")String idUser,
                                  @RequestBody @Valid ChildPOST childPOST) {
        String username=this.jwtTokenProvider.getUsername(jwtToken);
        if(!idUser.equals(username))
            throw new BadRequestException("Authentication Token and IdUser are different.");
        return new ChildGET(
                this.childService.create(idUser,childPOST.getFirstname(),childPOST.getSurname(),childPOST.getBirth(),
                childPOST.getGender(),childPOST.getBlobBase64(),childPOST.getIdStopBusOutDef(),childPOST.getIdStopBusRetDef()),
                stopBusService,lineService
        );
    }

    @PostMapping(value = "/{idChild}/{idBusRide}/{idStopBus}/isTakenWithoutReservation",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Segna il bambino (non prenotato) come preso. (SOLO Outward e GetIn).")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ReservationGET postChildTakenWithoutReservation(@RequestHeader (name="Authorization") String jwtToken,
                                     @PathVariable("idChild")String idChild,
                                     @PathVariable("idBusRide")String idBusRide,
                                     @PathVariable("idStopBus")String idStopBus,
                                     @RequestBody @Valid ReservationPUT reservationPUT) {
        //Prova prima a creare la reservation (SUPPONENDO CHE ESSA NON ESISTA)
        StopBusType stopBusType=this.busRideService.findById(idBusRide).getStopBusType();
        if(!stopBusType.equals(StopBusType.Outward) || !reservationPUT.getEnumChildGet().equals(EnumChildGET.GetIn)){
            throw new BadRequestException("This method is only to Outward and GetIn");
        }
        String username = jwtTokenProvider.getUsername(jwtToken);
        Reservation reservation=new Reservation(idBusRide,idChild,idStopBus,username);
        ReservationState rs=new ReservationState(
                reservationPUT.getIdStopBus(),
                (new Date()).getTime(),
                username);

        reservation.setGetIn(rs);

        try {
            return new ReservationGET(reservationService.create(reservation),childService,stopBusService,lineService);
        }catch (DuplicateKeyException e){//Allora reservation esiste... proviamo ad aggiornarla solo se Outward
            //TODO
            throw new NotImplementedException("La prenotazione ha un conflitto, questa funzionalità è ancora da implementare");
        }
    }

    @PutMapping(value = "/{idChild}",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Modifica bambino idChild")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ChildGET putChildById(@RequestHeader (name="Authorization") String jwtToken,
                              @PathVariable("idChild")String idChild,
                              @RequestBody @Valid ChildPOST childPOST) {
        if(jwtTokenProvider.getRoles(jwtToken).contains(Role.ROLE_PARENT)&&
            childService.findByIdUser(jwtTokenProvider.getUsername(jwtToken)).stream()
        .map(Child::getId).anyMatch(x->x.equals(idChild)))
            return new ChildGET(
                    this.childService.update(idChild,childPOST.getFirstname(),childPOST.getSurname(),childPOST.getBirth(),
                    childPOST.getGender(),childPOST.getBlobBase64(),childPOST.getIdStopBusOutDef(),childPOST.getIdStopBusRetDef()),
                    stopBusService,lineService
            );
        throw new ForbiddenException();
    }

    @DeleteMapping(value = "/{idChild}")
    @ApiOperation(value = "Cancella bambino idChild")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public void deleteChildById(@RequestHeader (name="Authorization") String jwtToken,
                                @PathVariable("idChild")String idChild) {
        if(jwtTokenProvider.getRoles(jwtToken).contains(Role.ROLE_PARENT)&&
                childService.findByIdUser(jwtTokenProvider.getUsername(jwtToken)).stream()
                        .map(Child::getId).anyMatch(x->x.equals(idChild))) {
            this.childService.deleteById(idChild);
            return;
        }
        throw new ForbiddenException();
    }

    @GetMapping(value = "/{idChild}/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tutte le prenotazioni per idChild")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found Child"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<ReservationGET> getReservationByChild(@RequestHeader (name="Authorization") String jwtToken,
                                                     @PathVariable("idChild")String idChild) {
        List roles = jwtTokenProvider.getRoles(jwtToken);
        if(roles.contains(Role.ROLE_ADMIN)||
                roles.contains(Role.ROLE_SYS_ADMIN)
        )
            return this.reservationService.findAllByIdChild(idChild).stream()
                    .map(x->new ReservationGET(x,childService,stopBusService,lineService)).collect(Collectors.toSet());
        String username=jwtTokenProvider.getUsername(jwtToken);
        if(roles.contains(Role.ROLE_PARENT)&&
                childService.findByIdUser(username).stream().map(Child::getId).anyMatch(x->x.equals(idChild)))
            return this.reservationService.findAllByIdChild(idChild).stream()
                    .map(x->new ReservationGET(x,childService,stopBusService,lineService)).collect(Collectors.toSet());
        throw new ForbiddenException();
    }

    @GetMapping(value = "/{idBusRide}/{idStopBus}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tutti i bambini 'prendibili', per quella fermata e quella corsa, " +
            "esso avviene solo se tale bambino non ha prenotazioni in quello SLOT, " +
            "oppure se ha prenotazioni ma non sono ancora state 'usate' (getIN==NULL). " +
            "Questo metodo è valido solo per Outward. (SONO ESCLUSI I BAMBINI GIà PRENOTATI PER QUELLA CORSA)")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found Child"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<ChildGET> getAllChildrenAvailableInThisBusRide(@RequestHeader (name="Authorization") String jwtToken,
                                                     @PathVariable("idBusRide")String idBusRide,
                                                     @PathVariable("idStopBus")String idStopBus) {
        return this.childService.findAllAvailableToBeTaken(idBusRide,idStopBus).stream()
                .map(x->new ChildGET(x,stopBusService,lineService)).collect(Collectors.toSet());
        //throw new NotImplementedException();
    }
}
