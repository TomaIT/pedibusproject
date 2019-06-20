package it.polito.ai.pedibusproject;

import it.polito.ai.pedibusproject.utility.LoaderLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class PedibusprojectApplication implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(PedibusprojectApplication.class);
    //Autowired Injection no, because CYCLE BEAN
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private LoaderLine loaderLine;

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

    @Override
    public void run(String... args) throws Exception {
        //cleanAllDB();
        this.loaderLine.updateLines();
    }
}
