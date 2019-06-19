package it.polito.ai.pedibusproject.service.interfaces;

import it.polito.ai.pedibusproject.database.model.Role;
import it.polito.ai.pedibusproject.database.model.User;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    User registerNewUserAccount(String email, String password, List<Role> roles);

    @Override
    User loadUserByUsername(String username);

    void enableUser(String username);

    void deleteById(String username);

    boolean existById(String username);

    void updatePassword(String username,String password);

    Page<User> findPaginated(int page, int size);

    boolean isAdminOfLine(String username, String idLine);

    void addLine(String username, String idLine);

    void removeLine(String username, String idLine);
}
