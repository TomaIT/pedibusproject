package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.ChildPOST;
import it.polito.ai.pedibusproject.database.model.Child;
import it.polito.ai.pedibusproject.database.model.Gender;
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
@RequestMapping("/rest/children")
public class ChildController {

    @GetMapping(value = "/genders",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna i possibili valori di Gender")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<String> getGenderValues() {
        return Arrays.stream(Gender.values()).map(Enum::name).collect(Collectors.toSet());
    }

    @GetMapping(value = "/{idUser}",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna i bambini dell'utente idUser")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<Child> getChildrenById(@PathVariable("idUser")String idUser) {
        //TODO
        throw new NotImplementedException();
    }

    @GetMapping(value = "{idChild}",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna il bambino idChild")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Child getChildById(@PathVariable("idChild")String idChild) {
        //TODO
        throw new NotImplementedException();
    }

    @PostMapping(value = "/{idUser}",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Crea e aggiunge bambino all'utente idUser")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Child postChildrenById(@PathVariable("idUser")String idUser,
                                       @RequestBody @Valid ChildPOST childPOST) {
        //TODO
        throw new NotImplementedException();
    }

    @PutMapping(value = "/{idChild}",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Modifica bambino idChild")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Child putChildById(@PathVariable("idChild")String idChild,
                              @RequestBody @Valid ChildPOST childPOST) {
        //TODO
        throw new NotImplementedException();
    }
}
