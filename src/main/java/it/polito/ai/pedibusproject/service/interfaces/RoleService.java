package it.polito.ai.pedibusproject.service.interfaces;

import it.polito.ai.pedibusproject.database.model.Role;
import it.polito.ai.pedibusproject.database.model.User;

import java.util.Set;

public interface RoleService {

    Set<User> findUsersByRole(Role role);
}
