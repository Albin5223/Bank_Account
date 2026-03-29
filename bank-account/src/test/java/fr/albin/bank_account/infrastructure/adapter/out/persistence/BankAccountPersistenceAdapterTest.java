package fr.albin.bank_account.infrastructure.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import fr.albin.bank_account.domain.model.BankAccount;
import fr.albin.bank_account.domain.model.BankAccountOverdraft;
import fr.albin.bank_account.domain.model.SavingsAccount;
import fr.albin.bank_account.infrastructure.adapter.out.persistence.entity.BankAccountEntity;
import fr.albin.bank_account.infrastructure.adapter.out.persistence.repository.BankAccountRepository;


@SpringBootTest
@Transactional // Annule les changements en base après chaque test
@Rollback      // S'assure que la base reste propre entre les tests
public class BankAccountPersistenceAdapterTest {
    
    @Autowired
    private BankAccountRepository repository;

    @Test
    @DisplayName("Devrait renvoyer le même type que le compte créer : Compte avec découvert")
    void should_create_load_overdraft_account() {
        BankAccountOverdraft original = new BankAccountOverdraft("ACC-003", 100.0, 80.0);

        repository.save(new BankAccountEntity(original));
        BankAccount reloaded = repository.findByAccountNumber("ACC-003").toBankAccount();

        assertInstanceOf(BankAccountOverdraft.class, reloaded);
        assertEquals(100.0, reloaded.getBalance());
        System.out.println(reloaded.getOperations());
        assertEquals(1, reloaded.getOperations().size());
    }


    @Test
    @DisplayName("Devrait pouvoir recharger un compte avec un solde négatif (découvert)")
    void should_create_load_overdraft_account_negative_balance() {
        BankAccountOverdraft original = new BankAccountOverdraft("ACC-003", 100.0, 80.0);
        original.withdraw(150.0); // Solde devient -50, dans la limite du découvert

        repository.save(new BankAccountEntity(original));
        BankAccount reloaded = repository.findByAccountNumber("ACC-003").toBankAccount();

        assertInstanceOf(BankAccountOverdraft.class, reloaded);
        assertEquals(-50.0, reloaded.getBalance());
        System.out.println(reloaded.getOperations());
        assertEquals(2, reloaded.getOperations().size());
    }

    @Test
    @DisplayName("Devrait renvoyer le même type que le compte créer : Compte épargne")
    void should_create_load_savings_account(){
        SavingsAccount sa = new SavingsAccount("ACC-004", 1200, 1500);
        repository.save(new BankAccountEntity(sa));

        BankAccount reloaded = repository.findByAccountNumber("ACC-004").toBankAccount();
        assertInstanceOf(SavingsAccount.class, reloaded);
        assertEquals(1200, reloaded.getBalance());
        assertEquals(1500, ((SavingsAccount) reloaded).getDepositCap());
        assertEquals(1, reloaded.getOperations().size());

    }


    @Test
    @DisplayName("Devrait sauvegarder et trouver un compte avec ses opérations")
    void should_save_and_find_account_with_operations() {
        BankAccount account = new BankAccount("ACC-001", 100.0);
        account.deposit(40.0);
        account.withdraw(20.0);

        repository.save(new BankAccountEntity(account));
        BankAccountEntity found = repository.findByAccountNumber("ACC-001");

        assertNotNull(found);
        BankAccount reloaded = found.toBankAccount();
        assertEquals(120.0, reloaded.getBalance());
        assertEquals(3, reloaded.getOperations().size());
    }

    @Test
    @DisplayName("Devrait retourner null si le compte n'existe pas")
    void should_return_null_if_account_not_found() {
        BankAccountEntity found = repository.findByAccountNumber("NON-EXISTENT");
        assertEquals(null, found);
    }
}
