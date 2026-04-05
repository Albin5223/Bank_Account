package fr.albin.bank_account.domain.port.in;

/**
 * Interface qui permet de créer un compte bancaire asique
 */
public interface CreateBankAccountUseCase {
    String createBankAccount(double balance);
}
