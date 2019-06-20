package it.polito.ai.pedibusproject.utility;

import it.polito.ai.pedibusproject.database.model.*;
import it.polito.ai.pedibusproject.exceptions.NotFoundException;
import it.polito.ai.pedibusproject.service.interfaces.ConfirmationTokenService;
import it.polito.ai.pedibusproject.service.interfaces.LineService;
import it.polito.ai.pedibusproject.service.interfaces.StopBusService;
import it.polito.ai.pedibusproject.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Component
public class LoaderLine {
    private static final Logger LOG = LoggerFactory.getLogger(LoaderLine.class);
    private LineService lineService;
    private StopBusService stopBusService;
    @Value("${lines.folder.path}")
    private String folderLines;
    private UserService userService;
    private ConfirmationTokenService confirmationTokenService;


    @Autowired
    public LoaderLine(LineService lineService,StopBusService stopBusService,
                      ConfirmationTokenService confirmationTokenService,UserService userService) {
        this.lineService=lineService;
        this.stopBusService=stopBusService;
        this.userService=userService;
        this.confirmationTokenService=confirmationTokenService;
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


    private void updateUserState(String email,String idLine){
        User user;
        try{
            user=this.userService.loadUserByUsername(email);
        }catch (NotFoundException e){
            Set<Role> temp = new HashSet<>();
            temp.add(Role.ROLE_ADMIN);
            user=this.userService.create(email,temp);
            this.userService.addLine(email,idLine);
            this.confirmationTokenService.create(email);
            return;
        }
        if(!user.getIdLines().contains(idLine)){
            this.userService.addLine(email,idLine);
        }
        //Check registration uuid is not expired
        if(!user.isEnabled()){
            Optional<ConfirmationToken> temp=this.confirmationTokenService
                    .findByEmail(email);
            if(!temp.isPresent()||this.confirmationTokenService.isExpired(temp.get())){
                this.confirmationTokenService.create(email);
            }
        }
    }

    public Line createLine(InputDataLine inputDataLine,Long creationTime){
        Line temp=new Line();
        temp.setCreationTime(creationTime);
        temp.setEmailAdmin(inputDataLine.getEmailAdmin());
        temp.setName(inputDataLine.getName());
        temp.setIdOutStopBuses(createStopBuses(inputDataLine.getOutwardLine(),StopBusType.Outward));
        temp.setIdRetStopBuses(createStopBuses(inputDataLine.getReturnLine(),StopBusType.Return));
        temp=this.lineService.create(temp);
        updateUserState(inputDataLine.getEmailAdmin(),temp.getId());
        return temp;
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
                            LOG.info("Update Line " +
                                    createLine(inputDataLine, file.lastModified()).getName());
                        }
                    }else{//Create
                        LOG.info("Create Line " +
                                createLine(inputDataLine,file.lastModified()).getName());
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
