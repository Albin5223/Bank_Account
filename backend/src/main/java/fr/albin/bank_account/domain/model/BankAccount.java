package fr.albin.bank_account.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import fr.albin.bank_account.domain.exception.InsufficientBalanceException;
import fr.albin.bank_account.domain.model.enums.TypeAccount;
import fr.albin.bank_account.domain.model.enums.TypeOperation;
import fr.albin.bank_account.domain.model.interfaces.BankAccountImpl;

/**
 * Représente un compte bancaire standard sans découvert possible.
 * <p>
 * Un compte bancaire standard permet de faire des dépôts et des retraits
 * tant que le solde du compte ne devient pas négatif.
 * </p>
 */
public class BankAccount implements BankAccountImpl {

    protected String accountNumber;
    protected double balance;
    protected User user;
    protected final List<Operation> operations;

    public BankAccount(String number, double balance){
        if(balance<0){
            throw new InsufficientBalanceException("Solde insuffisant lors de la création du compte");
        }
        this.accountNumber = number;
        this.balance = balance;
        this.operations = new ArrayList<>();
        this.operations.add(new Operation(LocalDateTime.now(), TypeOperation.CREATE, balance));
    }

    @Override
    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public double getDepositLimit() {
        return Double.MAX_VALUE;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public TypeAccount getAccountType() {
        return TypeAccount.BANK_ACCOUNT;
    }

    @Override
    public double getOverdraftLimit() {
        return 0;
    }

    @Override
    public List<Operation> getOperations() {
        return new ArrayList<>(operations);
    }

    @Override
    public void addOperation(Operation operation) {
        this.operations.add(operation);
    }

    @Override
    public void performDeposit(double amount) {
        this.balance += amount;
    }

    @Override
    public void performWithdraw(double amount) {
        this.balance -= amount;
    }

    @Override
    public void clearOperations() {
        this.operations.clear();
    }

}
