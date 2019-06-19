package it.polito.ai.pedibusproject.service.implementations;

import com.mongodb.client.result.UpdateResult;
import it.polito.ai.pedibusproject.database.model.Role;
import it.polito.ai.pedibusproject.database.model.User;
import it.polito.ai.pedibusproject.database.repository.UserRepository;
import it.polito.ai.pedibusproject.exceptions.DuplicateKeyException;
import it.polito.ai.pedibusproject.exceptions.NotFoundException;
import it.polito.ai.pedibusproject.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private MongoTemplate mongoTemplate;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder=passwordEncoder;
        this.mongoTemplate=mongoTemplate;
    }

    private UpdateResult myUpdateFunctionFirst(String id,Update update){
        Criteria criteria=new Criteria().andOperator(
                Criteria.where("_id").is(id)
        );
        Query query = new Query(criteria);
        return mongoTemplate.updateFirst(query, update, User.class);
    }

    @Override
    public User registerNewUserAccount(String username, String password, List<Role> roles) {
        User user = new User();

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);

        try {
            return userRepository.insert(user);
        }catch (org.springframework.dao.DuplicateKeyException e){
            throw new DuplicateKeyException("User <registerNewUserAccount>");
        }
    }

    @Override
    public User loadUserByUsername(String username) {
        return this.userRepository.findById(username)
                .orElseThrow(()->new NotFoundException("User"));
    }

    @Override
    public void enableUser(String username) {
        Update update = new Update();
        update.set("isEnabled", true);
        UpdateResult updateResult=myUpdateFunctionFirst(username,update);
        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("User");
    }

    @Override
    public void deleteById(String username) {
        userRepository.deleteById(username);
    }

    @Override
    public boolean existById(String username) {
        return userRepository.existsById(username);
    }

    @Override
    public void updatePassword(String username, String password) {
        Update update = new Update();
        update.set("password", passwordEncoder.encode(password));
        UpdateResult updateResult=myUpdateFunctionFirst(username,update);
        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("User");
    }

    @Override
    public Page<User> findPaginated(int page, int size) {
        Page<User> temp = userRepository.findAll(PageRequest.of(page, size));
        if(page+1>temp.getTotalPages())
            throw new NotFoundException("User <page>");
        return temp;
    }

    @Override
    public boolean isAdminOfLine(String username, String idLine) {
        User user = loadUserByUsername(username);
        return user.getIdLines().contains(idLine);
    }

    @Override
    public void addLine(String username, String idLine) {
        Update update = new Update();
        update.addToSet("idLines",idLine);
        update.addToSet("roles",Role.ROLE_ADMIN);
        UpdateResult updateResult=myUpdateFunctionFirst(username,update);
        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("User");
    }

    @Override
    public void removeLine(String username, String idLine) {
        Update update = new Update();
        update.pull("idLines",idLine);
        UpdateResult updateResult=myUpdateFunctionFirst(username,update);
        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("User");
    }
}
