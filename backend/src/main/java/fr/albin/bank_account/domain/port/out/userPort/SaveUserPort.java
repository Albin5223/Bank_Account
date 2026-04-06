package fr.albin.bank_account.domain.port.out.userPort;

import fr.albin.bank_account.domain.model.User;

/**
 * Interface qui permet de sauvegarder un utilisateur
 */
public interface SaveUserPort {
    void save(User user);
}
