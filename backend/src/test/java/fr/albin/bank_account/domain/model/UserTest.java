package fr.albin.bank_account.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.albin.bank_account.domain.model.enums.Role;

@DisplayName("User")
public class UserTest {

    @Test
    @DisplayName("Crée un utilisateur avec un format valide")
    void should_create_user_with_valid_format() {
        String username = "john_doe";
        String password = "password123";
        String email = "john.doe@example.com";
        User user = new User(username, password, email);

        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
    }

    @Test
    @DisplayName("Lance une exception pour un email invalide")
    void should_throw_exception_for_invalid_email() {
        String username = "john_doe";
        String password = "password123";
        String invalidEmail = "john.doeexample.com"; // Email sans '@'
        assertThrows(IllegalArgumentException.class, () -> {
            new User(username, password, invalidEmail);
        });
    }

    @Test
    @DisplayName("Lance une exception pour un nom d'utilisateur vide")
    void should_throw_exception_for_empty_username() {
        String password = "password123";
        String email = "john.doe@example.com";
        assertThrows(IllegalArgumentException.class, () -> {
            new User("", password, email);
        });
    }

    @Test
    @DisplayName("Lance une exception pour un mot de passe vide")
    void should_throw_exception_for_empty_password() {
        String username = "john_doe";
        String email = "john.doe@example.com";
        assertThrows(IllegalArgumentException.class, () -> {
            new User(username, "", email);
        });
    }

    @Test
    @DisplayName("Lance une exception pour un email vide")
    void should_throw_exception_for_empty_email() {
        String username = "john_doe";
        String password = "password123";
        assertThrows(IllegalArgumentException.class, () -> {
            new User(username, password, "");
        });
    }

    @Test
    @DisplayName("Ajoute et supprime un rôle pour l'utilisateur")
    void should_add_and_remove_role() {
        User user = new User("john_doe", "password123", "john.doe@example.com");
        Role adminRole = Role.ADMIN;

        user.addRole(adminRole);
        assertEquals(1, user.getRole().size());
        assertEquals(adminRole, user.getRole().get(0));
        user.removeRole(adminRole);
        assertEquals(0, user.getRole().size());
    }

    @Test
    @DisplayName("Ajoute un compte bancaire à l'utilisateur")
    void should_add_bank_account() {
        User user = new User("john_doe", "password123", "john.doe@example.com");
        BankAccount bankAccount = new BankAccount("123456789", 1000.0);
        user.addBankAccount(bankAccount);
        assertEquals(1, user.getBankAccounts().size());
        assertEquals(bankAccount.getAccountNumber(), user.getBankAccounts().get(0).getAccountNumber());
    }

    @Test
    @DisplayName("Supprime un compte bancaire de l'utilisateur")
    void should_remove_bank_account() {
        User user = new User("john_doe", "password123", "john.doe@example.com");
        BankAccount bankAccount = new BankAccount("123456789", 1000.0);
        user.addBankAccount(bankAccount);
        user.removeBankAccount(bankAccount);
        assertEquals(0, user.getBankAccounts().size());
    }
}
