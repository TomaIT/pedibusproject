package it.polito.ai.pedibusproject.controller.model.get;

import it.polito.ai.pedibusproject.database.model.Line;
import it.polito.ai.pedibusproject.database.model.StopBus;
import it.polito.ai.pedibusproject.exceptions.InternalServerErrorException;
import it.polito.ai.pedibusproject.service.interfaces.LineService;
import lombok.Data;

import java.util.TreeSet;

@Data
public class LineGET {
    private String id;
    private String name;
    private Boolean isDeleted;
    private String emailAdmin;
    private TreeSet<StopBusGET> outStopBuses = new TreeSet<>();
    private TreeSet<StopBusGET> retStopBuses = new TreeSet<>();
    private Long creationTime; //Epoch time (lastModified() of configuration file)
    private Long deletedTime; //Epoch time

    public LineGET(Line line){
        this.id=line.getId();
        this.name=line.getName();
        this.isDeleted=line.getIsDeleted();
        this.emailAdmin=line.getEmailAdmin();
        this.creationTime=line.getCreationTime();
        this.deletedTime=line.getDeletedTime();
    }

    public void addStopBus(StopBus stopBus, LineService lineService){
        StopBusGET temp=new StopBusGET(stopBus,lineService);
        switch (temp.getStopBusType()){
            case Outward:
                this.outStopBuses.add(temp);
                return;
            case Return:
                this.retStopBuses.add(temp);
                return;
            default:
                throw new InternalServerErrorException(temp.getStopBusType()+"not recognized");
        }
    }
}
