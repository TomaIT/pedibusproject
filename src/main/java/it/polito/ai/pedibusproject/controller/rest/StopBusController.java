package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.get.ChildGET;
import it.polito.ai.pedibusproject.controller.model.get.StopBusGET;
import it.polito.ai.pedibusproject.database.model.StopBusType;
import it.polito.ai.pedibusproject.exceptions.InternalServerErrorException;
import it.polito.ai.pedibusproject.service.interfaces.ChildService;
import it.polito.ai.pedibusproject.service.interfaces.LineService;
import it.polito.ai.pedibusproject.service.interfaces.StopBusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/rest/stopbuses")
public class StopBusController {
    private StopBusService stopBusService;
    private ChildService childService;
    private LineService lineService;

    @Autowired
    public StopBusController(StopBusService stopBusService,
                             ChildService childService,
                             LineService lineService){
        this.stopBusService=stopBusService;
        this.childService=childService;
        this.lineService=lineService;
    }

    @GetMapping(value = "/withType/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tutti gli stopBus con quel type")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<StopBusGET> getStopBusesWithType(@RequestHeader (name="Authorization") String jwtToken,
                              @PathVariable("type") StopBusType stopBusType) {
        return this.stopBusService.findAllByStopBusType(stopBusType).stream()
                .map(x->new StopBusGET(x,lineService)).collect(Collectors.toSet());
    }

    @GetMapping(value = "/{idStopBus}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna idStopBus")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public StopBusGET getLine(@RequestHeader (name="Authorization") String jwtToken,
                              @PathVariable("idStopBus") String idStopBus) {
        return new StopBusGET(
                this.stopBusService.findById(idStopBus),lineService
        );
    }

    @GetMapping(value = "/{idStopBus}/children", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tutti i bambini che hanno come fermata di default idStopBus")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<ChildGET> getChildrenStopBusDef(@RequestHeader (name="Authorization") String jwtToken,
                                               @PathVariable("idStopBus") String idStopBus) {
        switch (stopBusService.findById(idStopBus).getStopBusType()){
            case Outward:
                return childService.findAllByIdStopBusOutDef(idStopBus).stream().map(x->new ChildGET(x,stopBusService,lineService)).collect(Collectors.toSet());
            case Return:
                return childService.findAllByIdStopBusRetDef(idStopBus).stream().map(x->new ChildGET(x,stopBusService,lineService)).collect(Collectors.toSet());
            default:
                throw new InternalServerErrorException("StopBusType unrecognized.");
        }
    }

    @GetMapping(value = "/types", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna i possibili valori di StopBusType (Return/Outward)")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<String> getStopBusTypes(@RequestHeader (name="Authorization") String jwtToken) {
        return Arrays.stream(StopBusType.values()).map(Enum::name).collect(Collectors.toSet());
    }
}
