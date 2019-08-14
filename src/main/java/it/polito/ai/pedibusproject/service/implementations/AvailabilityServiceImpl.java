package it.polito.ai.pedibusproject.service.implementations;

import com.mongodb.client.result.UpdateResult;
import it.polito.ai.pedibusproject.database.model.Availability;
import it.polito.ai.pedibusproject.database.model.AvailabilityState;
import it.polito.ai.pedibusproject.database.model.BusRide;
import it.polito.ai.pedibusproject.database.model.StopBus;
import it.polito.ai.pedibusproject.database.repository.AvailabilityRepository;
import it.polito.ai.pedibusproject.database.repository.BusRideRepository;
import it.polito.ai.pedibusproject.database.repository.UserRepository;
import it.polito.ai.pedibusproject.exceptions.BadRequestException;
import it.polito.ai.pedibusproject.exceptions.DuplicateKeyException;
import it.polito.ai.pedibusproject.exceptions.NotFoundException;
import it.polito.ai.pedibusproject.service.interfaces.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {
    private AvailabilityRepository availabilityRepository;
    private BusRideRepository busRideRepository;
    private UserRepository userRepository;
    private MongoTemplate mongoTemplate;
    @Value("${availability.time.expired.before.busride.start.seconds}")
    private long timeBeforeStartBusRideSec;

    @Autowired
    public AvailabilityServiceImpl(AvailabilityRepository availabilityRepository,
                                   BusRideRepository busRideRepository, UserRepository userRepository,
                                   MongoTemplate mongoTemplate) {
        this.availabilityRepository = availabilityRepository;
        this.busRideRepository = busRideRepository;
        this.userRepository = userRepository;
        this.mongoTemplate=mongoTemplate;
    }

    private UpdateResult myUpdateFunctionFirst(String id, Update update){
        Criteria criteria=new Criteria().andOperator(
                Criteria.where("_id").is(id)
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

        try {
            return this.availabilityRepository.insert(new Availability(idBusRide, idStopBus, idUser, state));
        }catch (org.springframework.dao.DuplicateKeyException e){
            throw new DuplicateKeyException("Availability <create>");
        }
    }

    @Override
    public Availability update(String id, String idStopBus, AvailabilityState state) {

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
        UpdateResult updateResult=myUpdateFunctionFirst(id,update);
        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("Availability <update>");
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
