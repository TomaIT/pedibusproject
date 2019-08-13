package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.get.UserGET;
import it.polito.ai.pedibusproject.database.model.Role;
import it.polito.ai.pedibusproject.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/rest/roles")
public class RoleController {
    private UserService userService;

    @Autowired
    public RoleController(UserService userService) {
        this.userService=userService;
    }

    @GetMapping(value = "",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna Lista Possibili Valori di Role")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<String> getRoleValues(@RequestHeader (name="Authorization") String jwtToken) {
        return Arrays.stream(Role.values()).map(Enum::name).collect(Collectors.toSet());
    }

    @GetMapping(value = "/{role}/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna utenti con ruolo specificato (role)")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Set<UserGET> getLine(@RequestHeader (name="Authorization") String jwtToken,
                                @PathVariable("role") Role role) {
        return this.userService.findByRole(role).stream()
                .map(UserGET::new).collect(Collectors.toSet());
    }
}
