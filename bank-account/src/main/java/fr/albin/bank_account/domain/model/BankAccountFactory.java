package fr.albin.bank_account.domain.model;

import java.util.List;

import fr.albin.bank_account.domain.model.enums.TypeAccount;
import fr.albin.bank_account.domain.model.interfaces.BankAccountImpl;

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

    public static BankAccountImpl rehydrate(
            String accountNumber,
            double balance,
            double overdraftLimit,
            double depositLimit,
            TypeAccount accountType,
            List<Operation> operations) {

        BankAccountImpl account;
        
        switch (accountType) {
            case SAVINGS_ACCOUNT:
                account = new SavingsAccount(accountNumber, balance, depositLimit);
                break;
            case BANK_ACCOUNT_OVERDRAFT:
                account = new BankAccountOverdraft(accountNumber, 0, overdraftLimit);
                ((BankAccountOverdraft) account).balance = balance; // Met à jour le solde après la création du compte
                break;
            default:
                account = new BankAccount(accountNumber, balance);
        }
        applyOperations(account, operations);
        return account;
    }

    private static void applyOperations(BankAccountImpl account, List<Operation> operations) {
        if (operations != null && !operations.isEmpty()) {
            account.loadOperations(operations);
        }
    }
}
