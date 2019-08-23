package it.polito.ai.pedibusproject.database.repository;

import it.polito.ai.pedibusproject.database.model.BusRide;
import it.polito.ai.pedibusproject.database.model.StopBus;
import it.polito.ai.pedibusproject.database.model.StopBusType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

public interface BusRideRepository extends MongoRepository<BusRide,String> {
    Optional<BusRide> findByIdLineAndStopBusTypeAndYearAndMonthAndDay(String idLine, StopBusType stopBusType,
                                                                      Integer year,Integer month,Integer day);
    Set<BusRide> findAllByIdLine(String idLine);

    Set<BusRide> findAllByStopBusesContainsAndStartTimeAfter(StopBus stopBus, Date startTime);
}
