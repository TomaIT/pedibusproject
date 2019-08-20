package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.get.AvailabilityGET;
import it.polito.ai.pedibusproject.controller.model.get.PresenceBusRideGET;
import it.polito.ai.pedibusproject.database.model.BusRide;
import it.polito.ai.pedibusproject.database.model.StopBusType;
import it.polito.ai.pedibusproject.service.interfaces.BusRideService;
import it.polito.ai.pedibusproject.service.interfaces.ChildService;
import it.polito.ai.pedibusproject.service.interfaces.LineService;
import it.polito.ai.pedibusproject.service.interfaces.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/rest/aggregates")
public class AggregateController {
    private BusRideService busRideService;
    private ChildService childService;
    private ReservationService reservationService;
    private LineService lineService;

    @Autowired
    public AggregateController(BusRideService busRideService,
                               ChildService childService,
                               ReservationService reservationService,
                               LineService lineService){
        this.busRideService=busRideService;
        this.childService=childService;
        this.reservationService=reservationService;
        this.lineService=lineService;
    }


    @GetMapping(value = "/presence/{idLine}/{stopBusType}/{year}/{month}/{day}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna le infromazioni aggregate per le presenze.")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public PresenceBusRideGET getAvailabilities(@RequestHeader (name="Authorization") String jwtToken,
                                                @PathVariable("idLine")String idLine,
                                                @PathVariable("stopBusType") StopBusType stopBusType,
                                                @PathVariable("year")Integer year,
                                                @PathVariable("month")Integer month,
                                                @PathVariable("day")Integer day) {
        BusRide busRide=this.busRideService.findByIdLineAndStopBusTypeAndYearAndMonthAndDay(
                idLine,stopBusType,year,month,day);

        return new PresenceBusRideGET(busRide,lineService,childService,reservationService);
    }
}
