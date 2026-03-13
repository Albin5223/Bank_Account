package fr.albin.bank_account.domain.exception;

/**
 * Exception métier levée lorsqu'un retrait dépasse la limite de découvert.
 * <p>
 * Elle est spécifique aux comptes disposant d'une autorisation de découvert
 * et signale qu'une opération conduirait à un solde inférieur au minimum permis.
 * </p>
 */
public class OverdraftLimitExceededException extends RuntimeException {

    public OverdraftLimitExceededException(String message) {
        super(message);
    }
}
