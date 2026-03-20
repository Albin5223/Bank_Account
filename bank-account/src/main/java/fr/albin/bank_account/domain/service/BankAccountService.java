package fr.albin.bank_account.domain.service;

import java.time.LocalDateTime;

import fr.albin.bank_account.domain.model.AccountStatement;
import fr.albin.bank_account.domain.model.BankAccount;
import fr.albin.bank_account.domain.model.BankAccountOverdraft;
import fr.albin.bank_account.domain.model.SavingsAccount;
import fr.albin.bank_account.domain.port.in.CreateBankAccountOverdraftUseCase;
import fr.albin.bank_account.domain.port.in.CreateBankAccountUseCase;
import fr.albin.bank_account.domain.port.in.CreateSavingsAccountUseCase;
import fr.albin.bank_account.domain.port.in.DepositMoneyUseCase;
import fr.albin.bank_account.domain.port.in.GetAccountStatementUseCase;
import fr.albin.bank_account.domain.port.in.WithdrawMoneyUseCase;
import fr.albin.bank_account.domain.port.out.GenerateAccountNumberPort;
import fr.albin.bank_account.domain.port.out.LoadBankAccountPort;
import fr.albin.bank_account.domain.port.out.SaveBankAccountPort;

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

    public BankAccountService(
        GenerateAccountNumberPort generateAccountNumberPort,
        LoadBankAccountPort loadBankAccountPort,
        SaveBankAccountPort saveBankAccountPort
    ) {
        this.generateAccountNumberPort = generateAccountNumberPort;
        this.loadBankAccountPort = loadBankAccountPort;
        this.saveBankAccountPort = saveBankAccountPort;
    }

    @Override
    public String createBankAccount(double balance) {
        String accountNumber = generateAccountNumberPort.generateAccountNumber();
        BankAccount account = new BankAccount(accountNumber, balance);
        saveBankAccountPort.save(account);
        return accountNumber;
    }

    @Override
    public String createSavingsAccount(double balance, double depositCap) {
        String accountNumber = generateAccountNumberPort.generateAccountNumber();
        SavingsAccount account = new SavingsAccount(accountNumber, balance, depositCap);
        saveBankAccountPort.save(account);
        return accountNumber;
    }

    @Override
    public String createBankAccountOverdraft(double balance, double overdraft) {
        String accountNumber = generateAccountNumberPort.generateAccountNumber();
        BankAccountOverdraft account = new BankAccountOverdraft(accountNumber, balance, overdraft);
        saveBankAccountPort.save(account);
        return accountNumber;
    }

    @Override
    public boolean depositMoney(String accountNumber, double money) {
        BankAccount account = loadAccountOrThrow(accountNumber);
        account.deposit(money);
        saveBankAccountPort.save(account);
        return true;
    }

    @Override
    public boolean withdrawMoney(String accountNumber, double money) {
        BankAccount account = loadAccountOrThrow(accountNumber);
        account.withdraw(money);
        saveBankAccountPort.save(account);
        return true;
    }

    @Override
    public AccountStatement getAccountStatement(String accountNumber, LocalDateTime emissionDate) {
        BankAccount account = loadAccountOrThrow(accountNumber);
        return account.emitStatement(emissionDate);
    }

    private BankAccount loadAccountOrThrow(String accountNumber) {
        return loadBankAccountPort
            .loadByAccountNumber(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Compte introuvable : " + accountNumber));
    }
}