package fr.albin.bank_account.infrastructure.adapter.out.persistence.adapter;

import java.util.Optional;

import fr.albin.bank_account.domain.model.BankAccount;
import fr.albin.bank_account.domain.port.out.LoadBankAccountPort;
import fr.albin.bank_account.infrastructure.adapter.out.persistence.entity.BankAccountEntity;
import fr.albin.bank_account.infrastructure.adapter.out.persistence.repository.BankAccountRepository;

import org.springframework.stereotype.Component;

/**
 * Adapter de persistance pour charger un compte bancaire à partir de la base de données.
 * Cette classe implémente le port LoadBankAccountPort et utilise BankAccountRepository
 * pour accéder aux données. Elle convertit les entités BankAccountEntity en objets BankAccount
 * du domaine.
 */
@Component
public class LoadBankAccountAdapter implements LoadBankAccountPort {
    
    private final BankAccountRepository bankAccountRepository;

    public LoadBankAccountAdapter(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public Optional<BankAccount> loadByAccountNumber(String accountNumber) {
        return Optional.ofNullable(bankAccountRepository.findByAccountNumber(accountNumber))
                .map(BankAccountEntity::toBankAccount);
    }
    
}
