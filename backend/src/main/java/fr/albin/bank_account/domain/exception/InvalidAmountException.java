package fr.albin.bank_account.domain.exception;

/**
 * Exception métier levée lorsqu'un montant est invalide.
 * <p>
 * Cette exception est utilisée lorsque le montant d'une opération
 * (dépôt, retrait, plafond, limite de découvert) est nul ou négatif,
 * selon les règles du domaine.
 * </p>
 */
public class InvalidAmountException extends IllegalArgumentException {

    public InvalidAmountException(String message) {
        super(message);
    }
}
