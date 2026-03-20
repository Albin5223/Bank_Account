package fr.albin.bank_account.domain.port.out;

import fr.albin.bank_account.domain.model.BankAccount;


/**
 * Interface qui permet de sauvegarder un compte bancaire
 */
public interface SaveBankAccountPort {
    void save(BankAccount account);
}