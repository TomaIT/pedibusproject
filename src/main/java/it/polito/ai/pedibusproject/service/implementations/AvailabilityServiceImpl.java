package it.polito.ai.pedibusproject.service.implementations;

import com.mongodb.client.result.UpdateResult;
import it.polito.ai.pedibusproject.database.model.*;
import it.polito.ai.pedibusproject.database.repository.AvailabilityRepository;
import it.polito.ai.pedibusproject.database.repository.BusRideRepository;
import it.polito.ai.pedibusproject.database.repository.LineRepository;
import it.polito.ai.pedibusproject.database.repository.UserRepository;
import it.polito.ai.pedibusproject.exceptions.*;
import it.polito.ai.pedibusproject.service.interfaces.AvailabilityService;
import it.polito.ai.pedibusproject.service.interfaces.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {
    private AvailabilityRepository availabilityRepository;
    private BusRideRepository busRideRepository;
    private UserRepository userRepository;
    private MongoTemplate mongoTemplate;
    private MessageService messageService;
    private LineRepository lineRepository;
    @Value("${spring.mail.username}")
    private String sysAdmin;
    @Value("${availability.time.expired.before.busride.start.seconds}")
    private long timeBeforeStartBusRideSec;

    @Autowired
    public AvailabilityServiceImpl(AvailabilityRepository availabilityRepository,
                                   BusRideRepository busRideRepository, UserRepository userRepository,
                                   MongoTemplate mongoTemplate, MessageService messageService,
                                   LineRepository lineRepository) {
        this.availabilityRepository = availabilityRepository;
        this.busRideRepository = busRideRepository;
        this.userRepository = userRepository;
        this.mongoTemplate=mongoTemplate;
        this.messageService=messageService;
        this.lineRepository=lineRepository;
    }

    private UpdateResult myUpdateFunctionFirst(String id, Update update, AvailabilityState oldState){
        Criteria criteria=new Criteria().andOperator(
                Criteria.where("_id").is(id).andOperator(
                        Criteria.where("state").is(oldState)
                )
        );
        Query query = new Query(criteria);
        return mongoTemplate.updateFirst(query, update, Availability.class);
    }

    @Override
    public Availability findById(String id) {
        return this.availabilityRepository.findById(id).orElseThrow(()->new NotFoundException("Availability"));
    }

    @Override
    public Availability create(String idBusRide, String idStopBus, String idUser, AvailabilityState state) {

        Optional<BusRide> br = this.busRideRepository.findById(idBusRide);

        if(!br.isPresent()) throw new BadRequestException("Availability <create> not found BusRide");

        if(br.get().getStartTime().getTime()-timeBeforeStartBusRideSec<=(new Date()).getTime())
            throw new BadRequestException("Availability <create> BusRide startTime is too close.");

        if(br.get().getStopBuses().stream().map(StopBus::getId).noneMatch(y -> y.equals(idStopBus)))
            throw new BadRequestException("Availability <create> not found StopBus in BusRide");

        if(!this.userRepository.existsById(idUser))
            throw new BadRequestException("Availability <create> not found User");

        if(!state.equals(AvailabilityState.Available))
            throw new BadRequestException("Availability <create> only Available state is accetable.");

        try {
            return this.availabilityRepository.insert(new Availability(idBusRide, idStopBus, idUser, state));
        }catch (org.springframework.dao.DuplicateKeyException e){
            throw new DuplicateKeyException("Availability <create>");
        }
    }


    private void sendMessage_I(BusRide br, Availability av) {
        Line line=this.lineRepository.findById(br.getIdLine())
                .orElseThrow(()->new InternalServerErrorException("Availability <update>"));


        Set<User> usersTo=userRepository.findAllByRolesContains(Role.ROLE_SYS_ADMIN);
        usersTo.addAll(userRepository.findAllByIdLinesContains(br.getIdLine()));


        usersTo.forEach(y->
            this.messageService.create(av.getIdUser(),y.getUsername(), "Disponibilità Confermata",
                    "La disponibilità di "+av.getIdUser()+" è stata confermata.\n"+
                    av.getMessage(br,line),
                    (new Date()).getTime())
        );


    }

    private void sendMessage_II(BusRide br,Availability av,String idAdmin) {
        Line line=this.lineRepository.findById(br.getIdLine())
                .orElseThrow(()->new InternalServerErrorException("Availability <update>"));
        this.messageService.create(idAdmin,av.getIdUser(), "Turno Chiuso",
                "La sua diponibilità è stata confermata definitivamente da: "+idAdmin+".\n"+
                        "In particolare sarà addetto alla corsa\n"+av.getMessage(br,line),
                (new Date()).getTime());
    }

    @Override
    public Availability update(String idUser, List<Role> roles, String id, String idStopBus, AvailabilityState state) {

        // AGGIUNTO I CONTROLLI SU idStopBus
        Optional<Availability> av = this.availabilityRepository.findById(id);
        if(!av.isPresent()) throw new NotFoundException("Availability <update> not exist");
        String idBusRide = av.get().getIdBusRide();
        Optional<BusRide> br = this.busRideRepository.findById(idBusRide);
        if(!br.isPresent()) throw new BadRequestException("Availability <update> not found BusRide");
        if(br.get().getStopBuses().stream().map(StopBus::getId).noneMatch(y -> y.equals(idStopBus)))
            throw new BadRequestException("Availability <update> not found StopBus in BusRide");
        if(br.get().getStartTime().getTime()-timeBeforeStartBusRideSec<=(new Date()).getTime())
            throw new BadRequestException("Availability <update> BusRide startTime is too close.");
        // AGGIUNTO I CONTROLLI SU idStopBus FINE


        Update update = new Update();
        update.set("idStopBus", idStopBus);
        update.set("state", state);

        UpdateResult updateResult;
        switch (state){
            case Available:
                if(!(roles.contains(Role.ROLE_SYS_ADMIN)||roles.contains(Role.ROLE_ADMIN)))
                    throw new ForbiddenException();
                updateResult=myUpdateFunctionFirst(id,update,AvailabilityState.Confirmed);
                break;
            case Checked:
                if(!(roles.contains(Role.ROLE_SYS_ADMIN)||roles.contains(Role.ROLE_ADMIN)))
                    throw new ForbiddenException();
                updateResult=myUpdateFunctionFirst(id,update,AvailabilityState.Available);
                break;
            case ReadChecked:
                if(!roles.contains(Role.ROLE_ESCORT))
                    throw new ForbiddenException();
                updateResult=myUpdateFunctionFirst(id,update,AvailabilityState.Checked);
                break;
            case Confirmed:
                if(!(roles.contains(Role.ROLE_SYS_ADMIN)||roles.contains(Role.ROLE_ADMIN)))
                    throw new ForbiddenException();
                updateResult=myUpdateFunctionFirst(id,update,AvailabilityState.ReadChecked);
                break;
            default:
                throw new BadRequestException("Availability <update> state is wrong.");
        }

        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("Availability <update> or oldState is wrong");


        if(state.equals(AvailabilityState.ReadChecked)){
            sendMessage_I(br.get(),av.get());
        }
        if(state.equals(AvailabilityState.Confirmed)){
            sendMessage_II(br.get(),av.get(),idUser);
        }


        return this.availabilityRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Availability <update>"));
    }

    @Override
    public void deleteById(String id) {
        this.availabilityRepository.deleteById(id);
    }

    @Override
    public Set<Availability> findAllByIdBusRide(String idBusRide) {
        return this.availabilityRepository.findAllByIdBusRide(idBusRide);
    }

    @Override
    public Set<Availability> findAllByIdUser(String idUser) {
        return this.availabilityRepository.findAllByIdUser(idUser);
    }
}
