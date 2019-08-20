package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.get.MessageGET;
import it.polito.ai.pedibusproject.controller.model.post.MessagePOST;
import it.polito.ai.pedibusproject.controller.model.put.MessagePUT;
import it.polito.ai.pedibusproject.database.model.Message;
import it.polito.ai.pedibusproject.exceptions.ForbiddenException;
import it.polito.ai.pedibusproject.security.JwtTokenProvider;
import it.polito.ai.pedibusproject.service.interfaces.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/rest/messages")
public class MessageController {
    private MessageService messageService;
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public MessageController(MessageService messageService,JwtTokenProvider jwtTokenProvider){
        this.messageService=messageService;
        this.jwtTokenProvider=jwtTokenProvider;
    }

    private void checkUserIsProprietary(String jwtToken,String f,String t){
        String username=jwtTokenProvider.getUsername(jwtToken);
        if(!f.equals(username)&&!t.equals(username))
            throw new ForbiddenException();
    }


    @GetMapping(value = "/{idMessage}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tale message")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public MessageGET getMessageById(@RequestHeader (name="Authorization") String jwtToken,
                                     @PathVariable("idMessage")String idMessage) {
        MessageGET temp=new MessageGET(
                this.messageService.findById(idMessage)
        );
        checkUserIsProprietary(jwtToken,temp.getIdUserFrom(),temp.getIdUserTo());
        return temp;
    }

    @PostMapping(value = "",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Crea nuovo messaggio")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public MessageGET postMessage(@RequestHeader (name="Authorization") String jwtToken,
                               @RequestBody @Valid MessagePOST messagePOST) {
        return new MessageGET(
                this.messageService.create(jwtTokenProvider.getUsername(jwtToken),messagePOST.getIdUserTo(),
                messagePOST.getSubject(),messagePOST.getMessage(),(new Date()).getTime())
        );
    }

    @PutMapping(value = "/{idMessage}",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Aggiorna messaggio per la conferma di lettura")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public MessageGET putMessage(@RequestHeader (name="Authorization") String jwtToken,
                              @PathVariable("idMessage")String idMessage,
                              @RequestBody @Valid MessagePUT messagePUT) {
        Message temp=messageService.findById(idMessage);
        if(!jwtTokenProvider.getUsername(jwtToken).equals(temp.getIdUserTo()))
            throw new ForbiddenException();
        return new MessageGET(
                this.messageService.updateReadConfirmById(idMessage,(new Date()).getTime())
        );
    }


    @DeleteMapping(value = "/{idMessage}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Cancella tale messaggio")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public void deleteMessageById(@RequestHeader (name="Authorization") String jwtToken,
                                  @PathVariable("idMessage")String idMessage) {
        Message temp=messageService.findById(idMessage);
        checkUserIsProprietary(jwtToken,temp.getIdUserFrom(),temp.getIdUserTo());
        this.messageService.deleteById(idMessage);
    }


}
