package fr.albin.bank_account.domain.exception;

/**
 * Exception métier levée lorsqu'un retrait dépasse le solde disponible.
 * <p>
 * Elle s'applique aux comptes sans découvert autorisé, ou dès qu'une
 * opération ne respecte pas la contrainte de solde minimal autorisé.
 * </p>
 */
public class InsufficientBalanceException extends IllegalArgumentException{

    public InsufficientBalanceException(String message) {
        super(message);
    }
    
}
