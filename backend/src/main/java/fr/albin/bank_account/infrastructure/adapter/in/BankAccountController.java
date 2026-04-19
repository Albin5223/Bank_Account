package fr.albin.bank_account.infrastructure.adapter.in;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import fr.albin.bank_account.domain.model.User;
import fr.albin.bank_account.domain.port.in.bankAccountUseCase.CreateBankAccountOverdraftUseCase;
import fr.albin.bank_account.domain.port.in.bankAccountUseCase.CreateBankAccountUseCase;
import fr.albin.bank_account.domain.port.in.bankAccountUseCase.CreateSavingsAccountUseCase;
import fr.albin.bank_account.domain.port.in.bankAccountUseCase.DepositMoneyUseCase;
import fr.albin.bank_account.domain.port.in.bankAccountUseCase.GetAccountStatementUseCase;
import fr.albin.bank_account.domain.port.in.bankAccountUseCase.WithdrawMoneyUseCase;
import fr.albin.bank_account.domain.port.in.userUseCase.GetUserInfoUseCase;
import fr.albin.bank_account.infrastructure.DTO.AccountInformation;
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
    private final GetUserInfoUseCase getUserInfoUseCase;

    public BankAccountController(
            CreateBankAccountUseCase createBankAccount,
            CreateBankAccountOverdraftUseCase createBankAccountOverdraft,
            CreateSavingsAccountUseCase createSavingsAccountUseCase,
            DepositMoneyUseCase depositMoney,
            GetAccountStatementUseCase getAccountStatement,
            WithdrawMoneyUseCase withdrawMoney,
            GetUserInfoUseCase getUserInfoUseCase) {
        this.createBankAccountUseCase = createBankAccount;
        this.createBankAccountOverdraftUseCase = createBankAccountOverdraft;
        this.createSavingsAccountUseCase = createSavingsAccountUseCase;
        this.depositMoneyUseCase = depositMoney;
        this.getAccountStatementUseCase = getAccountStatement;
        this.withdrawMoneyUseCase = withdrawMoney;
        this.getUserInfoUseCase = getUserInfoUseCase;
    }

    @PostMapping("/createBankAccount")
    public ResponseEntity<CreateAccountResponse> createBankAccount(
        @Validated @RequestBody CreateBankAccountRequest request, 
        @AuthenticationPrincipal String username) {
        String accountId = createBankAccountUseCase.createBankAccount(request.balance(), username);
        URI location = URI.create("/api/accounts/" + accountId);
        return ResponseEntity.created(location).body(new CreateAccountResponse(accountId)); // 201
    }

    @PostMapping("/createSavingsAccount")
    public ResponseEntity<CreateAccountResponse> createSavingsAccount(
        @Validated @RequestBody CreateSavingsAccountRequest request,
        @AuthenticationPrincipal String username) {
        String accountId = createSavingsAccountUseCase.createSavingsAccount(request.balance(), request.depositCap(), username);
        URI location = URI.create("/api/accounts/" + accountId);
        return ResponseEntity.created(location).body(new CreateAccountResponse(accountId)); // 201
    }

    @PostMapping("/createBankAccountOverdraft")
    public ResponseEntity<CreateAccountResponse> createBankAccountOverdraft(
        @Validated @RequestBody CreateBankAccountOverdraftRequest request,
        @AuthenticationPrincipal String username) {
        String accountId = createBankAccountOverdraftUseCase.createBankAccountOverdraft(request.balance(), request.overdraft(), username);
        URI location = URI.create("/api/accounts/" + accountId);
        return ResponseEntity.created(location).body(new CreateAccountResponse(accountId)); // 201
    }

    @GetMapping("/accountStatement")
    public ResponseEntity<AccountStatement> getAccountStatement(@Validated @RequestBody GetAccountStatementRequest request) {
        AccountStatement accountStatement = getAccountStatementUseCase.getAccountStatement(request.accountNumber(), request.date());
        return ResponseEntity.ok(accountStatement); // 200
    }

    @GetMapping("/allAccounts")
    public ResponseEntity<List<AccountInformation>> getAllAccountsForUser(@AuthenticationPrincipal String username) {
        try{
            User user = getUserInfoUseCase.getUserInfo(username);
            List<AccountInformation> accountInformations = user.getBankAccounts().stream()
                .map(account -> new AccountInformation(
                    account.getAccountNumber(),
                    account.getBalance(),
                    account.getOverdraftLimit(),
                    account.getDepositLimit(),
                    account.getAccountType()
                )).toList();
            return ResponseEntity.ok(accountInformations);
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().build();
        }
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
