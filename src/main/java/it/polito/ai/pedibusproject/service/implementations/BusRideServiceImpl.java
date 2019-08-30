package it.polito.ai.pedibusproject.service.implementations;

import com.mongodb.client.result.UpdateResult;
import it.polito.ai.pedibusproject.database.model.*;
import it.polito.ai.pedibusproject.database.repository.AvailabilityRepository;
import it.polito.ai.pedibusproject.database.repository.BusRideRepository;
import it.polito.ai.pedibusproject.database.repository.ChildRepository;
import it.polito.ai.pedibusproject.database.repository.StopBusRepository;
import it.polito.ai.pedibusproject.exceptions.BadRequestException;
import it.polito.ai.pedibusproject.exceptions.DuplicateKeyException;
import it.polito.ai.pedibusproject.exceptions.InternalServerErrorException;
import it.polito.ai.pedibusproject.exceptions.NotFoundException;
import it.polito.ai.pedibusproject.service.interfaces.BusRideService;
import it.polito.ai.pedibusproject.service.interfaces.LineService;
import it.polito.ai.pedibusproject.service.interfaces.MessageService;
import it.polito.ai.pedibusproject.service.interfaces.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BusRideServiceImpl implements BusRideService {
    private static final Logger LOG = LoggerFactory.getLogger(BusRideServiceImpl.class);
    private BusRideRepository busRideRepository;
    private LineService lineService;
    private MongoTemplate mongoTemplate;
    private ReservationService reservationService;
    private MessageService messageService;
    private AvailabilityRepository availabilityRepository;
    private StopBusRepository stopBusRepository;
    private ChildRepository childRepository;
    @Value("${spring.mail.username}")
    private String sysAdmin;
    @Value("${busride.time.delay.before.create.busride.seconds}")
    private long minDelayBeforeCreateBusRideSec;
    @Value("${busride.time.delay.before.start.seconds}")
    private long maxDelayBeforeStartBusRideSec;


    @Autowired
    public BusRideServiceImpl(BusRideRepository busRideRepository, LineService lineService,
                              MongoTemplate mongoTemplate,ReservationService reservationService,
                              MessageService messageService,
                              AvailabilityRepository availabilityRepository,
                              StopBusRepository stopBusRepository,
                              ChildRepository childRepository) {
        this.busRideRepository = busRideRepository;
        this.lineService = lineService;
        this.mongoTemplate=mongoTemplate;
        this.reservationService=reservationService;
        this.messageService=messageService;
        this.availabilityRepository=availabilityRepository;
        this.stopBusRepository=stopBusRepository;
        this.childRepository=childRepository;
    }

    public BusRide mySave(BusRide busRide){
        try {
            return this.busRideRepository.insert(busRide);
        }catch (org.springframework.dao.DuplicateKeyException e){
            throw new DuplicateKeyException("BusRide <save>");
        }
    }

    private UpdateResult myUpdateFunctionFirst(String id, Update update){
        Criteria criteria=new Criteria().andOperator(
                Criteria.where("_id").is(id)
        );
        Query query = new Query(criteria);
        return mongoTemplate.updateFirst(query, update, BusRide.class);
    }

    @Override
    public BusRide create(String idLine, StopBusType stopBusType, Integer year,
                          Integer month, Integer day) {
        Line line = this.lineService.findById(idLine);
        TreeSet<StopBus> stopBuses = this.lineService.findByIdAndStopBusType(idLine, stopBusType);

        BusRide busRide = new BusRide(line.getId(), stopBusType, stopBuses, year, month, day);


        if(busRide.getStartTime().getTime()-minDelayBeforeCreateBusRideSec<(new Date()).getTime())
            throw new BadRequestException("BusRide <create> data is too close or in the past");
        return mySave(busRide);
    }

    @Override
    public TreeSet<BusRide> createToIntervalDate(String idLine, StopBusType stopBusType,
                                                 Integer year, Integer month, Integer day,
                                                 int intervalDays) {
        Calendar c = BusRide.getCalendarOnlyDay(year,month,day);
        TreeSet<BusRide> busRides = new TreeSet<>();
        Line line = this.lineService.findById(idLine);
        TreeSet<StopBus> stopBuses = this.lineService.findByIdAndStopBusType(idLine, stopBusType);
        for (int i = 0; i<intervalDays;i++){
            //TODO holiday ?? https://publicholidays.it/school-holidays/piedmont/
            if (!(c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
                TreeSet<StopBus> tempStopBuses = new TreeSet<>(stopBuses);
                BusRide temp = mySave(new BusRide(line.getId(), stopBusType,
                        tempStopBuses,c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)));
                busRides.add(temp);
            }
            c.add(Calendar.DAY_OF_MONTH,1);
        }

        return busRides;
    }

    @Override
    public BusRide findById(String id) {
        return this.busRideRepository.findById(id).orElseThrow(() -> new NotFoundException("BusRide"));
    }

    @Override
    public Set<BusRide> findByIdLine(String idLine) {
        return this.busRideRepository.findAllByIdLine(idLine);
    }

    @Override
    public BusRide findByIdLineAndStopBusTypeAndYearAndMonthAndDay(String idLine,
                                                                   StopBusType stopBusType,
                                                                   Integer year, Integer month,
                                                                   Integer day) {
        return this.busRideRepository.findByIdLineAndStopBusTypeAndYearAndMonthAndDay(
                idLine,stopBusType,year,month,day).orElseThrow(()->new NotFoundException("BusRide <findBy'id'>"));
    }

    @Override
    public BusRide updateLastStopBus(String id,Long timestampLastStopBus, String idLastStopBus) {
        //Check
        BusRide temp=this.busRideRepository.findById(id)
                .orElseThrow(()->new NotFoundException("BusRide"));

        if(temp.getIdLastStopBus()==null){//Is Start Point
            if(!temp.getStopBuses().first().getId().equals(idLastStopBus))//It's not first stopBus
                throw new BadRequestException("BusRide <updateLastStopBus> it's not the first StopBus");

            if(!(timestampLastStopBus>=temp.getStartTime().getTime()-maxDelayBeforeStartBusRideSec &&
                    timestampLastStopBus<=temp.getStartTime().getTime()+maxDelayBeforeStartBusRideSec)){
                throw new BadRequestException("BusRide <updateLastStopBus> è sei temporalmente troppo distante per poter far parire la corsa.");
            }
        }else{ //Controllo sequenzialità fermate
            TreeSet<StopBus> tempT=new TreeSet<>(temp.getStopBuses());
            while (!tempT.isEmpty()){
                if(Objects.requireNonNull(tempT.pollLast()).getId().equals(idLastStopBus)){
                    if(!temp.getIdLastStopBus().equals(tempT.last().getId()))
                        throw new BadRequestException("BusRide <updateLastStopBus> la nuova fermata non è successiva a quella vecchia.");
                    break;
                }
            }
        }
        Line line=this.lineService.findById(temp.getIdLine());
        switch (temp.getStopBusType()){
            case Return:
                if(!line.getIdRetStopBuses().contains(idLastStopBus))
                    throw new BadRequestException("BusRide <updateLastStopBus> stop bus not exist in busride");
                break;
            case Outward:
                if(!line.getIdOutStopBuses().contains(idLastStopBus))
                    throw new BadRequestException("BusRide <updateLastStopBus> stop bus not exist in busride");
                break;
            default:
                throw new InternalServerErrorException("BusRide <updateLastStopBus> inconsistent state");
        }
        //End check
        Update update = new Update();
        update.set("timestampLastStopBus", timestampLastStopBus);
        update.set("idLastStopBus", idLastStopBus);
        UpdateResult updateResult=myUpdateFunctionFirst(id,update);
        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("BusRide <updateLastStopBus>");
        return this.busRideRepository.findById(id)
                .orElseThrow(()->new NotFoundException("BusRide <updateLastStopBus>"));
    }

    @Override
    public TreeSet<BusRide> findAll() {
        return new TreeSet<>(this.busRideRepository.findAll());
    }

    @Override
    public TreeSet<BusRide> findAllByStopBusesContainsAndStartTimeAfter(String idStopBus, Date startTime) {
        StopBus temp=this.stopBusRepository.findById(idStopBus)
                .orElseThrow(()->new NotFoundException("StopBus <findAllBusRidesByStopBusesContainsAndStartTimeAfter>"));
        return new TreeSet<>(this.busRideRepository
                .findAllByStopBusesContainsAndStartTimeAfter(temp,startTime));
    }

    @Override
    public void deleteById(String id) {
        BusRide busRideToDelete=this.findById(id);
        if(busRideToDelete.getIdLastStopBus()!=null)
            throw new BadRequestException("BusRide <deleteById> the busRide is already started.");
        Set<Reservation> temp=this.reservationService.findAllByIdBusRide(id);
        temp.forEach(x-> {
            Child child=childRepository.findById(x.getIdChild())
                    .orElseThrow(()->new InternalServerErrorException("BusRide <deleteById> child not found"));
            BusRide busRide=busRideRepository.findById(x.getIdBusRide())
                    .orElseThrow(()->new InternalServerErrorException("BusRide <deleteById> busride not found"));

            this.messageService.create(sysAdmin, x.getIdUser(), "Prenotazione Cancellata",
                    "La sua prenotazione per:\n"+
                            "Bambino: "+child.getFirstname()+" "+child.getSurname()+"\n"+
                            "Linea: "+lineService.findById(busRide.getIdLine()).getName() +"\n"+
                            "Data: "+busRide.getStartTime()+"\n"+
                            "è stata cancellata, in quanto la corsa per quel giorno è stata annullata.\n" +
                            "Ci scusiamo per il disagio.",
                    System.currentTimeMillis());
            this.reservationService.deleteById(x.getId());
        });
        this.availabilityRepository.findAllByIdBusRide(id).forEach(x-> {
            BusRide br=busRideRepository.findById(x.getIdBusRide())
                    .orElseThrow(()->new InternalServerErrorException("BusRide <deleteById> busride not found"));
            this.messageService.create(sysAdmin, x.getIdUser(), "Disponibilità Cancellata",
                    "La sua disponibilità per\n"+
                            x.getMessage(br,lineService.findById(br.getIdLine()))+
                            "è stata cancellata, in quanto la corsa per quel giorno è stata annullata.",
                    System.currentTimeMillis());
            this.availabilityRepository.deleteById(x.getId());
        });
        this.busRideRepository.deleteById(id);
    }
}
