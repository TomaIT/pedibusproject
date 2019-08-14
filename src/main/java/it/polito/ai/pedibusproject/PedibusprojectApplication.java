package it.polito.ai.pedibusproject;

import it.polito.ai.pedibusproject.database.model.Role;
import it.polito.ai.pedibusproject.service.interfaces.UserService;
import it.polito.ai.pedibusproject.utility.LoaderLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class PedibusprojectApplication implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(PedibusprojectApplication.class);
    //Autowired Injection in constructor no, because CYCLE BEAN
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private LoaderLine loaderLine;
    @Autowired
    private UserService userService;
    @Value("${spring.mail.username}")
    private String sysAdmin;

    //Delete ALL into MongoDB
    private void cleanAllDB() {
        for (String i : mongoTemplate.getDb().listCollectionNames()) {
            mongoTemplate.dropCollection(i);
            LOG.info("Dropped collection: " + i);
        }
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        SpringApplication.run(PedibusprojectApplication.class, args);
    }

    public void createSysAdmin(){
        try{
            Set<Role> temp=new HashSet<>();
            temp.add(Role.ROLE_SYS_ADMIN);
            userService.create(sysAdmin,temp);
        }catch (Exception ignored){}
    }
    @Override
    public void run(String... args) throws Exception {
        cleanAllDB();
        this.createSysAdmin();
        this.loaderLine.updateLines();
    }
}
