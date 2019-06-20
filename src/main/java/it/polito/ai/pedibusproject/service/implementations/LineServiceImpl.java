package it.polito.ai.pedibusproject.service.implementations;

import com.mongodb.client.result.UpdateResult;
import it.polito.ai.pedibusproject.database.model.Line;
import it.polito.ai.pedibusproject.database.repository.LineRepository;
import it.polito.ai.pedibusproject.exceptions.DuplicateKeyException;
import it.polito.ai.pedibusproject.exceptions.NotFoundException;
import it.polito.ai.pedibusproject.service.interfaces.LineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LineServiceImpl implements LineService {
    private static final Logger LOG = LoggerFactory.getLogger(LineServiceImpl.class);
    private LineRepository lineRepository;
    private MongoTemplate mongoTemplate;

    @Autowired
    public LineServiceImpl(LineRepository lineRepository,
                           MongoTemplate mongoTemplate){
        this.lineRepository=lineRepository;
        this.mongoTemplate=mongoTemplate;
    }

    @Override
    public Set<String> aggregateNames() {
        return this.lineRepository.findByIsDeleted(false).stream()
                .map(Line::getName).collect(Collectors.toSet());
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


}
