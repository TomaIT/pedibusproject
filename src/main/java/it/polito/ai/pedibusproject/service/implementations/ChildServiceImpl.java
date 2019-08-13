package it.polito.ai.pedibusproject.service.implementations;

import com.mongodb.client.result.UpdateResult;
import it.polito.ai.pedibusproject.database.model.Child;
import it.polito.ai.pedibusproject.database.model.Gender;
import it.polito.ai.pedibusproject.database.model.User;
import it.polito.ai.pedibusproject.database.repository.ChildRepository;
import it.polito.ai.pedibusproject.database.repository.UserRepository;
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
import java.util.Set;

@Service
public class ChildServiceImpl implements ChildService {
    private ChildRepository childRepository;
    private MongoTemplate mongoTemplate;
    private UserRepository userRepository;

    @Autowired
    public ChildServiceImpl(ChildRepository childRepository, MongoTemplate mongoTemplate,UserRepository userRepository){
        this.childRepository=childRepository;
        this.mongoTemplate=mongoTemplate;
        this.userRepository=userRepository;
    }

    private UpdateResult myUpdateFunctionFirst(String id, Update update){
        Criteria criteria=new Criteria().andOperator(
                Criteria.where("_id").is(id)
        );
        Query query = new Query(criteria);
        return mongoTemplate.updateFirst(query, update, User.class);
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
    public Child create(String idUser, String firstname, String surname, Date birth, Gender gender, String blobBase64, String idStopBusOutDef, String idStopBusRetDef) {
        if(!this.userRepository.existsById(idUser))
            throw new BadRequestException("Child <create> not found Parent");
        return this.childRepository.insert(new Child(idUser,firstname,surname,birth,gender,blobBase64,idStopBusOutDef,idStopBusRetDef));
    }

    @Override
    public Child update(String id, String firstname, String surname, Date birth, Gender gender, String blobBase64, String idStopBusOutDef, String idStopBusRetDef) {
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
    }
}
