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
@Document(collection = "recoverytokens")
public class RecoveryToken {
    @Id
    private String id;
    private User user;
    private Long creationTime; //Epoch time
    @Indexed
    private UUID uuid;
}
