package fr.albin.bank_account.domain.port.in.userUseCase;

/**
 * Interface qui permet d'enregistrer un nouvel utilisateur dans l'application bancaire
 * <p>
 */
public interface RegisterUserUseCase {

    String registerUser(String username, String password, String email);
}
