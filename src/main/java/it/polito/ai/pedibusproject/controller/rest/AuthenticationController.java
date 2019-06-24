package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.get.LoginGET;
import it.polito.ai.pedibusproject.controller.model.post.LoginPOST;
import it.polito.ai.pedibusproject.controller.model.post.RecoverPOST;
import it.polito.ai.pedibusproject.exceptions.NotImplementedException;
import it.polito.ai.pedibusproject.exceptions.UnauthorizedException;
import it.polito.ai.pedibusproject.security.JwtTokenProvider;
import it.polito.ai.pedibusproject.service.interfaces.RecoveryTokenService;
import it.polito.ai.pedibusproject.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/rest/authentications")
public class AuthenticationController {
    private RecoveryTokenService recoveryTokenService;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private UserService userService;

    @Autowired
    public AuthenticationController(RecoveryTokenService recoveryTokenService,
                                    AuthenticationManager authenticationManager,
                                    JwtTokenProvider jwtTokenProvider,
                                    UserService userService){
        this.recoveryTokenService=recoveryTokenService;
        this.authenticationManager=authenticationManager;
        this.jwtTokenProvider=jwtTokenProvider;
        this.userService=userService;
    }

    @PostMapping(value = "/login",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Crea jwtToken per autenticarsi")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value={
            @ApiResponse(code = 401,message = "Unauthorized"),
            @ApiResponse(code = 500,message = "Internal Server Error")
    })
    public LoginGET postLogin(@RequestBody @Valid LoginPOST loginPOST){
        try {
            String email=loginPOST.getEmail();
            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, loginPOST.getPassword()));

            String token = jwtTokenProvider.createToken(email,
                    userService.loadUserByUsername(email).getRoles().stream().map(Enum::name).collect(Collectors.toList()));

            return new LoginGET(email,token);
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Username or Password invalid");
        }
    }

    @PostMapping(value = "/recover",consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Creai recoveryToken e invia il link all'interno dell'email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value={
            @ApiResponse(code = 404,message = "Not Found User"),
            @ApiResponse(code = 500,message = "Internal Server Error")
    })
    public void postRecover(@RequestBody @Valid RecoverPOST recoverPOST){
        this.recoveryTokenService.create(recoverPOST.getEmail());
    }
}
