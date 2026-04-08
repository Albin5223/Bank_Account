package fr.albin.bank_account.infrastructure.adapter.out.persistence.entity;

import java.util.List;
import java.util.UUID;

import fr.albin.bank_account.domain.model.User;
import fr.albin.bank_account.domain.model.enums.Role;
import fr.albin.bank_account.domain.model.interfaces.BankAccountImpl;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;

/**
 * Classe qui représente l'entité User dans la base de données
 */
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BankAccountEntity> bankAccounts;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Role> roles;

    protected UserEntity() {
    }

    public UserEntity(User user){
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPassword();

        this.roles = user.getRoles().stream().toList();

        this.bankAccounts = user.getBankAccounts().stream().map(BankAccountEntity::new).toList();
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }   
    
    
    public User toUser() {
        List<BankAccountImpl> convertedAccounts = this.bankAccounts.stream()
            .map(BankAccountEntity::toBankAccount)
            .toList();
        
        return new User(
            this.username,
            this.password,
            this.email,
            this.roles,
            convertedAccounts
        );
    }

    public void addBankAccount(BankAccountEntity bankAccount) {
        this.bankAccounts.add(bankAccount);
        bankAccount.setUser(this);
    }

    public void removeBankAccount(BankAccountEntity bankAccount) {
        this.bankAccounts.remove(bankAccount);
        bankAccount.setUser(null);
    }
}
