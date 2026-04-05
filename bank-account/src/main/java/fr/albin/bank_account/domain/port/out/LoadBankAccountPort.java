package fr.albin.bank_account.domain.port.out;

import java.util.Optional;

import fr.albin.bank_account.domain.model.interfaces.BankAccountImpl;
/**
 * Interface qui permet de récupérer un compte bancaire
 */
public interface LoadBankAccountPort {
    Optional<BankAccountImpl> loadByAccountNumber(String accountNumber);
}