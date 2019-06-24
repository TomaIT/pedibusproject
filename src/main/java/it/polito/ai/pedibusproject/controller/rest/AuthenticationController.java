package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.post.LoginPOST;
import it.polito.ai.pedibusproject.controller.model.post.RecoverPOST;
import it.polito.ai.pedibusproject.exceptions.NotImplementedException;
import it.polito.ai.pedibusproject.service.interfaces.RecoveryTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/rest/authentications")
public class AuthenticationController {
    private RecoveryTokenService recoveryTokenService;

    @Autowired
    public AuthenticationController(RecoveryTokenService recoveryTokenService){
        this.recoveryTokenService=recoveryTokenService;
    }

    @PostMapping(value = "/login",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value={
            @ApiResponse(code = 401,message = "Unauthorized"),
            @ApiResponse(code = 500,message = "Internal Server Error")
    })
    public ResponseEntity postLogin(@RequestBody @Valid LoginPOST loginPOST){

        //TODO
        throw new NotImplementedException();
    }

    @PostMapping("/recover")
    public void postRecover(@RequestBody @Valid RecoverPOST recoverPOST){
        this.recoveryTokenService.create(recoverPOST.getEmail());
    }
}
