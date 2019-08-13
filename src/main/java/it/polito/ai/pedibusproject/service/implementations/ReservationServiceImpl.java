package it.polito.ai.pedibusproject.service.implementations;

import com.mongodb.client.result.UpdateResult;
import it.polito.ai.pedibusproject.database.model.Reservation;
import it.polito.ai.pedibusproject.database.model.ReservationState;
import it.polito.ai.pedibusproject.database.model.User;
import it.polito.ai.pedibusproject.database.repository.BusRideRepository;
import it.polito.ai.pedibusproject.database.repository.ChildRepository;
import it.polito.ai.pedibusproject.database.repository.ReservationRepository;
import it.polito.ai.pedibusproject.database.repository.StopBusRepository;
import it.polito.ai.pedibusproject.exceptions.BadRequestException;
import it.polito.ai.pedibusproject.exceptions.NotFoundException;
import it.polito.ai.pedibusproject.service.interfaces.BusRideService;
import it.polito.ai.pedibusproject.service.interfaces.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ReservationServiceImpl implements ReservationService {
    private ReservationRepository reservationRepository;
    private BusRideRepository busRideRepository;
    private ChildRepository childRepository;
    private StopBusRepository stopBusRepository;
    private MongoTemplate mongoTemplate;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  BusRideRepository busRideRepository,
                                  ChildRepository childRepository,
                                  StopBusRepository stopBusRepository,
                                  MongoTemplate mongoTemplate){
        this.reservationRepository=reservationRepository;
        this.busRideRepository=busRideRepository;
        this.childRepository=childRepository;
        this.stopBusRepository=stopBusRepository;
        this.mongoTemplate=mongoTemplate;
    }

    private UpdateResult myUpdateFunctionFirst(String id, Update update){
        Criteria criteria=new Criteria().andOperator(
                Criteria.where("_id").is(id)
        );
        Query query = new Query(criteria);
        return mongoTemplate.updateFirst(query, update, Reservation.class);
    }

    @Override
    public Set<Reservation> findAllByIdBusRide(String idBusRide) {
        return this.reservationRepository.findAllByIdBusRide(idBusRide);
    }

    @Override
    public Set<Reservation> findAllByIdChild(String idChild) {
        return this.reservationRepository.findAllByIdChild(idChild);
    }

    @Override
    public Set<Reservation> findAllByIdUser(String idUser) {
        return reservationRepository.findAllByIdUser(idUser);
    }

    @Override
    public Reservation findById(String id) {
        return this.reservationRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Reservation <findById>"));
    }

    @Override
    public Reservation create(String idBusRide, String idChild, String idStopBus, String idUser) {
        //Controlli
        if(!childRepository.existsById(idChild))
            throw new BadRequestException("Reservation <create> idChild not found");
        if(!busRideRepository.existsById(idBusRide))
            throw new BadRequestException("Reservation <create> idBusRide not found");
        if(!stopBusRepository.existsById(idStopBus))
            throw new BadRequestException("Reservation <create> idStopBus not found");
        //Fine controlli
        Reservation temp=new Reservation(idBusRide,idChild,idStopBus,idUser);
        return this.reservationRepository.insert(temp);
    }

    @Override
    public Reservation updateGetIn(String id,ReservationState reservationState) {
        Update update = new Update();
        update.set("getIn", reservationState);
        UpdateResult updateResult=myUpdateFunctionFirst(id,update);
        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("Reservation <updateGetIn>");
        return this.reservationRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Reservation <updateGetIn>"));
    }

    @Override
    public Reservation updateGetOut(String id,ReservationState reservationState) {
        Update update = new Update();
        update.set("getOut", reservationState);
        UpdateResult updateResult=myUpdateFunctionFirst(id,update);
        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("Reservation <updateGetOut>");
        return this.reservationRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Reservation <updateGetOut>"));
    }

    @Override
    public void deleteById(String id) {
        this.reservationRepository.deleteById(id);
    }
}
