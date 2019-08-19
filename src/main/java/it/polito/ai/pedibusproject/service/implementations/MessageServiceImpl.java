package it.polito.ai.pedibusproject.service.implementations;

import com.mongodb.client.result.UpdateResult;
import it.polito.ai.pedibusproject.database.model.Message;
import it.polito.ai.pedibusproject.database.model.User;
import it.polito.ai.pedibusproject.database.repository.MessageRepository;
import it.polito.ai.pedibusproject.exceptions.NotFoundException;
import it.polito.ai.pedibusproject.service.interfaces.MessageService;
import it.polito.ai.pedibusproject.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MessageServiceImpl implements MessageService {
    private MessageRepository messageRepository;
    private UserService userService;
    private MongoTemplate mongoTemplate;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository,
                              UserService userService,
                              MongoTemplate mongoTemplate){
        this.messageRepository=messageRepository;
        this.userService=userService;
        this.mongoTemplate=mongoTemplate;
    }

    private UpdateResult myUpdateFunctionFirst(String id, Update update){
        Criteria criteria=new Criteria().andOperator(
                Criteria.where("_id").is(id)
        );
        Query query = new Query(criteria);
        return mongoTemplate.updateFirst(query, update, Message.class);
    }

    @Override
    public Message create(String idUserFrom, String idUserTo, String subject, String message, Long creationTime) {
        this.userService.loadUserByUsername(idUserFrom);
        this.userService.loadUserByUsername(idUserTo);
        return this.messageRepository.insert(new Message(idUserFrom,idUserTo,subject,message,creationTime));
    }

    @Override
    public Message findById(String id) {
        return this.messageRepository.findById(id).orElseThrow(()->new NotFoundException("Message"));
    }

    @Override
    public Message updateReadConfirmById(String id, Long readConfirm) {
        Update update = new Update();
        update.set("readConfirm", readConfirm);
        UpdateResult updateResult=myUpdateFunctionFirst(id,update);
        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("Message <updateReadConfirm>");
        return this.messageRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Message"));
    }

    @Override
    public void deleteById(String id) {
        this.messageRepository.deleteById(id);
    }

    @Override
    public Set<Message> findAllByIdUserTo(String idUserTo) {
        return this.messageRepository.findAllByIdUserTo(idUserTo);
    }
}
