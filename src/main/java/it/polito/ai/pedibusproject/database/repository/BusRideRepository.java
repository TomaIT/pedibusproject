package it.polito.ai.pedibusproject.database.repository;

import it.polito.ai.pedibusproject.database.model.BusRide;
import it.polito.ai.pedibusproject.database.model.StopBusType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BusRideRepository extends MongoRepository<BusRide,String> {
    Optional<BusRide> findByIdLineAndStopBusTypeAndYearAndMonthAndDay(String idLine, StopBusType stopBusType,
                                                                      Integer year,Integer month,Integer day);
}
