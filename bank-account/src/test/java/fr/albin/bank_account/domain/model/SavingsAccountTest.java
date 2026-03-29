package fr.albin.bank_account.domain.model;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.albin.bank_account.domain.exception.DepositCapExceededException;
import fr.albin.bank_account.domain.exception.InvalidAmountException;
import fr.albin.bank_account.domain.exception.InsufficientBalanceException;
import fr.albin.bank_account.domain.model.enums.TypeAccount;
import fr.albin.bank_account.domain.model.enums.TypeOperation;

@DisplayName("SavingsAccount - Feature 3 : Livret")
class SavingsAccountTest {

    @Test
    @DisplayName("Crée un livret avec numéro, solde et plafond")
    void should_create_savings_account_with_number_and_balance() {
        String number = "123456";
        double balance = 1000.0;
        double plafond = 1600;
        SavingsAccount account = new SavingsAccount(number, balance,plafond);

        assertEquals(number, account.getAccountNumber());
        assertEquals(balance, account.getBalance());
        assertEquals(plafond, account.getDepositCap());
    }

    @Test
    @DisplayName("Refuse la création d'un livret avec plafond négatif")
    void should_reject_negative_deposit_cap() {
        assertThrows(
            InvalidAmountException.class,
            () -> new SavingsAccount("123456", 1000.0, -1.0)
        );
    }

    @Test
    @DisplayName("Refuse la création d'un livret si le solde dépasse le plafond")
    void should_not_create_account_with_balance_above_cap() {
        String number = "123456";
        double balance = 2500.0;
        double depositCap = 1600;
        assertThrows(
            DepositCapExceededException.class,
            () -> new SavingsAccount(number, balance,depositCap),
            "La création d'un livret avec un solde supérieur au plafond devrait lever une exception"
        ); 
    }

    @Test
    @DisplayName("Refuse la création d'un livret avec solde négatif")
    void should_not_create_account_with_negative_balance() {
        String number = "123456";
        double balance = -500.0;
        assertThrows(
            InsufficientBalanceException.class,
            () -> new SavingsAccount(number, balance,1600),
            "La création d'un compte avec un solde négatif devrait lever une exception"
        ); 
    }

    @Test
    @DisplayName("Devrait réfuser de créer un compte avec un solde < depositCap")
    void should_not_create_account_with_balance_sup_depositCap() {
        String number = "123456";
        double balance = 2500.0;
        assertThrows(
            DepositCapExceededException.class,
            () -> new SavingsAccount(number, balance,1600),
            "La création d'un compte avec un solde négatif devrait lever une exception"
        ); 
    }

    @Test
    @DisplayName("Devrait réfuser de créer un compte avec un solde = depositCap")
    void should_not_create_account_with_balance_eq_depositCap() {
        String number = "123456";
        double balance = 2500.0;
        assertDoesNotThrow(
            () -> new SavingsAccount(number, balance,balance),
            "La création d'un compte avec un solde négatif devrait lever une exception"
        ); 
    }

    @Test
    @DisplayName("Dépose de l'argent sur le livret")
    void should_deposit_money(){
        SavingsAccount account = new SavingsAccount("123456", 1000.0,1600);

        account.deposit(500.0);

        assertEquals(1500.0, account.getBalance());
    }

    @Test
    @DisplayName("Devrait déposer de l'argent sur le compte")
    void should_deposit_money_eq_desiteCape(){
        SavingsAccount account = new SavingsAccount("123456", 1000.0,1600);

        account.deposit(600.0);

        assertEquals(1600.0, account.getBalance());
    }

    @Test
    @DisplayName("Refuse un dépôt négatif")
    void should_reject_negative_deposit(){
        SavingsAccount account = new SavingsAccount("123456", 1000.0,1600);

        assertThrows(
            InvalidAmountException.class,
            () -> account.deposit(-1.0)
        );
    }

    @Test
    @DisplayName("Refuse un dépôt à zéro")
    void should_reject_zero_deposit(){
        SavingsAccount account = new SavingsAccount("123456", 1000.0,1600);

        assertThrows(
            InvalidAmountException.class,
            () -> account.deposit(0.0)
        );
    }

    @Test
    @DisplayName("Refuse un dépôt qui dépasse le plafond")
    void should_reject_deposit_above_cap(){
        SavingsAccount account = new SavingsAccount("123456", 1000.0,1600);

        account.deposit(500.0);

        assertThrows(
            DepositCapExceededException.class,
            () -> account.deposit(1500.0),
            "Un dépot qui dépasse le plafond devrait lever une exception"
        );
    }

    @Test
    @DisplayName("Retire de l'argent du livret")
    void should_withdraw_money(){
        
        SavingsAccount account = new SavingsAccount("123456", 1000.0,1600);

        account.withdraw(300.0);

        assertEquals(700.0, account.getBalance());
    }

    @Test
    @DisplayName("Refuse un retrait négatif")
    void should_reject_negative_withdrawal(){
        SavingsAccount account = new SavingsAccount("123456", 1000.0,1600);

        assertThrows(
            InvalidAmountException.class,
            () -> account.withdraw(-1.0)
        );
    }

    @Test
    @DisplayName("Refuse un retrait à zéro")
    void should_reject_zero_withdrawal(){
        SavingsAccount account = new SavingsAccount("123456", 1000.0,1600);

        assertThrows(
            InvalidAmountException.class,
            () -> account.withdraw(0.0)
        );
    }

    @Test
    @DisplayName("Refuse un retrait si le solde est insuffisant")
    void should_reject_withdrawal_if_insufficient_balance() {
        
        SavingsAccount account = new SavingsAccount("123456", 500.0,1600);

        assertThrows(
            InsufficientBalanceException.class,
            () -> account.withdraw(600.0),
            "Un retrait supérieur au solde devrait lever une exception"
        );
    }

    @Test
    @DisplayName("Accepte un retrait égal au solde exact")
    void should_allow_withdrawal_equal_to_balance(){
        
        SavingsAccount account = new SavingsAccount("123456", 500.0,1600);

        account.withdraw(500.0);

        assertEquals(0.0, account.getBalance());
    }

    @Test
    @DisplayName("Émet un relevé de livret avec type de compte, solde et opérations")
    void should_emit_savings_statement_with_account_type_balance_and_operations() {
        SavingsAccount account = new SavingsAccount("123456", 1000.0, 3000.0);
        account.deposit(200.0);
        account.withdraw(50.0);

        AccountStatement statement = account.emitStatement(LocalDateTime.now());

        assertEquals(TypeAccount.SAVINGS_ACCOUNT, statement.getAccountType());
        assertEquals(1150.0, statement.getBalance());
        assertEquals(3, statement.getOperations().size());
    }

    @Test
    @DisplayName("Trie les opérations du relevé en ordre antéchronologique")
    void should_sort_savings_statement_operations_by_descending_date() throws InterruptedException {
        SavingsAccount account = new SavingsAccount("123456", 1000.0, 3000.0);
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
