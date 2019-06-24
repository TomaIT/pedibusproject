package it.polito.ai.pedibusproject.controller.model.post;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class MessagePOST {
    //private String idUserFrom; //jwt
    @NotNull
    @Size(max = 1024)
    private String idUserTo;
    @NotNull
    @Size(max = 1024)
    private String subject;
    @NotNull
    @Size(max = 8192)
    private String message;
    @NotNull
    private Long creationTime; //Epoch Time
}
