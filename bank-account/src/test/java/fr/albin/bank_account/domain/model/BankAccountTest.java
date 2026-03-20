package fr.albin.bank_account.domain.model;

import java.time.LocalDateTime;

import fr.albin.bank_account.domain.exception.InvalidAmountException;
import fr.albin.bank_account.domain.exception.InsufficientBalanceException;
import fr.albin.bank_account.domain.model.enums.TypeAccount;
import fr.albin.bank_account.domain.model.enums.TypeOperation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BankAccount - Feature 1 : Compte Bancaire")
class BankAccountTest {

    @Test
    @DisplayName("Crée un compte avec un numéro et un solde")
    void should_create_account_with_number_and_balance() {
        String number = "123456";
        double balance = 1000.0;
        BankAccount account = new BankAccount(number, balance);

        assertEquals(number, account.getAccountNumber());
        assertEquals(balance, account.getBalance());
    }

    @Test
    @DisplayName("Refuse la création d'un compte avec un solde négatif")
    void should_not_create_account_with_negative_balance() {
        String number = "123456";
        double balance = -500.0;
        assertThrows(
            InsufficientBalanceException.class,
            () -> new BankAccount(number, balance),
            "La création d'un compte avec un solde négatif devrait lever une exception"
        ); 
    }

    @Test
    @DisplayName("Dépose de l'argent sur le compte")
    void should_deposit_money() {
        BankAccount account = new BankAccount("123456", 1000.0);

        account.deposit(500.0);

        assertEquals(1500.0, account.getBalance());
    }

    @Test
    @DisplayName("Refuse un dépôt négatif")
    void should_reject_negative_deposit() {
        BankAccount account = new BankAccount("123456", 1000.0);

        assertThrows(
            InvalidAmountException.class,
            () -> account.deposit(-1.0),
            "Un dépôt négatif devrait lever une exception"
        );
    }

    @Test
    @DisplayName("Refuse un dépôt à zéro")
    void should_reject_zero_deposit() {
        BankAccount account = new BankAccount("123456", 1000.0);

        assertThrows(
            InvalidAmountException.class,
            () -> account.deposit(0.0),
            "Un dépôt à zéro devrait lever une exception"
        );
    }

    @Test
    @DisplayName("Retire de l'argent du compte")
    void should_withdraw_money() {

        BankAccount account = new BankAccount("123456", 1000.0);

        account.withdraw(300.0);

        assertEquals(700.0, account.getBalance());
    }

    @Test
    @DisplayName("Refuse un retrait négatif")
    void should_reject_negative_withdrawal() {
        BankAccount account = new BankAccount("123456", 1000.0);

        assertThrows(
            InvalidAmountException.class,
            () -> account.withdraw(-1.0),
            "Un retrait négatif devrait lever une exception"
        );
    }

    @Test
    @DisplayName("Refuse un retrait à zéro")
    void should_reject_zero_withdrawal() {
        BankAccount account = new BankAccount("123456", 1000.0);

        assertThrows(
            InvalidAmountException.class,
            () -> account.withdraw(0.0),
            "Un retrait à zéro devrait lever une exception"
        );
    }

    @Test
    @DisplayName("Refuse un retrait si le solde est insuffisant")
    void should_reject_withdrawal_if_insufficient_balance() {
        BankAccount account = new BankAccount("123456", 500.0);

        assertThrows(
            InsufficientBalanceException.class,
            () -> account.withdraw(600.0),
            "Un retrait supérieur au solde devrait lever une exception"
        );
    }

    @Test
    @DisplayName("Accepte un retrait égal au solde exact")
    void should_allow_withdrawal_equal_to_balance() {

        BankAccount account = new BankAccount("123456", 500.0);

        account.withdraw(500.0);

        assertEquals(0.0, account.getBalance());
    }

    @Test
    @DisplayName("Émet un relevé avec type de compte, solde et opérations")
    void should_emit_statement_with_account_type_balance_and_operations() {
        BankAccount account = new BankAccount("123456", 1000.0);
        account.deposit(200.0);
        account.withdraw(50.0);

        AccountStatement statement = account.emitStatement(LocalDateTime.now());

        assertEquals(TypeAccount.BankAccount, statement.getAccountType());
        assertEquals(1150.0, statement.getBalance());
        assertEquals(3, statement.getOperations().size());
    }

    @Test
    @DisplayName("Trie les opérations du relevé en ordre antéchronologique")
    void should_sort_statement_operations_by_descending_date() throws InterruptedException {
        BankAccount account = new BankAccount("123456", 1000.0);
        Thread.sleep(5);
        account.deposit(100.0);
        Thread.sleep(5);
        account.withdraw(30.0);

        AccountStatement statement = account.emitStatement(LocalDateTime.now());

        assertEquals(TypeOperation.WITHDRAWAL, statement.getOperations().get(0).getOperation());
        assertEquals(TypeOperation.DEPOSIT, statement.getOperations().get(1).getOperation());
        assertEquals(TypeOperation.CREATE, statement.getOperations().get(2).getOperation());
    }

    @Test
    @DisplayName("Exclut les opérations hors du mois glissant")
    void should_exclude_operations_outside_sliding_month() {
        BankAccount account = new BankAccount("123456", 1000.0);
        account.deposit(200.0);
        account.withdraw(50.0);

        AccountStatement recentStatement = account.emitStatement(LocalDateTime.now());
        AccountStatement futureStatement = account.emitStatement(LocalDateTime.now().plusMonths(2));

        assertEquals(3, recentStatement.getOperations().size());
        assertEquals(0, futureStatement.getOperations().size());
        assertEquals(1150.0, futureStatement.getBalance());
    }

}
