package it.polito.ai.pedibusproject.controller.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.ai.pedibusproject.controller.model.post.MessagePOST;
import it.polito.ai.pedibusproject.controller.model.put.MessagePUT;
import it.polito.ai.pedibusproject.database.model.Message;
import it.polito.ai.pedibusproject.exceptions.NotImplementedException;
import it.polito.ai.pedibusproject.service.interfaces.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/rest/messages")
public class MessageController {
    private MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService){
        this.messageService=messageService;
    }

    @GetMapping(value = "/{idMessage}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Ritorna tale message")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Message getMessageById(@RequestHeader (name="Authorization") String jwtToken,
                                  @PathVariable("idMessage")String idMessage) {
        return this.messageService.findById(idMessage);
    }

    @PostMapping(value = "",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Crea nuovo messaggio")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Message postMessage(@RequestHeader (name="Authorization") String jwtToken,
                               @RequestBody @Valid MessagePOST messagePOST) {
        //TODO userFrom
        return this.messageService.create("TODO@GMAIL.COM",messagePOST.getIdUserTo(),
                messagePOST.getSubject(),messagePOST.getMessage(),messagePOST.getCreationTime());
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
    public Message putMessage(@RequestHeader (name="Authorization") String jwtToken,
                              @PathVariable("idMessage")String idMessage,
                              @RequestBody @Valid MessagePUT messagePUT) {
        return this.messageService.updateReadConfirmById(idMessage,messagePUT.getReadConfirm());
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
        this.messageService.deleteById(idMessage);
    }


}
