package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.BusRidePOST;
import it.polito.ai.pedibusproject.controller.model.BusRidePUT;
import it.polito.ai.pedibusproject.database.model.BusRide;
import it.polito.ai.pedibusproject.database.model.Reservation;
import it.polito.ai.pedibusproject.database.model.StopBusType;
import it.polito.ai.pedibusproject.exceptions.NotImplementedException;
import it.polito.ai.pedibusproject.service.interfaces.BusRideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.TreeSet;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/rest/busrides")
public class BusRideController {
    private BusRideService busRideService;

    @Autowired
    public BusRideController(BusRideService busRideService){
        this.busRideService=busRideService;
    }

    @GetMapping(value = "",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tutte le corse (solo per debug)")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public TreeSet<BusRide> getBusRides() {
        return this.busRideService.findAll();
    }

    @GetMapping(value = "/{idBusRide}",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna corsa idBusRide")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public BusRide getBusRide(@PathVariable("idBusRide")String idBusRide) {
        return this.busRideService.findById(idBusRide);
    }

    @GetMapping(value = "/{idBusRide}/availabilities",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna corsa idBusRide")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public BusRide getAvailabilities(@PathVariable("idBusRide")String idBusRide) {
        //TODO
        throw new NotImplementedException();
    }

    @GetMapping(value = "/{idLine}/{stopBusType}/{year}/{month}/{day}",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna corsa per quella linea (andata/ritorno) in quel giorno. (NOTA mese: 0-11)")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public BusRide getBusRideByDateAndIdLine(@PathVariable("idLine")String idLine,
                                                  @PathVariable("stopBusType") StopBusType stopBusType,
                                                  @PathVariable("year")Integer year,
                                                  @PathVariable("month")Integer month,
                                                  @PathVariable("day")Integer day) {
        BusRide busRide=this.busRideService.findByIdLineAndStopBusTypeAndYearAndMonthAndDay(idLine,stopBusType,
                year,month,day);
        System.out.println(busRide.getStartTime());
        return busRide;
    }



    @PostMapping(value = "",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Crea Nuova Corsa 'eccezionale', " +
            "(di default esistono già per tutti i giorni della settimana " +
            "dal 01/01/2019 al 31/12/2019) (application.properties)")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public BusRide postBusRide(@RequestBody @Valid BusRidePOST busRidePOST) {
        return this.busRideService.create(busRidePOST.getIdLine(),busRidePOST.getStopBusType(),
                busRidePOST.getYear(),busRidePOST.getMonth(),busRidePOST.getDay());
    }

    @PutMapping(value = "/{idBusRide}",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Aggiorna la posizione del 'bus'")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public BusRide putBusRide(@RequestBody @Valid BusRidePUT busRidePUT) {
        //TODO
        throw new NotImplementedException();
    }

    @DeleteMapping(value = "/{idBusRide}",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "'Elimina' tale corsa. (Crea messaggio per tutte le prenotazioni annullate)")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public BusRide deleteBusRide() {
        //TODO
        throw new NotImplementedException();
    }
}
