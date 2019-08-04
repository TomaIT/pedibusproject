package it.polito.ai.pedibusproject.controller.model.get;

import it.polito.ai.pedibusproject.database.model.Message;
import lombok.Data;

@Data
public class MessageGET {
    private String id;
    private String idUserFrom;
    private String idUserTo;
    private String subject;
    private Long readConfirm; //Epoch Time
    private String message;
    private Long creationTime; //Epoch Time

    public MessageGET(Message message){
        this.id=message.getId();
        this.idUserFrom=message.getIdUserFrom();
        this.idUserTo=message.getIdUserTo();
        this.subject=message.getSubject();
        this.readConfirm=message.getReadConfirm();
        this.message=message.getMessage();
        this.creationTime=message.getCreationTime();
    }
}
