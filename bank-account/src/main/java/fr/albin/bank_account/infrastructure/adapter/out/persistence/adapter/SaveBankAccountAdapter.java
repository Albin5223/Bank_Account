package fr.albin.bank_account.infrastructure.adapter.out.persistence.adapter;

import fr.albin.bank_account.domain.model.BankAccount;
import fr.albin.bank_account.domain.port.out.SaveBankAccountPort;
import fr.albin.bank_account.infrastructure.adapter.out.persistence.entity.BankAccountEntity;
import fr.albin.bank_account.infrastructure.adapter.out.persistence.repository.BankAccountRepository;

import org.springframework.stereotype.Component;

/**
 * Adapter de persistance pour sauvegarder un compte bancaire dans la base de données.
 * Cette classe implémente le port SaveBankAccountPort et utilise BankAccountRepository
 * pour accéder aux données. Elle convertit les objets BankAccount du domaine en entités
 * BankAccountEntity avant de les sauvegarder.
 */
@Component
public class SaveBankAccountAdapter implements SaveBankAccountPort {

    
    private final BankAccountRepository bankAccountRepository;

    public SaveBankAccountAdapter(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public void save(BankAccount bankAccount) {
        BankAccountEntity bankAccountEntity = new BankAccountEntity(bankAccount);
        bankAccountRepository.save(bankAccountEntity);
    }
}
