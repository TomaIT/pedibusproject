package it.polito.ai.pedibusproject.controller.rest;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.AvailabilityPOST;
import it.polito.ai.pedibusproject.controller.model.AvailabilityPUT;
import it.polito.ai.pedibusproject.controller.model.BusRidePOST;
import it.polito.ai.pedibusproject.database.model.Availability;
import it.polito.ai.pedibusproject.database.model.AvailabilityState;
import it.polito.ai.pedibusproject.database.model.BusRide;
import it.polito.ai.pedibusproject.exceptions.NotImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/rest/availabilities")
public class AvailabilityController {

    @GetMapping(value = "/states",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tutti i possibili stati di availability")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<String> getAvailabilityStates() {
        return Arrays.stream(AvailabilityState.values()).map(Enum::name).collect(Collectors.toSet());
    }

    @GetMapping(value = "/{idAvailability}",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tale availability")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<String> getAvailabilityStates(@PathVariable("idAvailability")String idAvailability) {
        //TODO
        throw new NotImplementedException();
    }

    @PostMapping(value = "",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Crea nuova availability")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Availability postAvailability(@RequestBody @Valid AvailabilityPOST availabilityPOST) {
        //TODO
        throw new NotImplementedException();
    }

    @PutMapping(value = "/{idAvailability}",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Aggiorna availability")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Availability putAvailability(@PathVariable("idAvailability")String idAvailability,
                                        @RequestBody @Valid AvailabilityPUT availabilityPUT) {
        //TODO
        throw new NotImplementedException();
    }

    @DeleteMapping(value = "/{idAvailability}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Cancella availability")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Availability deleteAvailability(@PathVariable("idAvailability")String idAvailability) {
        //TODO
        throw new NotImplementedException();
    }

}
