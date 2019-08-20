package it.polito.ai.pedibusproject.service.implementations;

import com.mongodb.client.result.UpdateResult;
import it.polito.ai.pedibusproject.database.model.Role;
import it.polito.ai.pedibusproject.database.model.User;
import it.polito.ai.pedibusproject.database.repository.LineRepository;
import it.polito.ai.pedibusproject.database.repository.UserRepository;
import it.polito.ai.pedibusproject.exceptions.BadRequestException;
import it.polito.ai.pedibusproject.exceptions.DuplicateKeyException;
import it.polito.ai.pedibusproject.exceptions.NotFoundException;
import it.polito.ai.pedibusproject.service.interfaces.ConfirmationTokenService;
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

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private MongoTemplate mongoTemplate;
    private ConfirmationTokenService confirmationTokenService;
    private LineRepository lineRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           MongoTemplate mongoTemplate,
                           ConfirmationTokenService confirmationTokenService,
                           LineRepository lineRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder=passwordEncoder;
        this.mongoTemplate=mongoTemplate;
        this.confirmationTokenService=confirmationTokenService;
        this.lineRepository=lineRepository;
    }

    private UpdateResult myUpdateFunctionFirst(String id,Update update){
        Criteria criteria=new Criteria().andOperator(
                Criteria.where("_id").is(id)
        );
        Query query = new Query(criteria);
        return mongoTemplate.updateFirst(query, update, User.class);
    }

    @Override
    public User create(String username, Set<Role> roles) {
        User user = new User();

        user.setUsername(username);
        user.setRoles(roles);

        try {
            return userRepository.insert(user);
        }catch (org.springframework.dao.DuplicateKeyException e){
            throw new DuplicateKeyException("User <create>");
        }
    }

    @Override
    public User confirmRegistration(UUID uuid, String email, String password, String firstname,
                                    String surname, Date birth, String street, String phoneNumber) {
        Update update = new Update();
        update.set("password", passwordEncoder.encode(password));
        update.set("firstname", firstname);
        update.set("surname", surname);
        update.set("birth", birth);
        update.set("street", street);
        update.set("phoneNumber", phoneNumber);
        update.set("isEnabled", true);
        UpdateResult updateResult=myUpdateFunctionFirst(email,update);
        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("User <confirmRegistration>");
        this.confirmationTokenService.deleteByUuid(uuid);
        return this.userRepository.findById(email)
                .orElseThrow(()->new NotFoundException("User <confirmRegistration>"));
    }

    @Override
    public User loadUserByUsername(String username) {
        return this.userRepository.findById(username)
                .orElseThrow(()->new NotFoundException("User"));
    }

    @Override
    public Set<User> findByRole(Role role) {
        return this.userRepository.findAllByRolesContains(role);
    }

    @Override
    public User updateUser(String email,String password, String firstname, String surname, Date birth, String street, String phoneNumber) {
        Update update = new Update();
        update.set("password", passwordEncoder.encode(password));
        update.set("firstname", firstname);
        update.set("surname", surname);
        update.set("birth", birth);
        update.set("street", street);
        update.set("phoneNumber", phoneNumber);
        UpdateResult updateResult=myUpdateFunctionFirst(email,update);
        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("User <update>");
        return this.userRepository.findById(email)
                .orElseThrow(()->new NotFoundException("User <update>"));
    }

    @Override
    public User addRole(String id, Role role) {
        //if(role.equals(Role.ROLE_ADMIN)) throw new BadRequestException("User <addRole> does not handle role "+role);
        Update update = new Update();
        update.addToSet("roles",role);
        UpdateResult updateResult=myUpdateFunctionFirst(id,update);
        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("User <addRole>");
        return this.userRepository.findById(id)
                .orElseThrow(()->new NotFoundException("User <addRole>"));
    }

    @Override
    public User removeRole(String id, Role role) {
        Update update = new Update();
        update.pull("roles",role);
        UpdateResult updateResult=myUpdateFunctionFirst(id,update);
        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("User <removeRole>");
        return this.userRepository.findById(id)
                .orElseThrow(()->new NotFoundException("User <removeRole>"));
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
    public User addLine(String username, String idLine) {
        if(!lineRepository.existsById(idLine))
            throw new BadRequestException("User <addLine> line not found");
        Update update = new Update();
        update.addToSet("idLines",idLine);
        update.addToSet("roles",Role.ROLE_ADMIN);
        UpdateResult updateResult=myUpdateFunctionFirst(username,update);
        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("User <addLine>");
        return this.userRepository.findById(username)
                .orElseThrow(()->new NotFoundException("User <addLine>"));
    }

    @Override
    public User removeLine(String username, String idLine) {
        Update update = new Update();
        update.pull("idLines",idLine);
        UpdateResult updateResult=myUpdateFunctionFirst(username,update);
        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("User <removeLine>");
        return this.userRepository.findById(username)
                .orElseThrow(()->new NotFoundException("User <removeLine>"));
    }

    @Override
    public User disableById(String idUser) {
        Update update = new Update();
        update.set("isAccountNonLocked",false);
        UpdateResult updateResult=myUpdateFunctionFirst(idUser,update);
        if(updateResult.getMatchedCount()==0)
            throw new NotFoundException("User <disableById>");
        return this.userRepository.findById(idUser)
                .orElseThrow(()->new NotFoundException("User <disableById>"));
    }
}
