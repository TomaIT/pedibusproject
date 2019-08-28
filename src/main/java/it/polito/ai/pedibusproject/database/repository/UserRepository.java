package it.polito.ai.pedibusproject.database.repository;

import it.polito.ai.pedibusproject.database.model.Role;
import it.polito.ai.pedibusproject.database.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Set;

public interface UserRepository extends MongoRepository<User, String> {
    Set<User> findAllByRolesContains(Role role);
    Set<User> findAllByIdLinesContains(String idLine);
    Page<User> findAllByUsernameStartsWithOrderByUsername(String username, Pageable pageable);
}
