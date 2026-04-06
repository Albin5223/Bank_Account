package fr.albin.bank_account.domain.port.in.bankAccountUseCase;

/**
 * Interface qui permet de créer un compte bancaire autorisé au découvert
 */
public interface CreateBankAccountOverdraftUseCase {
    String createBankAccountOverdraft(double balance, double overdraft);
}
