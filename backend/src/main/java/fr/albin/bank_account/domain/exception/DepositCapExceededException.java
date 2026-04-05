package fr.albin.bank_account.domain.exception;

/**
 * Exception métier levée lorsque le plafond d'un livret est dépassé.
 * <p>
 * Cette exception est utilisée lors de la création d'un compte épargne
 * ou lors d'un dépôt qui ferait dépasser le plafond autorisé.
 * </p>
 */
public class DepositCapExceededException extends IllegalArgumentException{

    public DepositCapExceededException(String message) {
        super(message);
    }

}
