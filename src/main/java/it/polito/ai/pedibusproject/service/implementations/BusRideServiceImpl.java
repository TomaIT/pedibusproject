package it.polito.ai.pedibusproject.service.implementations;

import it.polito.ai.pedibusproject.database.model.BusRide;
import it.polito.ai.pedibusproject.database.model.Line;
import it.polito.ai.pedibusproject.database.model.StopBus;
import it.polito.ai.pedibusproject.database.model.StopBusType;
import it.polito.ai.pedibusproject.database.repository.BusRideRepository;
import it.polito.ai.pedibusproject.exceptions.DuplicateKeyException;
import it.polito.ai.pedibusproject.exceptions.NotFoundException;
import it.polito.ai.pedibusproject.service.interfaces.BusRideService;
import it.polito.ai.pedibusproject.service.interfaces.LineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class BusRideServiceImpl implements BusRideService {
    private static final Logger LOG = LoggerFactory.getLogger(BusRideServiceImpl.class);
    private BusRideRepository busRideRepository;
    private LineService lineService;

    @Autowired
    public BusRideServiceImpl(BusRideRepository busRideRepository, LineService lineService) {
        this.busRideRepository = busRideRepository;
        this.lineService = lineService;
    }

    private BusRide mySave(BusRide busRide){
        //TODO transazione...
        if(this.busRideRepository.findByIdLineAndStopBusTypeAndYearAndMonthAndDay(
                busRide.getIdLine(),busRide.getStopBusType(),busRide.getYear(),
                busRide.getMonth(),busRide.getDay()).isPresent())
            throw new DuplicateKeyException("BusRide <save>");
        return this.busRideRepository.insert(busRide);
    }

    @Override
    public BusRide create(String idLine, StopBusType stopBusType, Integer year,
                          Integer month, Integer day) {
        Line line = this.lineService.findById(idLine);
        TreeSet<StopBus> stopBuses = this.lineService.findByIdAndStopBusType(idLine, stopBusType);
        BusRide busRide = new BusRide(line.getId(), stopBusType, stopBuses, year, month, day);
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
            //TODO holiday ?? http://www.bank-holidays.com/
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
    public BusRide findByIdLineAndStopBusTypeAndYearAndMonthAndDay(String idLine,
                                                                   StopBusType stopBusType,
                                                                   Integer year, Integer month,
                                                                   Integer day) {
        return this.busRideRepository.findByIdLineAndStopBusTypeAndYearAndMonthAndDay(
                idLine,stopBusType,year,month,day).orElseThrow(()->new NotFoundException("BusRide <findBy'id'>"));
    }

    @Override
    public TreeSet<BusRide> findAll() {
        return new TreeSet<>(this.busRideRepository.findAll());
    }
}
