package fr.albin.bank_account.domain.model;

import java.util.List;

import fr.albin.bank_account.domain.exception.OverdraftLimitExceededException;
import fr.albin.bank_account.domain.model.enums.TypeAccount;

/**
 * Factory pour créer et recharger les comptes bancaires.
 */
public final class BankAccountFactory {

    private BankAccountFactory() {
    }

    public static BankAccount createStandard(String accountNumber, double balance) {
        return new BankAccount(accountNumber, balance);
    }

    public static SavingsAccount createSavings(String accountNumber, double balance, double depositCap) {
        return new SavingsAccount(accountNumber, balance, depositCap);
    }

    public static BankAccountOverdraft createOverdraft(String accountNumber, double balance, double overdraftLimit) {
        return new BankAccountOverdraft(accountNumber, balance, overdraftLimit);
    }

    public static BankAccount rehydrate(
            String accountNumber,
            double balance,
            double overdraftLimit,
            double depositLimit,
            TypeAccount accountType,
            List<Operation> operations) {

        BankAccount account;

        if (TypeAccount.SAVINGS_ACCOUNT.equals(accountType)) {
            account = new SavingsAccount(accountNumber, balance, depositLimit);
            applyOperations(account, operations);
            return account;
        }

        if (TypeAccount.BANK_ACCOUNT_OVERDRAFT.equals(accountType)) {
            account = rehydrateOverdraft(accountNumber, balance, overdraftLimit);
            applyOperations(account, operations);
            return account;
        }

        account = new BankAccount(accountNumber, balance);
        applyOperations(account, operations);
        return account;
    }

    private static BankAccountOverdraft rehydrateOverdraft(String accountNumber, double balance, double overdraftLimit) {
        if (balance < -overdraftLimit) {
            throw new OverdraftLimitExceededException("Limite de découvert dépassée lors du chargement du compte");
        }

        double initialBalance = Math.max(balance, 0);
        BankAccountOverdraft account = new BankAccountOverdraft(accountNumber, initialBalance, overdraftLimit);
        account.balance = balance;
        return account;
    }

    private static void applyOperations(BankAccount account, List<Operation> operations) {
        if (operations != null && !operations.isEmpty()) {
            account.loadOperations(operations);
        }
    }
}
