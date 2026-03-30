package fr.albin.bank_account.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import fr.albin.bank_account.domain.exception.InvalidAmountException;
import fr.albin.bank_account.domain.model.enums.TypeAccount;
import fr.albin.bank_account.domain.model.enums.TypeOperation;
import fr.albin.bank_account.domain.model.interfaces.BankAccountImpl;

/**
 * Représente un compte bancaire avec une autorisation de découvert.
 * <p>
 * Un compte bancaire avec autorisation de découvert permet de faire des dépôts et des retraits
 * tant que le solde du compte ne devient pas inférieur à la limite de découvert autoriséee.
 * </p>
 */
public class BankAccountOverdraft implements BankAccountImpl {

    protected double overdraftLimit;
    protected String accountNumber;
    protected double balance;
    protected final List<Operation> operations = new java.util.ArrayList<>();
    

    public BankAccountOverdraft(String number, double balance, double overdraftLimit) {
        if (balance < -overdraftLimit) {
            throw new InvalidAmountException("Le solde initial ne peut pas être inférieur à la limite de découvert");
        }
        if (overdraftLimit < 0) {
            throw new InvalidAmountException("La limite de découvert ne peut pas être négative");
        }
        this.overdraftLimit = overdraftLimit;
        this.accountNumber = number;
        this.balance = balance;
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
        return TypeAccount.BANK_ACCOUNT_OVERDRAFT;
    }


    @Override
    public double getOverdraftLimit() {
        return overdraftLimit;
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
