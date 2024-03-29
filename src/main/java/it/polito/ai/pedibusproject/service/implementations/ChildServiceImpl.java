package it.polito.ai.pedibusproject.service.implementations;

import com.mongodb.client.result.UpdateResult;
import it.polito.ai.pedibusproject.database.model.*;
import it.polito.ai.pedibusproject.database.repository.*;
import it.polito.ai.pedibusproject.exceptions.BadRequestException;
import it.polito.ai.pedibusproject.exceptions.NotFoundException;
import it.polito.ai.pedibusproject.service.interfaces.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChildServiceImpl implements ChildService {
    private ChildRepository childRepository;
    private MongoTemplate mongoTemplate;
    private UserRepository userRepository;
    private StopBusRepository stopBusRepository;
    private ReservationRepository reservationRepository;
    private BusRideRepository busRideRepository;

    @Autowired
    public ChildServiceImpl(ChildRepository childRepository, MongoTemplate mongoTemplate,
                            UserRepository userRepository, StopBusRepository stopBusRepository,
                            ReservationRepository reservationRepository, BusRideRepository busRideRepository){
        this.childRepository=childRepository;
        this.mongoTemplate=mongoTemplate;
        this.userRepository=userRepository;
        this.stopBusRepository=stopBusRepository;
        this.reservationRepository=reservationRepository;
        this.busRideRepository=busRideRepository;
    }

    private UpdateResult myUpdateFunctionFirst(String id, Update update){
        Criteria criteria=new Criteria().andOperator(
                Criteria.where("_id").is(id)
        );
        Query query = new Query(criteria);
        return mongoTemplate.updateFirst(query, update, Child.class);
    }

    @Override
    public Child findById(String id) {
        return this.childRepository.findById(id).orElseThrow(()->new NotFoundException("Child"));
    }

    @Override
    public Set<Child> findByIdUser(String idUser) {
        return this.childRepository.findByIdUser(idUser);
    }

    @Override
    public Set<Child> findAllByIdStopBusOutDef(String idStopBusOutDef) {
        return this.childRepository.findAllByIdStopBusOutDef(idStopBusOutDef);
    }

    @Override
    public Set<Child> findAllByIdStopBusRetDef(String idStopBusRetDef) {
        return this.childRepository.findAllByIdStopBusRetDef(idStopBusRetDef);
    }

    @Override
    public Set<Child> findAllAvailableToBeTaken(String idBusRide, String idStopBus) {
        BusRide busRide=busRideRepository.findById(idBusRide).orElseThrow(()->new NotFoundException("BusRide <findAllChildrenAvailableToBeTaken>"));
        //Controllo sia Outward altrimenti BadRequest
        if(!busRide.getStopBusType().equals(StopBusType.Outward))
            throw new BadRequestException("Child <findAllAvailableToBeTaken> this methos is valid only to StopBusType.Outward");

        Set<BusRide> busRides=this.busRideRepository.findAllByStopBusTypeAndYearAndMonthAndDay(
                busRide.getStopBusType(),busRide.getYear(),busRide.getMonth(),busRide.getDay()).stream()
                .filter(x->!x.getId().equals(idBusRide)).collect(Collectors.toSet());


        Set<Child> children= new HashSet<>(childRepository.findAll());
        Set<Reservation> reservationsThisBusRide=reservationRepository.findAllByIdBusRide(idBusRide);
        Set<Reservation> reservationsAlreadyChecked=new HashSet<>();

        busRides.forEach(x->reservationsAlreadyChecked.addAll(reservationRepository.findAllByIdBusRideAndGetInIsNotNull(x.getId())));

        Set<String> childrenToRemove=new HashSet<>();
        childrenToRemove.addAll(reservationsThisBusRide.stream().map(Reservation::getIdChild).collect(Collectors.toSet()));
        childrenToRemove.addAll(reservationsAlreadyChecked.stream().map(Reservation::getIdChild).collect(Collectors.toSet()));

        return children.stream().filter(x->!childrenToRemove.contains(x.getId())).collect(Collectors.toSet());
    }

    private void checkIdStopsBus(String idStopBusOutDef, String idStopBusRetDef) {
        Optional<StopBus> sbOut = this.stopBusRepository.findById(idStopBusOutDef);
        if(!sbOut.isPresent())
            throw new BadRequestException("Child <create> not found StopBus with id=idStopBusOutDef");
        if(!sbOut.get().getStopBusType().equals(StopBusType.Outward))
            throw new BadRequestException("Child <create> StopBus with id=idStopBusOutDef is not of type OUTWARD");

        Optional<StopBus> sbRet = this.stopBusRepository.findById(idStopBusRetDef);
        if(!sbRet.isPresent())
            throw new BadRequestException("Child <create> not found StopBus with id=idStopBusRetDef");
        if(!sbRet.get().getStopBusType().equals(StopBusType.Return))
            throw new BadRequestException("Child <create> StopBus with id=idStopBusRetDef is not of type RETURN");
        // forse messaggi troppo dettagliati (troppi dettagli all'utente!?)
    }

    @Override
    public Child create(String idUser, String firstname, String surname, Date birth, Gender gender, String blobBase64, String idStopBusOutDef, String idStopBusRetDef) {
        if(!this.userRepository.existsById(idUser))
            throw new BadRequestException("Child <create> not found Parent");

        checkIdStopsBus(idStopBusOutDef, idStopBusRetDef);

        return this.childRepository.insert(new Child(idUser,firstname,surname,birth,gender,blobBase64,idStopBusOutDef,idStopBusRetDef));
    }

    @Override
    public Child update(String id, String firstname, String surname, Date birth, Gender gender, String blobBase64, String idStopBusOutDef, String idStopBusRetDef) {
        checkIdStopsBus(idStopBusOutDef, idStopBusRetDef);

        Update update = new Update();
        update.set("firstname", firstname);
        update.set("surname", surname);
        update.set("birth", birth);
        update.set("gender", gender);
        update.set("blobBase64", blobBase64);
        update.set("idStopBusOutDef", idStopBusOutDef);
        update.set("idStopBusRetDef", idStopBusRetDef);
        UpdateResult updateResult=myUpdateFunctionFirst(id,update);
        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("Child <update>");
        return this.childRepository.findById(id).orElseThrow(()->new NotFoundException("Child"));
    }

    @Override
    public void deleteById(String id) {
        Update update = new Update();
        update.set("isDeleted", true);
        UpdateResult updateResult=myUpdateFunctionFirst(id,update);
        if(updateResult.getMatchedCount()==0) throw new NotFoundException("Child <delete>");
        this.reservationRepository.deleteByIdChild(id);
    }
}
