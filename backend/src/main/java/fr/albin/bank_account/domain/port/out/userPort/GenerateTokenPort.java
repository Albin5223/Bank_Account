package fr.albin.bank_account.domain.port.out.userPort;

import fr.albin.bank_account.domain.model.User;

/**
 * Interface qui permet de générer un token d'authentification pour un utilisateur donné
 */
public interface GenerateTokenPort {
    String generateToken(User user);
}
