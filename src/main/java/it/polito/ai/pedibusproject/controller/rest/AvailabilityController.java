package it.polito.ai.pedibusproject.controller.rest;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.get.AvailabilityGET;
import it.polito.ai.pedibusproject.controller.model.post.AvailabilityPOST;
import it.polito.ai.pedibusproject.controller.model.put.AvailabilityPUT;
import it.polito.ai.pedibusproject.database.model.*;
import it.polito.ai.pedibusproject.exceptions.ForbiddenException;
import it.polito.ai.pedibusproject.security.JwtTokenProvider;
import it.polito.ai.pedibusproject.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/rest/availabilities")
public class AvailabilityController {
    private AvailabilityService availabilityService;
    private JwtTokenProvider jwtTokenProvider;
    private BusRideService busRideService;
    private UserService userService;
    private MessageService messageService;
    private LineService lineService;


    @Autowired
    public AvailabilityController(AvailabilityService availabilityService,
                                  JwtTokenProvider jwtTokenProvider,
                                  BusRideService busRideService,
                                  UserService userService,MessageService messageService,
                                  LineService lineService) {
        this.availabilityService = availabilityService;
        this.jwtTokenProvider=jwtTokenProvider;
        this.busRideService=busRideService;
        this.userService=userService;
        this.messageService=messageService;
        this.lineService=lineService;
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
        List roles=jwtTokenProvider.getRoles(jwtToken);
        String username=jwtTokenProvider.getUsername(jwtToken);
        Availability temp=this.availabilityService.findById(idAvailability);

        if(roles.contains(Role.ROLE_SYS_ADMIN))
            return new AvailabilityGET(temp,busRideService,lineService);
        if(roles.contains(Role.ROLE_ADMIN)&&
                userService.isAdminOfLine(username,
                        busRideService.findById(temp.getIdBusRide()).getIdLine()))
            return new AvailabilityGET(temp,busRideService,lineService);

        if(roles.contains(Role.ROLE_ESCORT)&&temp.getIdUser().equals(username))
            return new AvailabilityGET(temp,busRideService,lineService);

        throw new ForbiddenException();
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
        String username=jwtTokenProvider.getUsername(jwtToken);

        return new AvailabilityGET(
                this.availabilityService.create(availabilityPOST.getIdBusRide(),
                        availabilityPOST.getIdStopBus(),username, availabilityPOST.getState())
                ,busRideService,lineService
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
        List<Role> roles=jwtTokenProvider.getRoles(jwtToken);
        String username=jwtTokenProvider.getUsername(jwtToken);

        if(roles.contains(Role.ROLE_SYS_ADMIN))
            return new AvailabilityGET(
                    this.availabilityService.update(username,roles,idAvailability,
                            availabilityPUT.getIdStopBus(), availabilityPUT.getState())
                    ,busRideService,lineService
            );

        if(roles.contains(Role.ROLE_ADMIN)&& userService.isAdminOfLine(username,
                busRideService.findById(availabilityService.findById(idAvailability).getIdBusRide()).getIdLine()))
            return new AvailabilityGET(
                    this.availabilityService.update(username,roles,idAvailability,
                            availabilityPUT.getIdStopBus(), availabilityPUT.getState())
                    ,busRideService,lineService
            );

        if(roles.contains(Role.ROLE_ESCORT)&&availabilityService
                .findById(idAvailability).getIdUser().equals(username))
            return new AvailabilityGET(
                    this.availabilityService.update(username,roles,idAvailability,
                            availabilityPUT.getIdStopBus(), availabilityPUT.getState())
                    ,busRideService,lineService
            );

        throw new ForbiddenException();
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
        List roles=jwtTokenProvider.getRoles(jwtToken);
        String username=jwtTokenProvider.getUsername(jwtToken);
        Availability av=availabilityService.findById(idAvailability);
        BusRide br=busRideService.findById(av.getIdBusRide());


        if(roles.contains(Role.ROLE_SYS_ADMIN)){
            this.messageService.create(username,av.getIdUser(),"Disponibiltà Cancellata",
                    "La sua disponibilità ("+av+") è stata cancellata.",
                    (new Date()).getTime());
            this.availabilityService.deleteById(idAvailability);
            return;
        }

        if(roles.contains(Role.ROLE_ADMIN)&& userService.isAdminOfLine(username, br.getIdLine())) {
            this.messageService.create(username,av.getIdUser(),"Disponibiltà Cancellata",
                    "La sua disponibilità ("+av+") è stata cancellata.",
                    (new Date()).getTime());
            this.availabilityService.deleteById(idAvailability);
            return;
        }


        if(roles.contains(Role.ROLE_ESCORT)&&av.getIdUser().equals(username)) {
            if(av.getState()==AvailabilityState.Confirmed)
                throw new ForbiddenException();
            Set<User> usersTo=userService.findByRole(Role.ROLE_SYS_ADMIN);
            usersTo.addAll(userService.findByIdLine(br.getIdLine()));

            usersTo.forEach(y->
                    this.messageService.create(username,y.getUsername(),"Disponibiltà Cancellata",
                    "La disponibilità di "+av.getIdUser()+" per la corsa '"+
                            br.getStopBuses().first().getName()+" -> "+br.getStopBuses().last().getName()
                            +" "+br.getStartTime()+"', è stata cancellata.",
                    (new Date()).getTime()));
            this.availabilityService.deleteById(idAvailability);
            return;
        }

        throw new ForbiddenException();
    }

}
