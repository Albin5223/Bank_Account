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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.albin.bank_account.domain.exception.DepositCapExceededException;
import fr.albin.bank_account.domain.exception.InsufficientBalanceException;
import fr.albin.bank_account.domain.exception.InvalidAmountException;
import fr.albin.bank_account.domain.model.AccountStatement;
import fr.albin.bank_account.domain.model.BankAccountOverdraft;
import fr.albin.bank_account.domain.model.SavingsAccount;
import fr.albin.bank_account.domain.model.User;
import fr.albin.bank_account.domain.model.enums.TypeAccount;
import fr.albin.bank_account.domain.model.interfaces.BankAccountImpl;
import fr.albin.bank_account.domain.port.out.bankAccountPort.GenerateAccountNumberPort;
import fr.albin.bank_account.domain.port.out.bankAccountPort.LoadBankAccountPort;
import fr.albin.bank_account.domain.port.out.bankAccountPort.SaveBankAccountPort;
import fr.albin.bank_account.domain.port.out.userPort.LoadUserPort;
import fr.albin.bank_account.domain.port.out.userPort.SaveUserPort;

@DisplayName("BankAccountUseCaseService - tests applicatifs")
class BankAccountServiceTest {

    private static String username = "testuser";
    private static String email = "test@user.com";
    private static String password = "password";

    private static InMemoryAccountStore accountStore = new InMemoryAccountStore();
    private static FixedAccountNumberGenerator generator = new FixedAccountNumberGenerator("ACC-001");
    private static InMemoryUserStore userStore = new InMemoryUserStore();
    private static BankAccountService service;

    /**
     * Méthode d'initialisation pour les tests de BankAccountService. 
     * Elle crée une instance de BankAccountService et ajoute un user
     */
    @BeforeAll
    static void setup(){
        userStore.save(new User(username, password,email));
        service = new BankAccountService(generator, accountStore, accountStore, userStore);
    }
    
    /**
     * Méthode d'initialisation avant chaque test pour s'assurer que le magasin de comptes est vide avant chaque test.
     */
    @BeforeEach
    void init(){
        accountStore.accounts.clear();
    }

    @Test
    @DisplayName("Crée un compte bancaire et le persiste")
    void should_create_bank_account_and_persist_it() {
        String accountNumber = service.createBankAccount(100.0, username);

        assertEquals("ACC-001", accountNumber);
        BankAccountImpl created = accountStore.loadByAccountNumber("ACC-001").orElseThrow();
        assertEquals(100.0, created.getBalance());
        assertEquals("ACC-001", created.getAccountNumber());
    }

    @Test
    @DisplayName("Crée un livret et le persiste")
    void should_create_savings_account_and_persist_it() {
        String accountNumber = service.createSavingsAccount(200.0, 500.0,username);

        assertEquals("ACC-001", accountNumber);
        BankAccountImpl created = accountStore.loadByAccountNumber("ACC-001").orElseThrow();
        assertInstanceOf(SavingsAccount.class, created);
        assertEquals(200.0, created.getBalance());
    }

    @Test
    @DisplayName("Crée un compte avec découvert et le persiste")
    void should_create_overdraft_account_and_persist_it() {

        String accountNumber = service.createBankAccountOverdraft(300.0, 250.0,username);

        assertEquals("ACC-001", accountNumber);
        BankAccountImpl created = accountStore.loadByAccountNumber("ACC-001").orElseThrow();
        assertInstanceOf(BankAccountOverdraft.class, created);
        assertEquals(300.0, created.getBalance());
    }

    @Test
    @DisplayName("Dépose sur un compte existant")
    void should_deposit_money_on_existing_account() {
        service.createBankAccount(100.0, username);
        boolean result = service.depositMoney("ACC-001", 40.0);

        assertTrue(result);
        assertEquals(140.0, accountStore.loadByAccountNumber("ACC-001").orElseThrow().getBalance());
    }

    @Test
    @DisplayName("Retire sur un compte existant")
    void should_withdraw_money_on_existing_account() {
        service.createBankAccount(150.0, username);
        boolean result = service.withdrawMoney("ACC-001", 50.0);

        assertTrue(result);
        assertEquals(100.0, accountStore.loadByAccountNumber("ACC-001").orElseThrow().getBalance());
    }

    @Test
    @DisplayName("Refuse un dépôt sur compte introuvable")
    void should_reject_deposit_when_account_not_found() {
        assertThrows(NoSuchElementException.class, () -> service.depositMoney("UNKNOWN", 10.0));
    }

    @Test
    @DisplayName("Refuse un retrait sur compte introuvable")
    void should_reject_withdrawal_when_account_not_found() {
        assertThrows(NoSuchElementException.class, () -> service.withdrawMoney("UNKNOWN", 10.0));
    }

    @Test
    @DisplayName("Propage l'erreur métier sur dépôt invalide")
    void should_propagate_invalid_amount_on_deposit() {
        String accountNumber = service.createBankAccount(100.0, username);
        assertThrows(InvalidAmountException.class, () -> service.depositMoney(accountNumber, 0.0));
    }

    @Test
    @DisplayName("Propage l'erreur métier sur retrait insuffisant")
    void should_propagate_insufficient_balance_on_withdrawal() {
        String accountNumber = service.createBankAccount(100.0, username);
        assertThrows(InsufficientBalanceException.class, () -> service.withdrawMoney(accountNumber, 160.0));
    }

    @Test
    @DisplayName("Propage l'erreur métier sur dépassement du plafond livret")
    void should_propagate_deposit_cap_exception_for_savings_account() {
        String accountNumber = service.createSavingsAccount(100.0, 150.0, username);
        assertThrows(DepositCapExceededException.class, () -> service.depositMoney(accountNumber, 60.0));
    }

    @Test
    @DisplayName("Propage l'erreur métier sur dépassement découvert")
    void should_propagate_overdraft_limit_exceeded_exception() {
        String accountNumber = service.createBankAccountOverdraft(100.0, 50.0, username);
        assertThrows(InsufficientBalanceException.class, () -> service.withdrawMoney(accountNumber, 200.0));
    }

    @Test
    @DisplayName("Retourne un relevé pour un compte existant")
    void should_get_statement_for_existing_account() {
        String accountNumber = service.createBankAccount(100.0, username);
        service.depositMoney(accountNumber, 20.0);
        
        LocalDateTime emissionDate = LocalDateTime.now();
        AccountStatement statement = service.getAccountStatement(accountNumber, emissionDate);

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

    private static final class InMemoryUserStore implements LoadUserPort, SaveUserPort {

        private final Map<String, User> usernames = new HashMap<>();
        private final Map<String, User> emails = new HashMap<>();


        @Override
        public void save(User user) {
            usernames.put(user.getUsername(), user);
            emails.put(user.getEmail(), user);
        }

        @Override
        public Optional<User> loadUserByEmail(String email) {
            return Optional.ofNullable(emails.get(email));
        }

        @Override
        public Optional<User> loadUserByUsername(String username) {
            return Optional.ofNullable(usernames.get(username));
        }
    }
}