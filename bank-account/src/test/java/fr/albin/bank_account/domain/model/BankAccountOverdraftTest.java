package fr.albin.bank_account.domain.model;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.albin.bank_account.domain.exception.InvalidAmountException;
import fr.albin.bank_account.domain.exception.InsufficientBalanceException;
import fr.albin.bank_account.domain.exception.OverdraftLimitExceededException;
import fr.albin.bank_account.domain.model.enums.TypeAccount;
import fr.albin.bank_account.domain.model.enums.TypeOperation;

public class BankAccountOverdraftTest {

    @Test
    @DisplayName("Devrait créer un compte avec un numéro et un solde")
    void should_create_account_with_number_and_balance() {
        String number = "123456";
        double balance = 1000.0;
        BankAccountOverdraft account = new BankAccountOverdraft(number, balance,1000);

        assertEquals(number, account.getAccountNumber());
        assertEquals(balance, account.getBalance());
    }

    @Test
    @DisplayName("Devrait refuser de créer un compte avec une limite de découvert négative")
    void should_reject_negative_overdraft_limit() {
        assertThrows(
            InvalidAmountException.class,
            () -> new BankAccountOverdraft("123456", 1000.0, -1.0)
        );
    }

    @Test
    @DisplayName("Devrait déposer de l'argent sur le compte")
    void should_deposit_money() {
        BankAccountOverdraft account = new BankAccountOverdraft("123456", 1000.0,1000);

        account.deposit(500.0);

        assertEquals(1500.0, account.getBalance());
    }

    @Test
    @DisplayName("Devrait refuser un dépôt négatif")
    void should_reject_negative_deposit() {
        BankAccountOverdraft account = new BankAccountOverdraft("123456", 1000.0, 1000.0);

        assertThrows(
            InvalidAmountException.class,
            () -> account.deposit(-1.0)
        );
    }

    @Test
    @DisplayName("Devrait retirer de l'argent du compte")
    void should_withdraw_money() throws InsufficientBalanceException{
        
        BankAccountOverdraft account = new BankAccountOverdraft("123456", 1000.0,1000);

        account.withdraw(300.0);

        assertEquals(700.0, account.getBalance());
    }

    @Test
    @DisplayName("Devrait autoriser un retrait si solde suffisant")
    void should_accept_withdrawal_with_overdraft() throws InsufficientBalanceException{
        
        BankAccountOverdraft account = new BankAccountOverdraft("123456", 500.0,1000);

        account.withdraw(600.0);
        assertEquals(-100.0, account.getBalance());
    }

    @Test
    @DisplayName("Devrait refuser un retrait négatif")
    void should_reject_negative_withdrawal() {
        BankAccountOverdraft account = new BankAccountOverdraft("123456", 500.0, 1000.0);

        assertThrows(
            InvalidAmountException.class,
            () -> account.withdraw(-1.0)
        );
    }

    @Test
    @DisplayName("Devrait refuser un retrait si solde insuffisant")
    void should_reject_withdrawal_if_insufficient_balance() {
        
        BankAccountOverdraft account = new BankAccountOverdraft("123456", 500.0,1000);

        assertThrows(
            OverdraftLimitExceededException.class,
            () -> account.withdraw(1600.0),
            "Un retrait supérieur au solde devrait lever une exception"
        );
    }

    @Test
    @DisplayName("Devrait émettre un relevé de compte découvert avec type, solde et opérations")
    void should_emit_overdraft_statement_with_account_type_balance_and_operations() {
        BankAccountOverdraft account = new BankAccountOverdraft("123456", 1000.0, 1000.0);
        account.deposit(200.0);
        account.withdraw(50.0);

        AccountStatement statement = account.emitStatement(LocalDateTime.now());

        assertEquals(TypeAccount.BankAccount, statement.getAccountType());
        assertEquals(1150.0, statement.getBalance());
        assertEquals(3, statement.getOperations().size());
    }

    @Test
    @DisplayName("Devrait trier les opérations du relevé découvert en ordre antéchronologique")
    void should_sort_overdraft_statement_operations_by_descending_date() throws InterruptedException {
        BankAccountOverdraft account = new BankAccountOverdraft("123456", 1000.0, 1000.0);
        Thread.sleep(5);
        account.deposit(100.0);
        Thread.sleep(5);
        account.withdraw(30.0);

        AccountStatement statement = account.emitStatement(LocalDateTime.now());

        assertEquals(TypeOperation.WITHDRAWAL, statement.getOperations().get(0).getOperation());
        assertEquals(TypeOperation.DEPOSIT, statement.getOperations().get(1).getOperation());
        assertEquals(TypeOperation.CREATE, statement.getOperations().get(2).getOperation());
    }
}
