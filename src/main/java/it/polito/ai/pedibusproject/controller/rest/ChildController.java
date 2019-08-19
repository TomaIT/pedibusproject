package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.get.ChildGET;
import it.polito.ai.pedibusproject.controller.model.get.ReservationGET;
import it.polito.ai.pedibusproject.controller.model.post.ChildPOST;
import it.polito.ai.pedibusproject.database.model.Gender;
import it.polito.ai.pedibusproject.exceptions.BadRequestException;
import it.polito.ai.pedibusproject.security.JwtTokenProvider;
import it.polito.ai.pedibusproject.service.interfaces.ChildService;
import it.polito.ai.pedibusproject.service.interfaces.ReservationService;
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
@RequestMapping("/rest/children")
public class ChildController {
    private ChildService childService;
    private JwtTokenProvider jwtTokenProvider;
    private ReservationService reservationService;

    @Autowired
    public ChildController(ChildService childService, JwtTokenProvider jwtTokenProvider,
                           ReservationService reservationService){
        this.childService=childService;
        this.reservationService=reservationService;
        this.jwtTokenProvider=jwtTokenProvider;
    }

    @GetMapping(value = "/genders",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna i possibili valori di Gender")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<String> getGenderValues() {
        return Arrays.stream(Gender.values()).map(Enum::name).collect(Collectors.toSet());
    }

    @GetMapping(value = "/{idChild}",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna il bambino idChild")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ChildGET getChildById(@RequestHeader (name="Authorization") String jwtToken,
                                 @PathVariable("idChild")String idChild) {
        return new ChildGET(
                this.childService.findById(idChild)
        );
    }


    @PostMapping(value = "/{idUser}",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Crea bambino")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ChildGET postChildrenById(@RequestHeader (name="Authorization") String jwtToken,
                                  @PathVariable("idUser")String idUser,
                                  @RequestBody @Valid ChildPOST childPOST) {
        String username=this.jwtTokenProvider.getUsername(jwtToken);
        if(!idUser.equals(username))
            throw new BadRequestException("Authentication Token and IdUser are different.");
        return new ChildGET(
                this.childService.create(idUser,childPOST.getFirstname(),childPOST.getSurname(),childPOST.getBirth(),
                childPOST.getGender(),childPOST.getBlobBase64(),childPOST.getIdStopBusOutDef(),childPOST.getIdStopBusRetDef())
        );
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
    public ChildGET putChildById(@RequestHeader (name="Authorization") String jwtToken,
                              @PathVariable("idChild")String idChild,
                              @RequestBody @Valid ChildPOST childPOST) {
        return new ChildGET(
                this.childService.update(idChild,childPOST.getFirstname(),childPOST.getSurname(),childPOST.getBirth(),
                childPOST.getGender(),childPOST.getBlobBase64(),childPOST.getIdStopBusOutDef(),childPOST.getIdStopBusRetDef())
        );
    }

    @DeleteMapping(value = "/{idChild}")
    @ApiOperation(value = "Cancella bambino idChild")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public void deleteChildById(@RequestHeader (name="Authorization") String jwtToken,
                                @PathVariable("idChild")String idChild) {
        this.childService.deleteById(idChild);
    }

    @GetMapping(value = "/{idChild}/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tutte le prenotazioni per idChild")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found Child"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<ReservationGET> getReservationByChild(@RequestHeader (name="Authorization") String jwtToken,
                                                     @PathVariable("idChild")String idChild) {
        return this.reservationService.findAllByIdChild(idChild).stream()
                .map(ReservationGET::new).collect(Collectors.toSet());
    }
}
