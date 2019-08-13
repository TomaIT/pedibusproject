package it.polito.ai.pedibusproject.service.interfaces;

import it.polito.ai.pedibusproject.database.model.BusRide;
import it.polito.ai.pedibusproject.database.model.StopBusType;

import java.util.TreeSet;

public interface BusRideService {

    BusRide create(String idLine, StopBusType stopBusType,
                   Integer year,Integer month,Integer day);

    //Esclude sabato e domenica
    TreeSet<BusRide> createToIntervalDate(String idLine, StopBusType stopBusType,
                                          Integer year,Integer month,Integer day,
                                          int intervalDays);

    BusRide findById(String id);

    BusRide findByIdLineAndStopBusTypeAndYearAndMonthAndDay(String idLine, StopBusType stopBusType,
                                                            Integer year, Integer month, Integer day);

    BusRide updateLastStopBus(String id,Long timestampLastStopBus,String idLastStopBus);

    TreeSet<BusRide> findAll();

    //Cancella busride, creando un messaggio di comunicazioni per tutte le prenotazioni annullate
    void deleteById(String id);
}
