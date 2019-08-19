package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.get.ReservationGET;
import it.polito.ai.pedibusproject.controller.model.post.ReservationPOST;
import it.polito.ai.pedibusproject.controller.model.put.ReservationPUT;
import it.polito.ai.pedibusproject.database.model.Child;
import it.polito.ai.pedibusproject.database.model.Reservation;
import it.polito.ai.pedibusproject.database.model.ReservationState;
import it.polito.ai.pedibusproject.database.model.Role;
import it.polito.ai.pedibusproject.exceptions.BadRequestException;
import it.polito.ai.pedibusproject.exceptions.ForbiddenException;
import it.polito.ai.pedibusproject.security.JwtTokenProvider;
import it.polito.ai.pedibusproject.service.interfaces.ChildService;
import it.polito.ai.pedibusproject.service.interfaces.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/rest/reservations")
public class ReservationController {
    private ReservationService reservationService;
    private JwtTokenProvider jwtTokenProvider;
    private ChildService childService;


    @Autowired
    public ReservationController(ReservationService reservationService,
                                 JwtTokenProvider jwtTokenProvider,
                                 ChildService childService){
        this.reservationService=reservationService;
        this.jwtTokenProvider=jwtTokenProvider;
        this.childService=childService;
    }


    @GetMapping(value = "/{idReservation}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tale prenotazione")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ReservationGET getReservationById(@RequestHeader (name="Authorization") String jwtToken,
                                                  @PathVariable("idReservation")String idReservation) {
        Reservation temp=this.reservationService.findById(idReservation);
        String username=jwtTokenProvider.getUsername(jwtToken);
        if(temp.getIdUser().equals(username))
            return new ReservationGET(temp);

        List roles=jwtTokenProvider.getRoles(jwtToken);
        if(roles.contains(Role.ROLE_SYS_ADMIN)||roles.contains(Role.ROLE_ADMIN)||
            roles.contains(Role.ROLE_ESCORT))
            return new ReservationGET(temp);

        throw new ForbiddenException();
    }

    @PostMapping(value = "",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Crea nuova prenotazione")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ReservationGET postReservation(@RequestHeader (name="Authorization") String jwtToken,
                                       @RequestBody @Valid ReservationPOST reservationPOST) {
        String username=jwtTokenProvider.getUsername(jwtToken);
        if( childService.findByIdUser(username).stream()
                .map(Child::getId).anyMatch(x->x.equals(reservationPOST.getIdChild())))
            return new ReservationGET(
                    reservationService.create(reservationPOST.getIdBusRide(),reservationPOST.getIdChild(),
                    reservationPOST.getIdStopBus(),username));
        throw new ForbiddenException();
    }

    @PutMapping(value = "/{idReservation}",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Aggiorna stato della prenotazione idReservation")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ReservationGET putReservationById(@RequestHeader (name="Authorization") String jwtToken,
                                          @PathVariable("idReservation")String idReservation,
                                          @RequestBody @Valid ReservationPUT reservationPUT) {
        String username=jwtTokenProvider.getUsername(jwtToken);
        ReservationState rs=new ReservationState(
                reservationPUT.getIdStopBus(),
                (new Date()).getTime(),
                username);
        ReservationGET ret;
        switch (reservationPUT.getEnumChildGet()){
            case GetIn:
                ret=new ReservationGET(reservationService.updateGetIn(idReservation,rs));
                break;
            case GetOut:
                ret=new ReservationGET(reservationService.updateGetOut(idReservation,rs));
                break;
            default:
                throw new BadRequestException("Update ReservationState enumChildGet invalid.");
        }
        return ret;
    }

    @DeleteMapping(value = "/{idReservation}")
    @ApiOperation(value = "Cancella prenotazione idReservation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public void deleteReservationById(@RequestHeader (name="Authorization") String jwtToken,
                                      @PathVariable("idReservation")String idReservation) {
        List roles=jwtTokenProvider.getRoles(jwtToken);
        if(roles.contains(Role.ROLE_SYS_ADMIN)||roles.contains(Role.ROLE_ADMIN)) {
            this.reservationService.deleteById(idReservation);
            return;
        }
        Reservation temp=reservationService.findById(idReservation);
        if(temp.getIdUser().equals(jwtTokenProvider.getUsername(jwtToken))) {
            this.reservationService.deleteById(idReservation);
            return;
        }
        throw new ForbiddenException();
    }
}
