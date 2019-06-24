package it.polito.ai.pedibusproject.database.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
public class Message {
    @Id
    private String id;
    private String idUserFrom;
    private String idUserTo;
    private String subject;
    private Long readConfirm = null; //Epoch Time
    private String message;
    private Long creationTime; //Epoch Time

    public Message(String idUserFrom,String idUserTo,String subject,String message,Long creationTime){
        this.idUserFrom=idUserFrom;
        this.idUserTo=idUserTo;
        this.subject=subject;
        this.message=message;
        this.creationTime=creationTime;
    }

}
