package fr.albin.bank_account.domain.exception;


/**
 * Exception métier levée lorsqu'un utilisateur fournit des identifiants invalides.
 */
public class InvalidCredential extends IllegalArgumentException{

    public InvalidCredential(String message) {
        super(message);
    }

}
