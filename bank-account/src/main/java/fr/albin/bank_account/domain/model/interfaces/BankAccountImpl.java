package fr.albin.bank_account.domain.model.interfaces;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import fr.albin.bank_account.domain.exception.DepositCapExceededException;
import fr.albin.bank_account.domain.exception.InsufficientBalanceException;
import fr.albin.bank_account.domain.exception.InvalidAmountException;
import fr.albin.bank_account.domain.model.AccountStatement;
import fr.albin.bank_account.domain.model.Operation;
import fr.albin.bank_account.domain.model.enums.TypeAccount;
import fr.albin.bank_account.domain.model.enums.TypeOperation;

/**
 * Interface qui permet de définir un compte bancaire standart, avec du découvert et un compte épargne
 */
public interface BankAccountImpl {
    
    /**
     * Méthode qui permet de récupérer le numéro de compte
     */
    public String getAccountNumber();

    /**
     * Méthode qui permet de récupérer le plafond de dépôt
     */
    public double getDepositLimit();

    /**
     * Méthode qui permet de récupérer le solde du compte bancaire
     */
    public double getBalance();

    /**
     * Méthode qui permet de récupérer le type du compte bancaire
     */
    public TypeAccount getAccountType();

    /**
     * Méthode qui permet de récupérer la limite de découvert
     */
    public double getOverdraftLimit();

    /**
     * Méthode qui permet de récupérer une copie de la liste des opérations du compte bancaire
     */
    public List<Operation> getOperations();

    /**
     * Méthode qui permet d'ajouter une opération à la liste des opérations
     */
    public void addOperation(Operation operation);

    /**
     * Méthode qui permet de vérifier si un dépôt est valide ou pas
     */
    public default boolean checkDepositValid(double amount){
        if (amount <= 0) {
            throw new InvalidAmountException("Le montant du dépôt doit être strictement positif");
        }
        if(getBalance() + amount > getDepositLimit()){
            throw new DepositCapExceededException("Plafond dépassé");
        }
        return true;

    }

    /**
     * Méthode qui permet de faire un dépôt sur le compte bancaire
     */
    void performDeposit(double amount);

    /**
     * Méthode qui permet de faire un dépôt
     */
    default void deposit(double amount) {
        if (checkDepositValid(amount)) {
            addOperation(new Operation(LocalDateTime.now(), TypeOperation.DEPOSIT, amount));
            performDeposit(amount);
        }
    }

    /**
     * Méthode qui permet de vérifier si un retrait est valide ou pas
     */
    public default boolean checkWithdrawValid(double amount){
        if (amount <= 0) {
            throw new InvalidAmountException("Le montant du retrait doit être strictement positif");
        }
        if (getBalance()-amount+getOverdraftLimit() < 0){
            throw new InsufficientBalanceException("Solde insuffisant");
        }
        return true;
    }

    /**
     * Méthode qui permet de faire un retrait sur le compte bancaire
     */
    void performWithdraw(double amount);

    /**
     * Méthode qui permet de faire un retrait
     */
    default void withdraw(double amount) {
        if (checkWithdrawValid(amount)) {
            addOperation(new Operation(LocalDateTime.now(), TypeOperation.WITHDRAWAL, amount));
            performWithdraw(amount);
        }
    }

    /**
     * Méthode qui permet d'émettre un relevé de compte à une date donnée
     * 
     */
    public default AccountStatement emitStatement(LocalDateTime emissionDate) {
        LocalDateTime startDate = emissionDate.minusMonths(1);

        List<Operation> filteredOperations = getOperations().stream()
            .filter(operation -> !operation.getDate().isBefore(startDate) && !operation.getDate().isAfter(emissionDate))
            .sorted(Comparator.comparing(Operation::getDate).reversed())
            .toList();

        return new AccountStatement(getAccountType(), getBalance(), emissionDate, filteredOperations);
    }

    /**
     * Méthode qui permet de vider la liste des opérations du compte bancaire
     */
    public void clearOperations();


    /**
     * Méthode qui permet de charger les opérations d'un compte bancaire à partir d'une liste d'opérations
     */
    public default void loadOperations(List<Operation> persistedOperations){
        clearOperations();
        for(Operation operation : persistedOperations){
            addOperation(operation);
        }
    }












    
}
