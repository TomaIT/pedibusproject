package it.polito.ai.pedibusproject.service.implementations;

import it.polito.ai.pedibusproject.database.model.Role;
import it.polito.ai.pedibusproject.database.model.User;
import it.polito.ai.pedibusproject.database.repository.UserRepository;
import it.polito.ai.pedibusproject.service.interfaces.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {
    private UserRepository userRepository;

    @Autowired
    public RoleServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Set<User> findUsersByRole(Role role) {
        this.userRepository.findByRole(role);
        return null;
    }
}
