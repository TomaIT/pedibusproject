package it.polito.ai.pedibusproject.database.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "confirmationtokens")
public class ConfirmationToken {
    @Id
    private String id;
    private String email;
    private Long creationTime; //Epoch time
    @Indexed
    private UUID uuid;

    public ConfirmationToken(String email){
        this.email=email;
        this.creationTime=System.currentTimeMillis();
        this.uuid= UUID.randomUUID();
    }
}
