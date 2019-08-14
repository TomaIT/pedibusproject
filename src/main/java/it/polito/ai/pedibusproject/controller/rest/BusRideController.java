package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.get.AvailabilityGET;
import it.polito.ai.pedibusproject.controller.model.get.BusRideGET;
import it.polito.ai.pedibusproject.controller.model.post.BusRidePOST;
import it.polito.ai.pedibusproject.controller.model.put.BusRidePUT;
import it.polito.ai.pedibusproject.database.model.BusRide;
import it.polito.ai.pedibusproject.database.model.StopBusType;
import it.polito.ai.pedibusproject.service.interfaces.AvailabilityService;
import it.polito.ai.pedibusproject.service.interfaces.BusRideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/rest/busrides")
public class BusRideController {
    private BusRideService busRideService;
    private AvailabilityService availabilityService;

    @Autowired
    public BusRideController(BusRideService busRideService,AvailabilityService availabilityService){
        this.busRideService=busRideService;
        this.availabilityService=availabilityService;

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
    public BusRideGET getBusRide(@RequestHeader (name="Authorization") String jwtToken,
                              @PathVariable("idBusRide")String idBusRide) {
        return new BusRideGET(this.busRideService.findById(idBusRide));
    }

    @GetMapping(value = "/{idBusRide}/availabilities",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tutte le availabilities per una data corsa (idBusRide)")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<AvailabilityGET> getAvailabilities(@RequestHeader (name="Authorization") String jwtToken,
                                                  @PathVariable("idBusRide")String idBusRide) {
        return this.availabilityService.findAllByIdBusRide(idBusRide).stream()
                .map(AvailabilityGET::new).collect(Collectors.toSet());
    }

    @GetMapping(value = "/{idLine}/{stopBusType}/{year}/{month}/{day}",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna corsa per quella linea (andata/ritorno) in quel giorno. (NOTA mese: 0-11)")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public BusRideGET getBusRideByDateAndIdLine(@RequestHeader (name="Authorization") String jwtToken,
                                             @PathVariable("idLine")String idLine,
                                             @PathVariable("stopBusType") StopBusType stopBusType,
                                             @PathVariable("year")Integer year,
                                             @PathVariable("month")Integer month,
                                             @PathVariable("day")Integer day) {
        return new BusRideGET(
                this.busRideService.findByIdLineAndStopBusTypeAndYearAndMonthAndDay(idLine,stopBusType, year,month,day)
        );
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
    public BusRideGET postBusRide(@RequestHeader (name="Authorization") String jwtToken,
                               @RequestBody @Valid BusRidePOST busRidePOST) {
        return new BusRideGET(
                this.busRideService.create(busRidePOST.getIdLine(),busRidePOST.getStopBusType(),
                busRidePOST.getYear(),busRidePOST.getMonth(),busRidePOST.getDay())
        );
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
    public BusRideGET putBusRide(@RequestHeader (name="Authorization") String jwtToken,
                              @PathVariable("idBusRide")String idBusRide,
                              @RequestBody @Valid BusRidePUT busRidePUT) {
        return new BusRideGET(
                this.busRideService.updateLastStopBus(idBusRide,busRidePUT.getTimestampLastStopBus(),busRidePUT.getIdLastStopBus())
        );
    }

    @DeleteMapping(value = "/{idBusRide}")
    @ApiOperation(value = "'Elimina' tale corsa. (Crea messaggio per tutte le prenotazioni annullate)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public void deleteBusRide(@RequestHeader (name="Authorization") String jwtToken,
                                    @PathVariable("idBusRide")String idBusRide) {
        this.busRideService.deleteById(idBusRide);
    }
}
