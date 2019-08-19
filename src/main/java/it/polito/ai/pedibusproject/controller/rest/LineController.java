package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.get.LineEnumGET;
import it.polito.ai.pedibusproject.controller.model.get.LineGET;
import it.polito.ai.pedibusproject.database.model.Line;
import it.polito.ai.pedibusproject.service.interfaces.LineService;
import it.polito.ai.pedibusproject.service.interfaces.StopBusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/rest/lines")
public class LineController {
    private LineService lineService;
    private StopBusService stopBusService;

    @Autowired
    public LineController(LineService lineService,StopBusService stopBusService){
        this.lineService=lineService;
        this.stopBusService=stopBusService;
    }


    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tutte le linee (id e nome)")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<LineEnumGET> getLineNames(@RequestHeader (name="Authorization") String jwtToken) {
        return this.lineService.aggregateNames();
    }


    @GetMapping(value = "/{idLine}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna idLine")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public LineGET getLine(@RequestHeader (name="Authorization") String jwtToken,
                           @PathVariable("idLine") String idLine) {
        Line temp=this.lineService.findById(idLine);
        LineGET ret=new LineGET(temp);
        temp.getIdRetStopBuses().forEach(x->ret.addStopBus(stopBusService.findById(x)));
        temp.getIdOutStopBuses().forEach(x->ret.addStopBus(stopBusService.findById(x)));
        return ret;
    }

}
