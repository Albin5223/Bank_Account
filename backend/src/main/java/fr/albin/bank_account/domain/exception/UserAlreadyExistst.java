package fr.albin.bank_account.domain.exception;


/**
 * Exception métier levée lorsqu'un utilisateur tente de s'inscrire avec un nom d'utilisateur déjà existant.
 */
public class UserAlreadyExistst extends IllegalArgumentException {

    public UserAlreadyExistst(String message) {
        super(message);
    }
}
