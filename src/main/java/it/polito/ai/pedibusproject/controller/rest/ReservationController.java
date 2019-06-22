package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.ReservationPOST;
import it.polito.ai.pedibusproject.controller.model.ReservationPUT;
import it.polito.ai.pedibusproject.database.model.Reservation;
import it.polito.ai.pedibusproject.exceptions.NotImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/rest/reservations")
public class ReservationController {

    @GetMapping(value = "/{idUser}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tutte le prenotazioni effetuate da idUser")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found User"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<Reservation> getReservationByUser(@PathVariable("idUser")String idUser) {
        //TODO
        throw new NotImplementedException();
    }

    @GetMapping(value = "/{idChild}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tutte le prenotazioni per idChild")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found Child"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<Reservation> getReservationByChild(@PathVariable("idChild")String idChild) {
        //TODO
        throw new NotImplementedException();
    }

    @GetMapping(value = "/{idBusRide}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tutte le prenotazioni per idBusRide")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found BusRide"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<Reservation> getReservationByBusRide(@PathVariable("idBusRide")String idBusRide) {
        //TODO
        throw new NotImplementedException();
    }

    @PostMapping(value = "",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Crea nuova prenotazione")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Reservation postReservation(@RequestBody @Valid ReservationPOST reservationPOST) {
        //TODO
        throw new NotImplementedException();
    }

    @PutMapping(value = "/{idReservation}",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Modifica prenotazione idReservation")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Reservation putReservationById(@PathVariable("idReservation")String idReservation,
                                          @RequestBody @Valid ReservationPUT reservationPUT) {
        //TODO
        throw new NotImplementedException();
    }

    @DeleteMapping(value = "/{idReservation}")
    @ApiOperation(value = "Cancella prenotazione idReservation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public void deleteReservationById(@PathVariable("idReservation")String idReservation) {
        //TODO
        throw new NotImplementedException();
    }
}
