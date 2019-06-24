package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.LineEnum;
import it.polito.ai.pedibusproject.database.model.Line;
import it.polito.ai.pedibusproject.service.interfaces.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/rest/lines")
public class LineController {
    private LineService lineService;

    @Autowired
    public LineController(LineService lineService){
        this.lineService=lineService;
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tutte le linee (id e nome)")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<LineEnum> getLineNames() {
        return this.lineService.aggregateNames();
    }

    @GetMapping(value = "/{idLine}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna idLine")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Line getLine(@PathVariable("idLine") String idLine) {
        return this.lineService.findById(idLine);
    }

}
