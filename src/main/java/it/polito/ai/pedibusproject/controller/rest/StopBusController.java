package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.database.model.StopBus;
import it.polito.ai.pedibusproject.service.interfaces.StopBusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/rest/stopbuses")
public class StopBusController {
    private StopBusService stopBusService;

    @Autowired
    public StopBusController(StopBusService stopBusService){
        this.stopBusService=stopBusService;
    }

    /*@GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tutti gli StopBus")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<StopBus> getLines() {
        return this.stopBusService.findAll();
    }*/

    @GetMapping(value = "/{idStopBus}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna idStopBus")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public StopBus getLine(@PathVariable("idStopBus") String idStopBus) {
        return this.stopBusService.findById(idStopBus);
    }
}
