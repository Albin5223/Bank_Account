package fr.albin.bank_account.domain.port.in.userUseCase;

import fr.albin.bank_account.domain.model.User;

/**
 * Interface qui permet de récupérer les informations d'un utilisateur
 * <p>
 * Cette interface définit les méthodes nécessaires pour obtenir les informations d'un utilisateur en fonction de son nom d'utilisateur
 */
public interface GetUserInfoUseCase {
    User getUserInfo(String username);
}
