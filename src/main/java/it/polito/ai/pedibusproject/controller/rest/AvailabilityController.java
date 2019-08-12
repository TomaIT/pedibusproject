package it.polito.ai.pedibusproject.controller.rest;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.get.AvailabilityGET;
import it.polito.ai.pedibusproject.controller.model.post.AvailabilityPOST;
import it.polito.ai.pedibusproject.controller.model.put.AvailabilityPUT;
import it.polito.ai.pedibusproject.database.model.AvailabilityState;
import it.polito.ai.pedibusproject.exceptions.NotImplementedException;
import it.polito.ai.pedibusproject.service.interfaces.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private AvailabilityService availabilityService;

    @Autowired
    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

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
    public AvailabilityGET getAvailability(@RequestHeader (name="Authorization") String jwtToken,
                                                 @PathVariable("idAvailability")String idAvailability) {
        return new AvailabilityGET(this.availabilityService.findById(idAvailability));
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
    public AvailabilityGET postAvailability(@RequestHeader (name="Authorization") String jwtToken,
                                         @RequestBody @Valid AvailabilityPOST availabilityPOST) {
        return new AvailabilityGET(
                this.availabilityService.create(availabilityPOST.getIdBusRide(), availabilityPOST.getIdStopBus(),
                                                availabilityPOST.getIdUser(), availabilityPOST.getState())
        );
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
    public AvailabilityGET putAvailability(@RequestHeader (name="Authorization") String jwtToken,
                                        @PathVariable("idAvailability")String idAvailability,
                                        @RequestBody @Valid AvailabilityPUT availabilityPUT) {

        return new AvailabilityGET(
                this.availabilityService.update(idAvailability, availabilityPUT.getIdStopBus(), availabilityPUT.getState())
        );
    }

    @DeleteMapping(value = "/{idAvailability}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Cancella availability")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public void deleteAvailability(@RequestHeader (name="Authorization") String jwtToken,
                                           @PathVariable("idAvailability")String idAvailability) {

        this.availabilityService.deleteById(idAvailability);
    }

}
