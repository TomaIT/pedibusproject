package it.polito.ai.pedibusproject.service.implementations;

import com.mongodb.client.result.UpdateResult;
import it.polito.ai.pedibusproject.controller.model.LineEnum;
import it.polito.ai.pedibusproject.database.model.Line;
import it.polito.ai.pedibusproject.database.model.StopBus;
import it.polito.ai.pedibusproject.database.model.StopBusType;
import it.polito.ai.pedibusproject.database.repository.LineRepository;
import it.polito.ai.pedibusproject.exceptions.BadRequestException;
import it.polito.ai.pedibusproject.exceptions.DuplicateKeyException;
import it.polito.ai.pedibusproject.exceptions.NotFoundException;
import it.polito.ai.pedibusproject.service.interfaces.LineService;
import it.polito.ai.pedibusproject.service.interfaces.StopBusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Service
public class LineServiceImpl implements LineService {
    private static final Logger LOG = LoggerFactory.getLogger(LineServiceImpl.class);
    private LineRepository lineRepository;
    private MongoTemplate mongoTemplate;
    private StopBusService stopBusService;

    @Autowired
    public LineServiceImpl(LineRepository lineRepository,
                           MongoTemplate mongoTemplate,
                           StopBusService stopBusService){
        this.lineRepository=lineRepository;
        this.stopBusService=stopBusService;
        this.mongoTemplate=mongoTemplate;
    }

    @Override
    public Set<LineEnum> aggregateNames() {
        Set<LineEnum> temp= new HashSet<>();
        this.lineRepository.findByIsDeleted(false)
                .forEach(x->temp.add(new LineEnum(x.getId(),x.getName())));
        return temp;
    }

    @Override
    public Line create(Line line) {
        //Only debug
        if(line.getId()!=null)
            LOG.debug("Create Line with id!=null");
        if(this.lineRepository.findByName(line.getName()).stream().anyMatch(x-> !x.getIsDeleted())) {
            LOG.error("Create Line recall when in db there is ancora una linea con stesso nome non deleted.");
            throw new DuplicateKeyException("Line <create>");
        }
        return this.lineRepository.insert(line);
    }

    @Override
    public Line findByName(String name) {
        return this.lineRepository.findByNameAndIsDeleted(name,false)
                .orElseThrow(()->new NotFoundException("Line <findByName>"));
    }

    private UpdateResult myUpdateFunctionFirst(String id, Update update){
        Criteria criteria=new Criteria().andOperator(
                Criteria.where("_id").is(id)
        );
        Query query = new Query(criteria);
        return mongoTemplate.updateFirst(query, update, Line.class);
    }

    @Override
    public void deleteById(String id) {
        Update update = new Update();
        update.set("isDeleted", true);
        update.set("deletedTime", System.currentTimeMillis());
        UpdateResult updateResult=myUpdateFunctionFirst(id,update);
        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("Line <delete>");
    }

    @Override
    public Set<Line> findAll() {
        return this.lineRepository.findByIsDeleted(false);
    }

    @Override
    public Line findById(String id) {
        return this.lineRepository.findById(id).orElseThrow(()->new NotFoundException("Line"));
    }

    @Override
    public TreeSet<StopBus> findByIdAndStopBusType(String id, StopBusType stopBusType) {
        Line line=this.lineRepository.findById(id).orElseThrow(()->new NotFoundException("Line <findStopBuses>"));
        TreeSet<StopBus> temp=new TreeSet<>();
        switch (stopBusType){
            case Outward:
                line.getIdOutStopBuses().forEach(x-> temp.add(this.stopBusService.findById(x)));
                break;
            case Return:
                line.getIdRetStopBuses().forEach(x-> temp.add(this.stopBusService.findById(x)));
                break;
            default:
                throw new BadRequestException("Line <findStopBuses>");
        }
        return temp;
    }


}
