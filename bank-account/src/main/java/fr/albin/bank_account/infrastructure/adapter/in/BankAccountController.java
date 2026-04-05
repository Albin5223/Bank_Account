package fr.albin.bank_account.infrastructure.adapter.in;

import java.net.URI;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.albin.bank_account.domain.exception.DepositCapExceededException;
import fr.albin.bank_account.domain.exception.InsufficientBalanceException;
import fr.albin.bank_account.domain.exception.InvalidAmountException;
import fr.albin.bank_account.domain.model.AccountStatement;
import fr.albin.bank_account.domain.port.in.CreateBankAccountOverdraftUseCase;
import fr.albin.bank_account.domain.port.in.CreateBankAccountUseCase;
import fr.albin.bank_account.domain.port.in.CreateSavingsAccountUseCase;
import fr.albin.bank_account.domain.port.in.DepositMoneyUseCase;
import fr.albin.bank_account.domain.port.in.GetAccountStatementUseCase;
import fr.albin.bank_account.domain.port.in.WithdrawMoneyUseCase;
import fr.albin.bank_account.infrastructure.DTO.CreateAccountResponse;
import fr.albin.bank_account.infrastructure.DTO.CreateBankAccountOverdraftRequest;
import fr.albin.bank_account.infrastructure.DTO.CreateBankAccountRequest;
import fr.albin.bank_account.infrastructure.DTO.CreateSavingsAccountRequest;
import fr.albin.bank_account.infrastructure.DTO.DepositMoneyRequest;
import fr.albin.bank_account.infrastructure.DTO.GetAccountStatementRequest;
import fr.albin.bank_account.infrastructure.DTO.WithdrawMoneyRequest;

/**
 * Contrôleur REST pour la gestion des comptes bancaires. Cette classe expose des endpoints
 * pour créer des comptes bancaires, déposer de l'argent, retirer de l'argent et obtenir le relevé de compte.
 */
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
    public ResponseEntity<CreateAccountResponse> createBankAccount(@Validated @RequestBody CreateBankAccountRequest request) {
        String accountId = createBankAccountUseCase.createBankAccount(request.balance());
        URI location = URI.create("/api/accounts/" + accountId);
        return ResponseEntity.created(location).body(new CreateAccountResponse(accountId)); // 201
    }

    @PostMapping("/createSavingsAccount")
    public ResponseEntity<CreateAccountResponse> createSavingsAccount(@Validated @RequestBody CreateSavingsAccountRequest request) {
        String accountId = createSavingsAccountUseCase.createSavingsAccount(request.balance(), request.depositCap());
        URI location = URI.create("/api/accounts/" + accountId);
        return ResponseEntity.created(location).body(new CreateAccountResponse(accountId)); // 201
    }

    @PostMapping("/createBankAccountOverdraft")
    public ResponseEntity<CreateAccountResponse> createBankAccountOverdraft(@Validated @RequestBody CreateBankAccountOverdraftRequest request) {
        String accountId = createBankAccountOverdraftUseCase.createBankAccountOverdraft(request.balance(), request.overdraft());
        URI location = URI.create("/api/accounts/" + accountId);
        return ResponseEntity.created(location).body(new CreateAccountResponse(accountId)); // 201
    }

    @GetMapping("/accountStatement")
    public ResponseEntity<AccountStatement> getAccountStatement(@Validated @RequestBody GetAccountStatementRequest request) {
        AccountStatement accountStatement = getAccountStatementUseCase.getAccountStatement(request.accountNumber(), request.date());
        return ResponseEntity.ok(accountStatement); // 200
    }


    @PostMapping("/depositMoney")
    public ResponseEntity<Void> depositMoney(@Validated @RequestBody DepositMoneyRequest request) {
        depositMoneyUseCase.depositMoney(request.accountNumber(), request.amount());
        return ResponseEntity.noContent().build(); // 204
    }

    @PostMapping("/withdrawMoney")
    public ResponseEntity<Void> withdrawMoney(@Validated @RequestBody WithdrawMoneyRequest request) {
        withdrawMoneyUseCase.withdrawMoney(request.accountNumber(), request.amount());
        return ResponseEntity.noContent().build(); // 204
    }

    /**
     * Intercepter les erreurs de validation des requetes
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<String> handleValidationExceptions(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorMessage(e));
    }

    /**
     * Intercepter les erreurs métiers
     */
    @ExceptionHandler({InvalidAmountException.class, InsufficientBalanceException.class, DepositCapExceededException.class})
    public ResponseEntity<String> handleBusinessExceptions(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(buildErrorMessage(e));
    }

    /**
     * Intercepter les erreurs liées aux comptes non trouvés
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorMessage(e));
    }

    /**
     * Intercepter toutes les autres erreurs
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnexpectedExceptions(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildErrorMessage(e));
    }

    /**
     * Méthode pour construire un message d'erreur
     */
    private String buildErrorMessage(Exception exception) {
        return exception.getClass().getSimpleName() + " : " + exception.getMessage();
    }
}
