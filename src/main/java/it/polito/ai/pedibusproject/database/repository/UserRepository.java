package it.polito.ai.pedibusproject.database.repository;

import it.polito.ai.pedibusproject.database.model.Role;
import it.polito.ai.pedibusproject.database.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Set;

public interface UserRepository extends MongoRepository<User, String> {

    // TODO: non ho ancora capito come fare la Query
    @Query(value = "{ 'users.roles' : ?0 }")
    Set<User> findByRole(Role role);
}
