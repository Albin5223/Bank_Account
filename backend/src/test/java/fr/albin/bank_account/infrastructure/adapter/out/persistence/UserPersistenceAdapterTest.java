package fr.albin.bank_account.infrastructure.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import fr.albin.bank_account.domain.model.BankAccount;
import fr.albin.bank_account.domain.model.SavingsAccount;
import fr.albin.bank_account.domain.model.User;
import fr.albin.bank_account.domain.model.enums.Role;
import fr.albin.bank_account.infrastructure.adapter.out.persistence.entity.UserEntity;
import fr.albin.bank_account.infrastructure.adapter.out.persistence.repository.UserRepository;
import jakarta.persistence.EntityManager;


@SpringBootTest
@Transactional // Annule les changements en base après chaque test
@Rollback 
public class UserPersistenceAdapterTest {
    
    @Autowired
    private UserRepository repository;

    @Autowired
    private EntityManager entityManager;


    private User setupUser() {
        String mail = "Albin@paris.com";
        String username = "albin";
        String password = "password123";
        User user = new User(username, password, mail);
        return user;
        
    }


    @Test
    @DisplayName("Devrait enregistrer un utilisateur et le retrouver en base avec l'email")
    void should_save_and_load_user_by_email() {
        User user = setupUser();
        repository.save(new UserEntity(user));

        User reloaded = repository.findByEmail(user.getEmail()).toUser();
        assertNotNull(reloaded);
        assertEquals(user.getUsername(), reloaded.getUsername());
        assertEquals(user.getEmail(), reloaded.getEmail());
        assertEquals(user.getPassword(), reloaded.getPassword());
    }

    @Test
    @DisplayName("Devrait enregistrer un utilisateur et le retrouver en base avec le nom d'utilisateur")
    void should_save_and_load_user_by_username() {
        User user = setupUser();
        repository.save(new UserEntity(user));

        User reloaded = repository.findByUsername(user.getUsername()).toUser();
        assertNotNull(reloaded);
        assertEquals(user.getUsername(), reloaded.getUsername());
        assertEquals(user.getEmail(), reloaded.getEmail());
        assertEquals(user.getPassword(), reloaded.getPassword());
    }

    @Test
    @DisplayName("Devrait retourner null si l'utilisateur n'existe pas pour l'email")
    void should_return_null_for_nonexistent_email() {
        UserEntity reloaded = repository.findByEmail("nonexistent@example.com");
        assertEquals(null, reloaded);
    }

    @Test
    @DisplayName("Enregistrer deux utilisateurs avec le même nom d'utilisateur devrait renoyer une exception")
    void should_throw_exception_when_saving_duplicate_user() {
        User user1 = setupUser();
        repository.save(new UserEntity(user1));
        entityManager.flush();
        assertThrows(Exception.class, () -> {
            repository.save(new UserEntity(user1));
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Enregistrer deux utilisateurs avec le même email devrait renvoyer une exception")
    void should_throw_exception_when_saving_duplicate_email() {
        User user1 = setupUser();
        repository.save(new UserEntity(user1));
        entityManager.flush();
        assertThrows(Exception.class, () -> {
            User user2 = setupUser();
            user2.setEmail(user1.getEmail()); // Utiliser le même email
            repository.save(new UserEntity(user2));
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Devrait pouvoir retourner les mêmes droits que ceux de l'utilisateur enregistré")
    void should_return_same_roles_as_saved_user() {
        User user = setupUser();
        user.addRole(Role.ADMIN);
        user.addRole(Role.USER);

        repository.save(new UserEntity(user));
        User reloaded = repository.findByEmail(user.getEmail()).toUser();
        assertNotNull(reloaded);
        assertEquals(2, reloaded.getRoles().size());
    }

    @Test
    @DisplayName("Devrait pouvoir enregistrer tous les comptes d'un utilisateur et les retrouver lors du chargement")
    void should_save_and_load_user_with_accounts() {
        User user = setupUser();
        user.addRole(Role.USER);
        user.addBankAccount(new BankAccount("ACC-001", 100.0));
        user.addBankAccount(new SavingsAccount("ACC-002", 200.0, 500.0));
        repository.save(new UserEntity(user));

        User reloaded = repository.findByEmail(user.getEmail()).toUser();
        assertNotNull(reloaded);
        assertEquals(2, reloaded.getBankAccounts().size());
        assertEquals("ACC-001", reloaded.getBankAccounts().get(0).getAccountNumber());
        assertEquals("ACC-002", reloaded.getBankAccounts().get(1).getAccountNumber());
    }


}
