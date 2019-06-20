package it.polito.ai.pedibusproject.service.implementations;

import it.polito.ai.pedibusproject.database.model.StopBus;
import it.polito.ai.pedibusproject.database.repository.StopBusRepository;
import it.polito.ai.pedibusproject.exceptions.NotFoundException;
import it.polito.ai.pedibusproject.service.interfaces.StopBusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class StopBusServiceImpl implements StopBusService {
    private StopBusRepository stopBusRepository;

    @Autowired
    public StopBusServiceImpl(StopBusRepository stopBusRepository){
        this.stopBusRepository=stopBusRepository;
    }

    @Override
    public StopBus insert(StopBus stopBus) {
        return this.stopBusRepository.insert(stopBus);
    }

    @Override
    public StopBus findById(String id) {
        return this.stopBusRepository.findById(id).orElseThrow(()->new NotFoundException("StopBus"));
    }

    @Override
    public Set<StopBus> findAll() {
        return new HashSet<>(this.stopBusRepository.findAll());
    }
}
