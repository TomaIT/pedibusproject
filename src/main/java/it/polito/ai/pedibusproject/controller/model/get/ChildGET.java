package it.polito.ai.pedibusproject.controller.model.get;

import it.polito.ai.pedibusproject.database.model.Child;
import it.polito.ai.pedibusproject.database.model.Gender;
import it.polito.ai.pedibusproject.database.model.Line;
import it.polito.ai.pedibusproject.database.model.StopBus;
import it.polito.ai.pedibusproject.service.interfaces.LineService;
import it.polito.ai.pedibusproject.service.interfaces.StopBusService;
import lombok.Data;

import java.util.Date;

@Data
public class ChildGET {
    private String id;
    private String idUser; //Parent (creator)
    private String firstname;
    private String surname;
    private Date birth;
    private Gender gender;
    private String blobBase64; //Photo ??
    private String idStopBusOutDef;
    private StopBusGET stopBusOutDef;
    private String idStopBusRetDef;
    private StopBusGET stopBusRetDef;
    private Boolean isDeleted;

    public ChildGET(Child child, StopBusService stopBusService, LineService lineService){
        this.id=child.getId();
        this.idUser=child.getIdUser();
        this.firstname=child.getFirstname();
        this.surname=child.getSurname();
        this.birth=child.getBirth();
        this.gender=child.getGender();
        this.blobBase64=child.getBlobBase64();
        this.idStopBusOutDef=child.getIdStopBusOutDef();
        this.idStopBusRetDef=child.getIdStopBusRetDef();
        this.isDeleted=child.getIsDeleted();
        if(idStopBusOutDef!=null)
        this.stopBusOutDef=new StopBusGET(stopBusService.findById(idStopBusOutDef),lineService);
        if(idStopBusRetDef!=null)
        this.stopBusRetDef=new StopBusGET(stopBusService.findById(idStopBusRetDef),lineService);
    }
}
