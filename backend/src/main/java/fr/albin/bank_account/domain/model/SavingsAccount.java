package fr.albin.bank_account.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import fr.albin.bank_account.domain.exception.DepositCapExceededException;
import fr.albin.bank_account.domain.exception.InvalidAmountException;
import fr.albin.bank_account.domain.model.enums.TypeAccount;
import fr.albin.bank_account.domain.model.enums.TypeOperation;
import fr.albin.bank_account.domain.model.interfaces.BankAccountImpl;

/**
 * Représente un compte d'épargne sans autorisation de découvert et possédant un plafond de dépôt.
 * <p>
 * Un compte d'épargne permet de faire des dépôts et des retraits tant que le solde du compte ne dépasse pas le plafond de dépôt.
 * </p>
 */
public class SavingsAccount implements BankAccountImpl {

    protected double depositCap;
    protected String accountNumber;
    protected double balance;
    protected User user;
    protected final List<Operation> operations = new ArrayList<>();


    public SavingsAccount(String number, double balance, double depositCap) {
        if(balance < 0) {
            throw new InvalidAmountException("Le solde initial ne peut pas être négatif");
        }
        
        if (depositCap < 0) {
            throw new IllegalArgumentException("Le plafond de dépôt ne peut pas être négatif");
        }
        if(balance>depositCap){
            throw new DepositCapExceededException("Plafond dépassé lors de la création du compte");
        }
        this.depositCap = depositCap;
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
        return depositCap;
    }


    @Override
    public double getBalance() {
        return balance;
    }


    @Override
    public TypeAccount getAccountType() {
        return TypeAccount.SAVINGS_ACCOUNT;
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
