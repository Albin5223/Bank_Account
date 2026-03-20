package fr.albin.bank_account.infrastructure.adapter.in;

import java.net.URI;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.albin.bank_account.domain.exception.OverdraftLimitExceededException;
import fr.albin.bank_account.domain.model.AccountStatement;
import fr.albin.bank_account.domain.port.in.CreateBankAccountOverdraftUseCase;
import fr.albin.bank_account.domain.port.in.CreateBankAccountUseCase;
import fr.albin.bank_account.domain.port.in.CreateSavingsAccountUseCase;
import fr.albin.bank_account.domain.port.in.DepositMoneyUseCase;
import fr.albin.bank_account.domain.port.in.GetAccountStatementUseCase;
import fr.albin.bank_account.domain.port.in.WithdrawMoneyUseCase;
import fr.albin.bank_account.infrastructure.TDO.CreateBankAccountOverdraftRequest;
import fr.albin.bank_account.infrastructure.TDO.CreateBankAccountRequest;
import fr.albin.bank_account.infrastructure.TDO.CreateSavingsAccountRequest;
import fr.albin.bank_account.infrastructure.TDO.DepositMoneyRequest;
import fr.albin.bank_account.infrastructure.TDO.GetAccountStatementRequest;
import fr.albin.bank_account.infrastructure.TDO.WithdrawMoneyRequest;

@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {

    private final CreateBankAccountUseCase createBankAccountUseCase;
    private final CreateBankAccountOverdraftUseCase createBankAccountOverdraftUseCase;
    private final CreateSavingsAccountUseCase createSavingsAccountUseCase;
    private final DepositMoneyUseCase depositMoneyUseCase;
    private final GetAccountStatementUseCase getAccountStatementUseCase;
    private final WithdrawMoneyUseCase withdrawMoneyUseCase;

    public BankAccountController(
            CreateBankAccountUseCase createBankAccount,
            CreateBankAccountOverdraftUseCase createBankAccountOverdraft,
            CreateSavingsAccountUseCase createSavingsAccountUseCase,
            DepositMoneyUseCase depositMoney,
            GetAccountStatementUseCase getAccountStatement,
            WithdrawMoneyUseCase withdrawMoney) {
        this.createBankAccountUseCase = createBankAccount;
        this.createBankAccountOverdraftUseCase = createBankAccountOverdraft;
        this.createSavingsAccountUseCase = createSavingsAccountUseCase;
        this.depositMoneyUseCase = depositMoney;
        this.getAccountStatementUseCase = getAccountStatement;
        this.withdrawMoneyUseCase = withdrawMoney;
    }

    @PostMapping("/createBankAccount")
    public ResponseEntity<String> createBankAccount(@Validated @RequestBody CreateBankAccountRequest request) {
        try {
            String accountId = createBankAccountUseCase.createBankAccount(request.balance());
            URI location = URI.create("/api/accounts/" + accountId);
            return ResponseEntity.created(location).build(); // 201
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error"); // 500
        }
    }

    @PostMapping("/createSavingsAccount")
    public ResponseEntity<String> createSavingsAccount(@Validated @RequestBody CreateSavingsAccountRequest request) {
        try {
            String accountId = createSavingsAccountUseCase.createSavingsAccount(request.balance(), request.depositCap());
            URI location = URI.create("/api/accounts/" + accountId);
            return ResponseEntity.created(location).build(); // 201
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // 409
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error"); // 500
        }
    }

    @PostMapping("/createBankAccountOverdraft")
    public ResponseEntity<String> createBankAccountOverdraft(@Validated @RequestBody CreateBankAccountOverdraftRequest request) {
        try {
            String accountId = createBankAccountOverdraftUseCase.createBankAccountOverdraft(request.balance(), request.overdraft());
            URI location = URI.create("/api/accounts/" + accountId);
            return ResponseEntity.created(location).build(); // 201
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // 409
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error"); // 500
        }
    }

    @GetMapping("/accountStatement")
    public ResponseEntity<?> getAccountStatement(@Validated @RequestBody GetAccountStatementRequest request) {
        try {
            AccountStatement accountStatement = getAccountStatementUseCase.getAccountStatement(request.accountNumber(),request.date());
            return ResponseEntity.ok(accountStatement); // 200
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error"); // 500
        }
    }


    @PostMapping("/depositMoney")
    public ResponseEntity<String> depositMoney(@Validated @RequestBody DepositMoneyRequest request) {
        try {
            depositMoneyUseCase.depositMoney(request.accountNumber(), request.amount());
            return ResponseEntity.noContent().build(); // 204
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error"); // 500
        }
    }

    @PostMapping("/withdrawMoney")
    public ResponseEntity<String> withdrawMoney(@Validated @RequestBody WithdrawMoneyRequest request) {
        try {
            withdrawMoneyUseCase.withdrawMoney(request.accountNumber(), request.amount());
            return ResponseEntity.noContent().build(); // 204
        } catch (OverdraftLimitExceededException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // 409
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // 409
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error"); // 500
        }
    }




    
    
}
