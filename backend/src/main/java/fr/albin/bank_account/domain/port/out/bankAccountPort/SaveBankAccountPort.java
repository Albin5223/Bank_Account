package fr.albin.bank_account.domain.port.out.bankAccountPort;

import fr.albin.bank_account.domain.model.interfaces.BankAccountImpl;


/**
 * Interface qui permet de sauvegarder un compte bancaire
 */
public interface SaveBankAccountPort {
    void save(BankAccountImpl account);
}