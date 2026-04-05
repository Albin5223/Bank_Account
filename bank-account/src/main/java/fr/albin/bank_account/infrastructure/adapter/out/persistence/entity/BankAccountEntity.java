package fr.albin.bank_account.infrastructure.adapter.out.persistence.entity;


import java.util.ArrayList;
import java.util.List;

import fr.albin.bank_account.domain.model.BankAccountFactory;
import fr.albin.bank_account.domain.model.Operation;
import fr.albin.bank_account.domain.model.enums.TypeAccount;
import fr.albin.bank_account.domain.model.interfaces.BankAccountImpl;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entité JPA représentant un compte bancaire. Cette classe est utilisée pour la persistance
 * des comptes bancaires dans la base de données
 */
@Entity
@Table(name = "bank_account")
public class BankAccountEntity {


    @Id
    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "balance", nullable = false)
    private double balance;

    @Column(name = "overdraft_limit", nullable = false)
    private double overdraftLimit;

    @Column(name = "deposit_limit", nullable = false)
    private double depositLimit;

    @Column(name = "account_type", nullable = false)
    private TypeAccount accountType;

    @OneToMany(mappedBy = "bankAccount", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OperationEntity> operations = new ArrayList<>();

    protected BankAccountEntity() {
    }

    public BankAccountEntity(String accountNumber, double balance, double overdraftLimit, double depositLimit, TypeAccount accountType) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.overdraftLimit = overdraftLimit;
        this.depositLimit = depositLimit;
        this.accountType = accountType;
    }

    public BankAccountEntity(BankAccountImpl bankAccount){
        this.accountNumber = bankAccount.getAccountNumber();
        this.balance = bankAccount.getBalance();

        setOperations(bankAccount.getOperations());

        this.depositLimit = bankAccount.getDepositLimit();
        this.overdraftLimit = bankAccount.getOverdraftLimit();
        this.accountType = bankAccount.getAccountType();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public double getDepositLimit() {
        return depositLimit;
    }


    public BankAccountImpl toBankAccount() {
        return BankAccountFactory.rehydrate(
                accountNumber,
                balance,
                overdraftLimit,
                depositLimit,
                accountType,
                toDomainOperations());
    }

    private void setOperations(List<Operation> domainOperations) {
        this.operations.clear();

        for (Operation domainOperation : domainOperations) {
            OperationEntity operationEntity = new OperationEntity(domainOperation);
            operationEntity.setBankAccount(this);
            this.operations.add(operationEntity);
        }
    }

    private List<Operation> toDomainOperations() {
        return operations.stream()
                .map(OperationEntity::toOperation)
                .toList();
    }
    


    
    
}
