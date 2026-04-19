package fr.albin.bank_account.domain.service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import fr.albin.bank_account.domain.model.AccountStatement;
import fr.albin.bank_account.domain.model.BankAccountOverdraft;
import fr.albin.bank_account.domain.model.User;
import fr.albin.bank_account.domain.model.BankAccountFactory;
import fr.albin.bank_account.domain.model.interfaces.BankAccountImpl;
import fr.albin.bank_account.domain.port.in.bankAccountUseCase.CreateBankAccountOverdraftUseCase;
import fr.albin.bank_account.domain.port.in.bankAccountUseCase.CreateBankAccountUseCase;
import fr.albin.bank_account.domain.port.in.bankAccountUseCase.CreateSavingsAccountUseCase;
import fr.albin.bank_account.domain.port.in.bankAccountUseCase.DepositMoneyUseCase;
import fr.albin.bank_account.domain.port.in.bankAccountUseCase.GetAccountStatementUseCase;
import fr.albin.bank_account.domain.port.in.bankAccountUseCase.WithdrawMoneyUseCase;
import fr.albin.bank_account.domain.port.out.bankAccountPort.GenerateAccountNumberPort;
import fr.albin.bank_account.domain.port.out.bankAccountPort.LoadBankAccountPort;
import fr.albin.bank_account.domain.port.out.bankAccountPort.SaveBankAccountPort;
import fr.albin.bank_account.domain.port.out.userPort.LoadUserPort;

/**
 * Service de domaine pour la gestion des comptes bancaires. Cette classe implémente les cas d'utilisation
 * de création de comptes bancaires, de dépôt d'argent, de retrait d'argent et d'obtention du relevé de compte.
 * Elle utilise les ports de sortie pour générer des numéros de compte, charger des comptes existants et sauvegarder les comptes modifiés. 
 */
@Service
public class BankAccountService implements
    CreateBankAccountUseCase,
    CreateSavingsAccountUseCase,
    CreateBankAccountOverdraftUseCase,
    DepositMoneyUseCase,
    WithdrawMoneyUseCase,
    GetAccountStatementUseCase {

    private final GenerateAccountNumberPort generateAccountNumberPort;
    private final LoadBankAccountPort loadBankAccountPort;
    private final SaveBankAccountPort saveBankAccountPort;
    private final LoadUserPort loadUserPort;

    public BankAccountService(
        GenerateAccountNumberPort generateAccountNumberPort,
        LoadBankAccountPort loadBankAccountPort,
        SaveBankAccountPort saveBankAccountPort,
        LoadUserPort loadUserPort
    ) {
        this.generateAccountNumberPort = generateAccountNumberPort;
        this.loadBankAccountPort = loadBankAccountPort;
        this.saveBankAccountPort = saveBankAccountPort;
        this.loadUserPort = loadUserPort;
    }

    @Override
    public String createBankAccount(double balance, String user) {
        loadUserOrThrow(user);
        String accountNumber = generateAccountNumberPort.generateAccountNumber();
        BankAccountImpl account = BankAccountFactory.createStandard(accountNumber, balance);
        saveBankAccountPort.save(account, user);
        return accountNumber;
    }

    @Override
    public String createSavingsAccount(double balance, double depositCap, String user) {
        loadUserOrThrow(user);
        String accountNumber = generateAccountNumberPort.generateAccountNumber();
        BankAccountImpl account = BankAccountFactory.createSavings(accountNumber, balance, depositCap);
        saveBankAccountPort.save(account, user);
        return accountNumber;
    }

    @Override
    public String createBankAccountOverdraft(double balance, double overdraft, String user) {
        loadUserOrThrow(user);
        String accountNumber = generateAccountNumberPort.generateAccountNumber();
        BankAccountOverdraft account = BankAccountFactory.createOverdraft(accountNumber, balance, overdraft);
        saveBankAccountPort.save(account, user);
        return accountNumber;
    }

    @Override
    public boolean depositMoney(String accountNumber, double money) {
        BankAccountImpl account = loadAccountOrThrow(accountNumber);
        account.deposit(money);
        saveBankAccountPort.save(account);
        return true;
    }

    @Override
    public boolean withdrawMoney(String accountNumber, double money) {
        BankAccountImpl account = loadAccountOrThrow(accountNumber);
        account.withdraw(money);
        saveBankAccountPort.save(account);
        return true;
    }

    @Override
    public AccountStatement getAccountStatement(String accountNumber, LocalDateTime emissionDate) {
        BankAccountImpl account = loadAccountOrThrow(accountNumber);
        return account.emitStatement(emissionDate);
    }

    private BankAccountImpl loadAccountOrThrow(String accountNumber) {
        return loadBankAccountPort
            .loadByAccountNumber(accountNumber)
            .orElseThrow(() -> new NoSuchElementException("Compte introuvable : " + accountNumber));
    }

    private User loadUserOrThrow(String username) {
        return loadUserPort
            .loadUserByUsername(username)
            .orElseThrow(() -> new NoSuchElementException("Utilisateur introuvable : " + username));
    }
}