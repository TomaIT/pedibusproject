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
    private Boolean isDeleted=false;

    public Child(String idUser, String firstname, String surname, Date birth, Gender gender, String blobBase64, String idStopBusOutDef, String idStopBusRetDef){
        this.idUser=idUser;
        this.firstname=firstname;
        this.surname=surname;
        this.birth=birth;
        this.gender=gender;
        this.blobBase64=blobBase64;
        this.idStopBusOutDef=idStopBusOutDef;
        this.idStopBusRetDef=idStopBusRetDef;
    }
}
