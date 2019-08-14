package it.polito.ai.pedibusproject.service.interfaces;

import it.polito.ai.pedibusproject.database.model.Role;
import it.polito.ai.pedibusproject.database.model.User;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

public interface UserService extends UserDetailsService {


    //Create User and Send Email for Official Registration
    User create(String email, Set<Role> roles);

    //If Success delete ConfirmationToken
    User confirmRegistration(UUID uuid,String email, String password, String firstname,
                             String surname, Date birth, String street, String phoneNumber);


    @Override
    User loadUserByUsername(String username);

    Set<User> findByRole(Role role);

    User updateUser(String email,String password,String firstname, String surname,
                    Date birth,String street,String phoneNumber);

    User addRole(String id,Role role);

    void enableUser(String username);

    void deleteById(String username);

    boolean existById(String username);

    void updatePassword(String username,String password);

    Page<User> findPaginated(int page, int size);

    boolean isAdminOfLine(String username, String idLine);

    //Aggiunge idLine e ROLE_ADMIN come set
    //(quindi duplicati non possono esistere)!!
    User addLine(String username, String idLine);

    User removeLine(String username, String idLine);
}
