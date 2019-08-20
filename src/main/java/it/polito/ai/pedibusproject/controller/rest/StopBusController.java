package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.get.StopBusGET;
import it.polito.ai.pedibusproject.database.model.StopBusType;
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

    @Autowired
    public StopBusController(StopBusService stopBusService){
        this.stopBusService=stopBusService;
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
                this.stopBusService.findById(idStopBus)
        );
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
