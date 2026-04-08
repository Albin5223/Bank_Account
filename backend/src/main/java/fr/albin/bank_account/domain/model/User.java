package fr.albin.bank_account.domain.model;

import java.util.ArrayList;
import java.util.List;

import fr.albin.bank_account.domain.model.enums.Role;
import fr.albin.bank_account.domain.model.interfaces.BankAccountImpl;
import lombok.Getter;

/**
 * Représente un utilisateur de l'application bancaire.
 */
@Getter
public class User {
    

    private String username;
    private String password; // Mot de passe haché
    private String email;
    private List<Role> roles;

    private List<BankAccountImpl> bankAccounts;

    public User(String username, String password, String email, List<Role> role, List<BankAccountImpl> bankAccounts) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur ne peut pas être vide.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide.");
        }
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide.");
        }
        // Validation de l'email (format basique)
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("L'email n'est pas dans un format valide.");
        }
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = role;
        this.bankAccounts = bankAccounts;
    }

    public User(String username, String password, String email) {
        this(username, password, email, new ArrayList<>(), new ArrayList<>());
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public List<Role> getRole(){
        return new ArrayList<>(roles);
    }

    public void addBankAccount(BankAccountImpl bankAccount) {
        this.bankAccounts.add(bankAccount);
    }

    public void removeBankAccount(BankAccountImpl bankAccount) {
        this.bankAccounts.remove(bankAccount);
    }

    public List<BankAccountImpl> getBankAccounts(){
        return new ArrayList<>(bankAccounts);
    }

}
