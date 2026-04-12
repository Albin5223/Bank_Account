package fr.albin.bank_account.domain.port.in.bankAccountUseCase;


/**
 * Interface qui permet de créer un compte épargne
 */
public interface CreateSavingsAccountUseCase {
    String createSavingsAccount(double balance,double depositCap, String user);
}
