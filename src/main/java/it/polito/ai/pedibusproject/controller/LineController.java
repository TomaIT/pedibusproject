package it.polito.ai.pedibusproject.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.database.model.Line;
import it.polito.ai.pedibusproject.service.interfaces.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
    @ApiOperation(value = "Get All Line Names")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<String> getLineNames() {
        return this.lineService.aggregateNames();
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get All Line")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<Line> getLines() {
        return this.lineService.findAll();
    }

    @GetMapping(value = "/{idLine}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Line By ID")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Line getLine(@PathVariable("idLine") String idLine) {
        return this.lineService.findById(idLine);
    }



}
