package fr.albin.bank_account.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import fr.albin.bank_account.domain.model.enums.TypeAccount;

/**
 * Représente un relevé de compte émis à une date donnée.
 * <p>
 * Le relevé contient le type de compte, le solde au moment de l'émission
 * ainsi que la liste des opérations retenues sur la période demandée.
 * </p>
 */
public class AccountStatement {

    private final TypeAccount accountType;
    private final double balance;
    private final LocalDateTime emissionDate;
    private final List<Operation> operations;

    public AccountStatement(TypeAccount accountType, double balance, LocalDateTime emissionDate, List<Operation> operations) {
        this.accountType = accountType;
        this.balance = balance;
        this.emissionDate = emissionDate;
        this.operations = List.copyOf(operations);
    }

    public TypeAccount getAccountType() {
        return accountType;
    }

    public double getBalance() {
        return balance;
    }

    public LocalDateTime getEmissionDate() {
        return emissionDate;
    }

    public List<Operation> getOperations() {
        return operations;
    }
}
