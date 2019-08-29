package it.polito.ai.pedibusproject.service.implementations;

import com.mongodb.client.result.UpdateResult;
import it.polito.ai.pedibusproject.database.model.*;
import it.polito.ai.pedibusproject.database.repository.BusRideRepository;
import it.polito.ai.pedibusproject.database.repository.ChildRepository;
import it.polito.ai.pedibusproject.database.repository.ReservationRepository;
import it.polito.ai.pedibusproject.database.repository.StopBusRepository;
import it.polito.ai.pedibusproject.exceptions.BadRequestException;
import it.polito.ai.pedibusproject.exceptions.DuplicateKeyException;
import it.polito.ai.pedibusproject.exceptions.NotFoundException;
import it.polito.ai.pedibusproject.service.interfaces.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    public Set<Reservation> findAllByIdBusRideAndIdStopBus(String idBusRide, String idStopBus) {
        return reservationRepository.findAllByIdBusRideAndIdStopBus(idBusRide,idStopBus);
    }

    @Override
    public Set<Reservation> findAllByIdBusRideAndIdChild(String idBusRide, String idChild) {
        return reservationRepository.findAllByIdBusRideAndIdChild(idBusRide, idChild);
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
        /*if(!busRideRepository.existsById(idBusRide))
            throw new BadRequestException("Reservation <create> idBusRide not found");
        if(!stopBusRepository.existsById(idStopBus))
            throw new BadRequestException("Reservation <create> idStopBus not found");*/
        Optional<BusRide> br = this.busRideRepository.findById(idBusRide);
        if(!br.isPresent())
            throw new BadRequestException("Reservation <create> idBusRide not found");
        if(!br.get().getStopBuses().stream().map(StopBus::getId).anyMatch(y -> y.equals(idStopBus)))
            throw new BadRequestException("Reservation <create> idStopBus not found in BusRide");

        if(br.get().getStartTime().getTime()<=(new Date()).getTime())
            throw new BadRequestException("Reservation <create> BusRide startTime has already passed.");
        //Fine controlli
        Reservation temp=new Reservation(idBusRide,idChild,idStopBus,idUser);
        try {
            return this.reservationRepository.insert(temp);
        }catch (org.springframework.dao.DuplicateKeyException e){
            throw new DuplicateKeyException("Reservation <create> already exist");
        }
    }

    @Override
    public Reservation create(Reservation reservation) {
        //Controlli
        if(!childRepository.existsById(reservation.getIdChild()))
            throw new BadRequestException("Reservation <create> idChild not found");
        /*if(!busRideRepository.existsById(idBusRide))
            throw new BadRequestException("Reservation <create> idBusRide not found");
        if(!stopBusRepository.existsById(idStopBus))
            throw new BadRequestException("Reservation <create> idStopBus not found");*/
        Optional<BusRide> br = this.busRideRepository.findById(reservation.getIdBusRide());
        if(!br.isPresent())
            throw new BadRequestException("Reservation <create> idBusRide not found");
        if(!br.get().getStopBuses().stream().map(StopBus::getId).anyMatch(y -> y.equals(reservation.getIdStopBus())))
            throw new BadRequestException("Reservation <create> idStopBus not found in BusRide");

        /*if(br.get().getStartTime().getTime()<=(new Date()).getTime())
            throw new BadRequestException("Reservation <create> BusRide startTime has already passed.");*/
        if(br.get().getIdLastStopBus() != null) {
            List<String> idStobBuses = br.get().getStopBuses().stream().map(StopBus::getId).collect(Collectors.toList());
            if (idStobBuses.indexOf(reservation.getIdStopBus()) <= idStobBuses.indexOf(br.get().getIdLastStopBus()))
                throw new BadRequestException("Reservation <create> Pedibus has already passed by this StopBus");
        }
        //Fine controlli

        try {
            return this.reservationRepository.insert(reservation);
        }catch (org.springframework.dao.DuplicateKeyException e){
            throw new DuplicateKeyException("Reservation <create> already exist");
        }
    }

    @Override
    public Reservation updateGetIn(String id,ReservationState reservationState) {
        Optional<Reservation> r = this.reservationRepository.findById(id);
        if(!r.isPresent())
            throw new BadRequestException("Reservation <updateGetIn> Reservation not found");
        Optional<BusRide> br = this.busRideRepository.findById(r.get().getIdBusRide());
        if(!br.isPresent())
            throw new BadRequestException("Reservation <updateGetIn> idBusRide not found");
        if(!br.get().getStopBuses().stream().map(StopBus::getId).anyMatch(x -> x.equals(reservationState.getIdStopBus())))
            throw new BadRequestException("Reservation <updateGetIn> idStopBus not found in BusRide");

        Update update = new Update();
        update.set("getIn", reservationState);
        update.set("absent", null);
        UpdateResult updateResult=myUpdateFunctionFirst(id,update);
        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("Reservation <updateGetIn>");
        return this.reservationRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Reservation <updateGetIn>"));
    }

    @Override
    public Reservation updateGetIn(String id, String idBusRide, String idStopBus,
                                   String idUser, ReservationState reservationState) {
        Optional<BusRide> br = this.busRideRepository.findById(idBusRide);
        if(!br.isPresent())
            throw new BadRequestException("Reservation <updateGetIn> idBusRide not found");
        if(br.get().getStopBuses().stream().map(StopBus::getId)
                .noneMatch(x -> x.equals(reservationState.getIdStopBus())))
            throw new BadRequestException("Reservation <updateGetIn> idStopBus not found in BusRide");
        if(!br.get().getStopBusType().equals(StopBusType.Outward))
            throw new BadRequestException("Reservation <updateGetIn> new idBusRide not is Outward");
        if(br.get().getStopBuses().stream().map(StopBus::getId)
                .noneMatch(x -> x.equals(idStopBus)))
            throw new BadRequestException("Reservation <updateGetIn> idStopBus not found in BusRide");



        Update update = new Update();
        update.set("getIn", reservationState);
        update.set("absent", null);
        update.set("idBusRide", idBusRide);
        update.set("idStopBus", idStopBus);
        update.set("idUser", idUser);


        //TODO: dubbio che .is(null) sia giusto...
        Criteria criteria=new Criteria().andOperator(
                Criteria.where("_id").is(id).andOperator(Criteria.where("getIn").is(null)));
        Query query = new Query(criteria);
        UpdateResult updateResult=mongoTemplate.updateFirst(query, update, Reservation.class);

        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("Reservation <updateGetIn>");
        return this.reservationRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Reservation <updateGetIn>"));
    }

    @Override
    public Reservation updateGetOut(String id,ReservationState reservationState) {
        Optional<Reservation> r = this.reservationRepository.findById(id);
        if(!r.isPresent())
            throw new BadRequestException("Reservation <updateGetOut> Reservation not found");
        Optional<BusRide> br = this.busRideRepository.findById(r.get().getIdBusRide());
        if(!br.isPresent())
            throw new BadRequestException("Reservation <updateGetOut> idBusRide not found");
        if(!br.get().getStopBuses().stream().map(StopBus::getId).anyMatch(x -> x.equals(reservationState.getIdStopBus())))
            throw new BadRequestException("Reservation <updateGetOut> idStopBus not found in BusRide");

        Update update = new Update();
        update.set("getOut", reservationState);
        UpdateResult updateResult=myUpdateFunctionFirst(id,update);
        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("Reservation <updateGetOut>");
        return this.reservationRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Reservation <updateGetOut>"));
    }

    @Override
    public Reservation updateAbsent(String id,ReservationState reservationState) {
        Optional<Reservation> r = this.reservationRepository.findById(id);
        if(!r.isPresent())
            throw new BadRequestException("Reservation <updateAbsent> Reservation not found");
        Optional<BusRide> br = this.busRideRepository.findById(r.get().getIdBusRide());
        if(!br.isPresent())
            throw new BadRequestException("Reservation <updateAbsent> idBusRide not found");
        if(!br.get().getStopBuses().stream().map(StopBus::getId).anyMatch(x -> x.equals(reservationState.getIdStopBus())))
            throw new BadRequestException("Reservation <updateAbsent> idStopBus not found in BusRide");

        Update update = new Update();
        update.set("absent", reservationState);
        update.set("getIn", null);
        UpdateResult updateResult=myUpdateFunctionFirst(id,update);
        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("Reservation <updateAbsent>");
        return this.reservationRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Reservation <updateAbsent>"));
    }

    @Override
    public void deleteById(String id) {
        this.reservationRepository.deleteById(id);
    }
}
