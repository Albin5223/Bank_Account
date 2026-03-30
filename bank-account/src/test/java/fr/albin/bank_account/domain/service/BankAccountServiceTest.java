package fr.albin.bank_account.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.albin.bank_account.domain.exception.DepositCapExceededException;
import fr.albin.bank_account.domain.exception.InsufficientBalanceException;
import fr.albin.bank_account.domain.exception.InvalidAmountException;
import fr.albin.bank_account.domain.model.AccountStatement;
import fr.albin.bank_account.domain.model.BankAccount;
import fr.albin.bank_account.domain.model.BankAccountOverdraft;
import fr.albin.bank_account.domain.model.SavingsAccount;
import fr.albin.bank_account.domain.model.enums.TypeAccount;
import fr.albin.bank_account.domain.model.interfaces.BankAccountImpl;
import fr.albin.bank_account.domain.port.out.GenerateAccountNumberPort;
import fr.albin.bank_account.domain.port.out.LoadBankAccountPort;
import fr.albin.bank_account.domain.port.out.SaveBankAccountPort;

@DisplayName("BankAccountUseCaseService - tests applicatifs")
class BankAccountServiceTest {

    @Test
    @DisplayName("Crée un compte bancaire et le persiste")
    void should_create_bank_account_and_persist_it() {
        InMemoryAccountStore accountStore = new InMemoryAccountStore();
        FixedAccountNumberGenerator generator = new FixedAccountNumberGenerator("ACC-001");
        BankAccountService service = new BankAccountService(generator, accountStore, accountStore);

        String accountNumber = service.createBankAccount(100.0);

        assertEquals("ACC-001", accountNumber);
        BankAccountImpl created = accountStore.loadByAccountNumber("ACC-001").orElseThrow();
        assertEquals(100.0, created.getBalance());
        assertEquals("ACC-001", created.getAccountNumber());
    }

    @Test
    @DisplayName("Crée un livret et le persiste")
    void should_create_savings_account_and_persist_it() {
        InMemoryAccountStore accountStore = new InMemoryAccountStore();
        FixedAccountNumberGenerator generator = new FixedAccountNumberGenerator("SAV-001");
        BankAccountService service = new BankAccountService(generator, accountStore, accountStore);

        String accountNumber = service.createSavingsAccount(200.0, 500.0);

        assertEquals("SAV-001", accountNumber);
        BankAccountImpl created = accountStore.loadByAccountNumber("SAV-001").orElseThrow();
        assertInstanceOf(SavingsAccount.class, created);
        assertEquals(200.0, created.getBalance());
    }

    @Test
    @DisplayName("Crée un compte avec découvert et le persiste")
    void should_create_overdraft_account_and_persist_it() {
        InMemoryAccountStore accountStore = new InMemoryAccountStore();
        FixedAccountNumberGenerator generator = new FixedAccountNumberGenerator("OD-001");
        BankAccountService service = new BankAccountService(generator, accountStore, accountStore);

        String accountNumber = service.createBankAccountOverdraft(300.0, 250.0);

        assertEquals("OD-001", accountNumber);
        BankAccountImpl created = accountStore.loadByAccountNumber("OD-001").orElseThrow();
        assertInstanceOf(BankAccountOverdraft.class, created);
        assertEquals(300.0, created.getBalance());
    }

    @Test
    @DisplayName("Dépose sur un compte existant")
    void should_deposit_money_on_existing_account() {
        InMemoryAccountStore accountStore = new InMemoryAccountStore();
        accountStore.save(new BankAccount("ACC-10", 100.0));
        BankAccountService service = new BankAccountService(new FixedAccountNumberGenerator("IGNORED"), accountStore, accountStore);

        boolean result = service.depositMoney("ACC-10", 40.0);

        assertTrue(result);
        assertEquals(140.0, accountStore.loadByAccountNumber("ACC-10").orElseThrow().getBalance());
    }

    @Test
    @DisplayName("Retire sur un compte existant")
    void should_withdraw_money_on_existing_account() {
        InMemoryAccountStore accountStore = new InMemoryAccountStore();
        accountStore.save(new BankAccount("ACC-11", 150.0));
        BankAccountService service = new BankAccountService(new FixedAccountNumberGenerator("IGNORED"), accountStore, accountStore);

        boolean result = service.withdrawMoney("ACC-11", 50.0);

        assertTrue(result);
        assertEquals(100.0, accountStore.loadByAccountNumber("ACC-11").orElseThrow().getBalance());
    }

    @Test
    @DisplayName("Refuse un dépôt sur compte introuvable")
    void should_reject_deposit_when_account_not_found() {
        InMemoryAccountStore accountStore = new InMemoryAccountStore();
        BankAccountService service = new BankAccountService(new FixedAccountNumberGenerator("IGNORED"), accountStore, accountStore);

        assertThrows(NoSuchElementException.class, () -> service.depositMoney("UNKNOWN", 10.0));
    }

    @Test
    @DisplayName("Refuse un retrait sur compte introuvable")
    void should_reject_withdrawal_when_account_not_found() {
        InMemoryAccountStore accountStore = new InMemoryAccountStore();
        BankAccountService service = new BankAccountService(new FixedAccountNumberGenerator("IGNORED"), accountStore, accountStore);

        assertThrows(NoSuchElementException.class, () -> service.withdrawMoney("UNKNOWN", 10.0));
    }

    @Test
    @DisplayName("Propage l'erreur métier sur dépôt invalide")
    void should_propagate_invalid_amount_on_deposit() {
        InMemoryAccountStore accountStore = new InMemoryAccountStore();
        accountStore.save(new BankAccount("ACC-12", 100.0));
        BankAccountService service = new BankAccountService(new FixedAccountNumberGenerator("IGNORED"), accountStore, accountStore);

        assertThrows(InvalidAmountException.class, () -> service.depositMoney("ACC-12", 0.0));
    }

    @Test
    @DisplayName("Propage l'erreur métier sur retrait insuffisant")
    void should_propagate_insufficient_balance_on_withdrawal() {
        InMemoryAccountStore accountStore = new InMemoryAccountStore();
        accountStore.save(new BankAccount("ACC-13", 50.0));
        BankAccountService service = new BankAccountService(new FixedAccountNumberGenerator("IGNORED"), accountStore, accountStore);

        assertThrows(InsufficientBalanceException.class, () -> service.withdrawMoney("ACC-13", 60.0));
    }

    @Test
    @DisplayName("Propage l'erreur métier sur dépassement du plafond livret")
    void should_propagate_deposit_cap_exception_for_savings_account() {
        InMemoryAccountStore accountStore = new InMemoryAccountStore();
        accountStore.save(new SavingsAccount("SAV-10", 80.0, 100.0));
        BankAccountService service = new BankAccountService(new FixedAccountNumberGenerator("IGNORED"), accountStore, accountStore);

        assertThrows(DepositCapExceededException.class, () -> service.depositMoney("SAV-10", 30.0));
    }

    @Test
    @DisplayName("Propage l'erreur métier sur dépassement découvert")
    void should_propagate_overdraft_limit_exceeded_exception() {
        InMemoryAccountStore accountStore = new InMemoryAccountStore();
        accountStore.save(new BankAccountOverdraft("OD-10", 20.0, 10.0));
        BankAccountService service = new BankAccountService(new FixedAccountNumberGenerator("IGNORED"), accountStore, accountStore);

        assertThrows(InsufficientBalanceException.class, () -> service.withdrawMoney("OD-10", 40.0));
    }

    @Test
    @DisplayName("Retourne un relevé pour un compte existant")
    void should_get_statement_for_existing_account() {
        InMemoryAccountStore accountStore = new InMemoryAccountStore();
        BankAccount account = new BankAccount("ACC-20", 100.0);
        account.deposit(20.0);
        accountStore.save(account);
        BankAccountService service = new BankAccountService(new FixedAccountNumberGenerator("IGNORED"), accountStore, accountStore);

        LocalDateTime emissionDate = LocalDateTime.now();
        AccountStatement statement = service.getAccountStatement("ACC-20", emissionDate);

        assertEquals(TypeAccount.BANK_ACCOUNT, statement.getAccountType());
        assertEquals(120.0, statement.getBalance());
        assertEquals(emissionDate, statement.getEmissionDate());
        assertEquals(2, statement.getOperations().size());
    }

    private static final class InMemoryAccountStore implements LoadBankAccountPort, SaveBankAccountPort {

        private final Map<String, BankAccountImpl> accounts = new HashMap<>();

        @Override
        public Optional<BankAccountImpl> loadByAccountNumber(String accountNumber) {
            return Optional.ofNullable(accounts.get(accountNumber));
        }

        @Override
        public void save(BankAccountImpl account) {
            accounts.put(account.getAccountNumber(), account);
        }
    }

    private static final class FixedAccountNumberGenerator implements GenerateAccountNumberPort {

        private final String accountNumber;

        private FixedAccountNumberGenerator(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        @Override
        public String generateAccountNumber() {
            return accountNumber;
        }
    }
}