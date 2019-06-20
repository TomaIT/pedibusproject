package it.polito.ai.pedibusproject.utility;

import it.polito.ai.pedibusproject.PedibusprojectApplication;
import it.polito.ai.pedibusproject.database.model.Line;
import it.polito.ai.pedibusproject.database.model.StopBus;
import it.polito.ai.pedibusproject.database.model.StopBusType;
import it.polito.ai.pedibusproject.service.interfaces.LineService;
import it.polito.ai.pedibusproject.service.interfaces.StopBusService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@Component
public class LoaderLine {
    private static final Logger LOG = LoggerFactory.getLogger(LoaderLine.class);
    private LineService lineService;
    private StopBusService stopBusService;
    @Value("${lines.folder.path}")
    private String folderLines;

    @Autowired
    public LoaderLine(LineService lineService,StopBusService stopBusService){
        this.lineService=lineService;
        this.stopBusService=stopBusService;
    }


    public List<String> createStopBuses(List<InputDataStopBus> stopBuses, StopBusType stopBusType) {
        List<String> ret = new ArrayList<>();
        for (InputDataStopBus i : stopBuses) {
            StopBus stopBus = new StopBus();
            stopBus.setHours(i.getTime());
            stopBus.setLocation(i.getLon(),i.getLat());
            stopBus.setName(i.getDescription());
            stopBus.setStopBusType(stopBusType);
            ret.add(this.stopBusService.insert(stopBus).getId());
            LOG.info("Create Stop Bus "+i.getDescription());
        }
        return ret;
    }

    public Line createLine(InputDataLine inputDataLine,Long creationTime){
        Line temp=new Line();
        temp.setCreationTime(creationTime);
        //TODO Send email ??
        temp.setEmailAdmin(inputDataLine.getEmailAdmin());
        temp.setName(inputDataLine.getName());
        temp.setIdOutStopBuses(createStopBuses(inputDataLine.getOutwardLine(),StopBusType.Outward));
        temp.setIdRetStopBuses(createStopBuses(inputDataLine.getReturnLine(),StopBusType.Return));
        return this.lineService.create(temp);
    }

    public void updateLines(){
        Set<Line> lines=this.lineService.findAll();
        File folder = new File(folderLines);
        if (folder.exists()) {
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                try {
                    InputDataLine inputDataLine = InputDataLine.loadData(file);
                    Optional<Line> line = lines.stream()
                            .filter(x->x.getName().equals(inputDataLine.getName())).findAny();
                    if(line.isPresent()){
                        if(!line.get().getCreationTime().equals(file.lastModified())) {//Update
                            this.lineService.deleteById(line.get().getId());
                            LOG.info("Update Line " + createLine(inputDataLine, file.lastModified()).getName());
                        }
                    }else{//Create
                        createLine(inputDataLine,file.lastModified());
                        LOG.info("Create Line " + inputDataLine.getName());
                    }
                }catch (IOException e){
                    LOG.error("File "+file.getName(),e);
                }
            }
        } else {
            LOG.warn("Folder " + folderLines + " not exists");
        }

    }
}
