package fr.albin.bank_account.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fr.albin.bank_account.domain.exception.InvalidAmountException;
import fr.albin.bank_account.domain.exception.InsufficientBalanceException;
import fr.albin.bank_account.domain.model.enums.TypeAccount;
import fr.albin.bank_account.domain.model.enums.TypeOperation;

public class BankAccount {
    protected String accountNumber;
    protected double balance;
    protected final List<Operation> operations;


    /**
    * Cette classe représent un compte bancaire sans découvert possible
    * <p>
    * @param number est le numéro du compte
    * @param balance est le solde de départ du compte
    * </p>
    */
    public BankAccount(String number, double balance){
        if(balance<0){
            throw new InsufficientBalanceException("Solde insuffisant lors de la création du compte");
        }
        this.accountNumber = number;
        this.balance = balance;
        this.operations = new ArrayList<>();
        this.operations.add(new Operation(LocalDateTime.now(), TypeOperation.CREATE, balance));
    }


    public String getAccountNumber(){
        return accountNumber;
    }

    public double getBalance(){
        return balance;
    }

    public List<Operation> getOperations() {
        return List.copyOf(operations);
    }

    public void deposit(double depot){
        if (depot <= 0) {
            throw new InvalidAmountException("Le montant du dépôt doit être strictement positif");
        }
        balance += depot;
        operations.add(new Operation(LocalDateTime.now(), TypeOperation.DEPOSIT, depot));
    }

    public void withdraw(double retrait){
        if (retrait <= 0) {
            throw new InvalidAmountException("Le montant du retrait doit être strictement positif");
        }
        if (balance-retrait<0){
            throw new InsufficientBalanceException("Solde insuffisant");
        }
        balance -= retrait;
        operations.add(new Operation(LocalDateTime.now(), TypeOperation.WITHDRAWAL, retrait));
    }

    protected TypeAccount getAccountType() {
        return TypeAccount.BankAccount;
    }

    public AccountStatement emitStatement(LocalDateTime emissionDate) {
        LocalDateTime startDate = emissionDate.minusMonths(1);

        List<Operation> filteredOperations = operations.stream()
            .filter(operation -> !operation.getDate().isBefore(startDate) && !operation.getDate().isAfter(emissionDate))
            .sorted(Comparator.comparing(Operation::getDate).reversed())
            .toList();

        return new AccountStatement(getAccountType(), balance, emissionDate, filteredOperations);
    }

    public AccountStatement emitStatement() {
        return emitStatement(LocalDateTime.now());
    }
}
