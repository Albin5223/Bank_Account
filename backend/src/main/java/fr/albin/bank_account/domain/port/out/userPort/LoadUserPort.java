package fr.albin.bank_account.domain.port.out.userPort;

import java.util.Optional;

import fr.albin.bank_account.domain.model.User;

/**
 * Interface qui permet de charger les informations d'un utilisateur à partir de son identifiant
 */
public interface LoadUserPort {

    Optional<User> loadUserByEmail(String email);
    Optional<User> loadUserByUsername(String username);
    
}
