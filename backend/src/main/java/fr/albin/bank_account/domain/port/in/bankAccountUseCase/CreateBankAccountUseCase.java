package fr.albin.bank_account.domain.port.in.bankAccountUseCase;

/**
 * Interface qui permet de créer un compte bancaire asique
 */
public interface CreateBankAccountUseCase {
    String createBankAccount(double balance);
}
