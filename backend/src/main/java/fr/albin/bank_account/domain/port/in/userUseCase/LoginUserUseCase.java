package fr.albin.bank_account.domain.port.in.userUseCase;

/**
 * Interface qui permet de connecter un utilisateur à l'application bancaire
 * <p>
 * Cette interface définit les méthodes nécessaires pour authentifier un utilisateur en fonction de ses identifiants
 */
public interface LoginUserUseCase {

    String loginUser(String username, String password);
    
}
