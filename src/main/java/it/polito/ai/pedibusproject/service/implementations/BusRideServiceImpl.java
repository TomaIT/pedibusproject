package it.polito.ai.pedibusproject.service.implementations;

import com.mongodb.client.result.UpdateResult;
import it.polito.ai.pedibusproject.database.model.*;
import it.polito.ai.pedibusproject.database.repository.AvailabilityRepository;
import it.polito.ai.pedibusproject.database.repository.BusRideRepository;
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

import java.util.Date;
import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;

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
    @Value("${spring.mail.username}")
    private String sysAdmin;
    @Value("${busride.time.delay.before.create.busride.seconds}")
    private long minDelayBeforeCreateBusRideSec;


    @Autowired
    public BusRideServiceImpl(BusRideRepository busRideRepository, LineService lineService,
                              MongoTemplate mongoTemplate,ReservationService reservationService,
                              MessageService messageService,
                              AvailabilityRepository availabilityRepository,
                              StopBusRepository stopBusRepository) {
        this.busRideRepository = busRideRepository;
        this.lineService = lineService;
        this.mongoTemplate=mongoTemplate;
        this.reservationService=reservationService;
        this.messageService=messageService;
        this.availabilityRepository=availabilityRepository;
        this.stopBusRepository=stopBusRepository;
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
        Set<Reservation> temp=this.reservationService.findAllByIdBusRide(id);
        temp.forEach(x-> {
            this.messageService.create(sysAdmin, x.getIdUser(),
                    "Deleted Reservation",
                    "La sua prenotazione ("+x.toString()+") è stata cancellata," +
                            " in quanto la corsa per quel giorno è stata annullata.\n" +
                            "Ci scusiamo per il disagio.",
                    System.currentTimeMillis());
            this.reservationService.deleteById(x.getId());
        });
        this.availabilityRepository.findAllByIdBusRide(id).forEach(x-> {
            this.messageService.create(sysAdmin, x.getIdUser(),
                    "Deleted Availability",
                    "La sua disponibilità ("+x.toString()+") è stata cancellata," +
                            " in quanto la corsa per quel giorno è stata annullata.",
                    System.currentTimeMillis());
            this.availabilityRepository.deleteById(x.getId());
        });
        this.busRideRepository.deleteById(id);
    }
}
