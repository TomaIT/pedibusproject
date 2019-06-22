package it.polito.ai.pedibusproject.database.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "children")
public class Child {
    @Id
    private String id;
    private String idUser; //Parent (creator)
    private String firstname;
    private String surname;
    private Date birth;
    private Gender gender;
    private String blobBase64; //Photo ??
    private String idStopBusOutDef;
    private String idStopBusRetDef;
}
