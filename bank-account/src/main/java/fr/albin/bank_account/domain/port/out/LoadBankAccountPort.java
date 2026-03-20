package fr.albin.bank_account.domain.port.out;

import java.util.Optional;

import fr.albin.bank_account.domain.model.BankAccount;
/**
 * Interface qui permet de récupérer un compte bancaire
 */
public interface LoadBankAccountPort {
    Optional<BankAccount> loadByAccountNumber(String accountNumber);
}