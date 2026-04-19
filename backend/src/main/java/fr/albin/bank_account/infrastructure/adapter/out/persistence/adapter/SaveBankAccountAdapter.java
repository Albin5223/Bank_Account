package fr.albin.bank_account.infrastructure.adapter.out.persistence.adapter;

import java.util.NoSuchElementException;

import fr.albin.bank_account.domain.model.interfaces.BankAccountImpl;
import fr.albin.bank_account.domain.port.out.bankAccountPort.SaveBankAccountPort;
import fr.albin.bank_account.infrastructure.adapter.out.persistence.entity.BankAccountEntity;
import fr.albin.bank_account.infrastructure.adapter.out.persistence.repository.BankAccountRepository;
import fr.albin.bank_account.infrastructure.adapter.out.persistence.repository.UserRepository;

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
    private final UserRepository userRepository;

    public SaveBankAccountAdapter(BankAccountRepository bankAccountRepository, UserRepository userRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void save(BankAccountImpl bankAccount) {
        BankAccountEntity bankAccountEntity = new BankAccountEntity(bankAccount);
        bankAccountRepository.save(bankAccountEntity);
    }

    @Override
    public void save(BankAccountImpl bankAccount, String username) {
        BankAccountEntity bankAccountEntity = new BankAccountEntity(bankAccount);

        var userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            throw new NoSuchElementException("Utilisateur introuvable : " + username);
        }

        bankAccountEntity.setUser(userEntity);
        bankAccountRepository.save(bankAccountEntity);
    }
}
