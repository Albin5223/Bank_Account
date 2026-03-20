package fr.albin.bank_account.infrastructure.adapter.in;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.List;

import fr.albin.bank_account.domain.exception.InsufficientBalanceException;
import fr.albin.bank_account.domain.exception.InvalidAmountException;
import fr.albin.bank_account.domain.exception.OverdraftLimitExceededException;
import fr.albin.bank_account.domain.model.AccountStatement;
import fr.albin.bank_account.domain.model.enums.TypeAccount;
import fr.albin.bank_account.domain.port.in.*;
import fr.albin.bank_account.infrastructure.TDO.*;
import tools.jackson.databind.ObjectMapper;


@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    properties = {
        "spring.autoconfigure.exclude=" +
            "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration," +
            "org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration," +
            "org.springframework.boot.jdbc.autoconfigure.DataSourceInitializationAutoConfiguration," +
            "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration," +
            "org.springframework.boot.data.jpa.autoconfigure.JpaRepositoriesAutoConfiguration",
        "spring.sql.init.mode=never"
    }
)
@AutoConfigureMockMvc
@DisplayName("POST /api/accounts/create - contrat d'ouverture de compte")
class BankAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateBankAccountUseCase createBankAccountUseCase;

    @MockitoBean
    private CreateBankAccountOverdraftUseCase createBankAccountOverdraftUseCase;

    @MockitoBean
    private CreateSavingsAccountUseCase createSavingsAccountUseCase;

    @MockitoBean
    private DepositMoneyUseCase depositMoneyUseCase;

    @MockitoBean
    private GetAccountStatementUseCase getAccountStatementUseCase;

    @MockitoBean
    private WithdrawMoneyUseCase withdrawMoneyUseCase;


    @Test
    @DisplayName("Teste l'endpoint qui permet de créer un compte - Devrait terminer en succès")
    void testCreateBankAccount_Success() throws Exception {
        CreateBankAccountRequest request = new CreateBankAccountRequest(1000.00);
        when(createBankAccountUseCase.createBankAccount(anyDouble())).thenReturn("ACC-001");

        mockMvc.perform(post("/api/accounts/createBankAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Teste l'endpoint qui permet de créer un compte - Devrait terminer en erreur")
    void testCreateBankAccount_error() throws Exception {
        CreateBankAccountRequest request = new CreateBankAccountRequest(1000.00);
        when(createBankAccountUseCase.createBankAccount(anyDouble()))
                .thenThrow(new InsufficientBalanceException("Solde insuffisant"));

        mockMvc.perform(post("/api/accounts/createBankAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // 400
    }

    @Test
    @DisplayName("Teste l'endpoint qui permet de créer un compte - Devrait terminer en erreur à cause ")
    void testCreateBankAccount_error_InvalideArgument() throws Exception {
        CreateBankAccountRequest request = new CreateBankAccountRequest(-1000.00);
        when(createBankAccountUseCase.createBankAccount(anyDouble()))
                .thenThrow(new InsufficientBalanceException("Solde insuffisant"));

        mockMvc.perform(post("/api/accounts/createBankAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // 400
    }

    @Test
    @DisplayName("Teste l'endpoint qui permet de créer un compte épargne - Devrait terminer en succès")
    void testCreateSavingsAccount_Success() throws Exception {
        CreateSavingsAccountRequest request = new CreateSavingsAccountRequest(1000.00, 5000.00);
        when(createSavingsAccountUseCase.createSavingsAccount(anyDouble(), anyDouble())).thenReturn("SAV-001");

        mockMvc.perform(post("/api/accounts/createSavingsAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Teste l'endpoint qui permet de créer un compte épargne - Devrait terminer en erreur d'argument invalide")
    void testCreateSavingsAccount_error_InvalidArgument() throws Exception {
        CreateSavingsAccountRequest request = new CreateSavingsAccountRequest(-1000.00, 5000.00);
        when(createSavingsAccountUseCase.createSavingsAccount(anyDouble(), anyDouble()))
                .thenThrow(new IllegalArgumentException("Solde doit être positif"));

        mockMvc.perform(post("/api/accounts/createSavingsAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // 400
    }

    @Test
    @DisplayName("Teste l'endpoint qui permet de créer un compte épargne - Devrait terminer en erreur de conflit")
    void testCreateSavingsAccount_error_Conflict() throws Exception {
        CreateSavingsAccountRequest request = new CreateSavingsAccountRequest(1000.00, 5000.00);
        when(createSavingsAccountUseCase.createSavingsAccount(anyDouble(), anyDouble()))
                .thenThrow(new IllegalStateException("Compte déjà existant"));

        mockMvc.perform(post("/api/accounts/createSavingsAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict()); // 409
    }

    @Test
    @DisplayName("Teste l'endpoint qui permet de créer un compte avec découvert - Devrait terminer en succès")
    void testCreateBankAccountOverdraft_Success() throws Exception {
        CreateBankAccountOverdraftRequest request = new CreateBankAccountOverdraftRequest(1000.00, 500.00);
        when(createBankAccountOverdraftUseCase.createBankAccountOverdraft(anyDouble(), anyDouble())).thenReturn("OVD-001");

        mockMvc.perform(post("/api/accounts/createBankAccountOverdraft")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Teste l'endpoint qui permet de créer un compte avec découvert - Devrait terminer en erreur d'argument invalide")
    void testCreateBankAccountOverdraft_error_InvalidArgument() throws Exception {
        CreateBankAccountOverdraftRequest request = new CreateBankAccountOverdraftRequest(-1000.00, 500.00);
        when(createBankAccountOverdraftUseCase.createBankAccountOverdraft(anyDouble(), anyDouble()))
                .thenThrow(new IllegalArgumentException("Solde doit être positif"));

        mockMvc.perform(post("/api/accounts/createBankAccountOverdraft")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // 400
    }

    @Test
    @DisplayName("Teste l'endpoint qui permet de créer un compte avec découvert - Devrait terminer en erreur de conflit")
    void testCreateBankAccountOverdraft_error_Conflict() throws Exception {
        CreateBankAccountOverdraftRequest request = new CreateBankAccountOverdraftRequest(1000.00, 500.00);
        when(createBankAccountOverdraftUseCase.createBankAccountOverdraft(anyDouble(), anyDouble()))
                .thenThrow(new IllegalStateException("Compte déjà existant"));

        mockMvc.perform(post("/api/accounts/createBankAccountOverdraft")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict()); // 409
    }

    @Test
    @DisplayName("Teste l'endpoint qui permet de consulter le relevé de compte - Devrait terminer en succès")
    void testGetAccountStatement_Success() throws Exception {
        GetAccountStatementRequest request = new GetAccountStatementRequest("ACC-001", LocalDateTime.now());
        AccountStatement accountStatement = new AccountStatement(TypeAccount.BankAccount, 1000.00, LocalDateTime.now(), List.of());
        
        when(getAccountStatementUseCase.getAccountStatement(anyString(), any(LocalDateTime.class)))
                .thenReturn(accountStatement);

        mockMvc.perform(get("/api/accounts/accountStatement")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()); // 200
    }

    @Test
    @DisplayName("Teste l'endpoint de dépôt d'argent - Devrait terminer en succès")
    void testDepositMoney_Success() throws Exception {
        DepositMoneyRequest request = new DepositMoneyRequest("ACC-001", 500.00);
        when(depositMoneyUseCase.depositMoney(anyString(), anyDouble()))
                .thenReturn(true);

        mockMvc.perform(post("/api/accounts/depositMoney")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent()); // 204
    }

    @Test
    @DisplayName("Teste l'endpoint de dépôt d'argent - Devrait terminer en erreur d'argument invalide")
    void testDepositMoney_error_InvalidArgument() throws Exception {
        DepositMoneyRequest request = new DepositMoneyRequest("ACC-001", -500.00);
        when(depositMoneyUseCase.depositMoney(anyString(), anyDouble()))
                .thenThrow(new InvalidAmountException("Montant doit être positif"));

        mockMvc.perform(post("/api/accounts/depositMoney")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // 400
    }

    @Test
    @DisplayName("Teste l'endpoint de dépôt d'argent - Devrait terminer en erreur compte non trouvé")
    void testDepositMoney_error_NotFound() throws Exception {
        DepositMoneyRequest request = new DepositMoneyRequest("ACC-999", 500.00);
        when(depositMoneyUseCase.depositMoney(anyString(), anyDouble()))
                .thenThrow(new NoSuchElementException("Compte non trouvé"));

        mockMvc.perform(post("/api/accounts/depositMoney")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound()); // 404
    }

    @Test
    @DisplayName("Teste l'endpoint de retrait d'argent - Devrait terminer en succès")
    void testWithdrawMoney_Success() throws Exception {
        WithdrawMoneyRequest request = new WithdrawMoneyRequest("ACC-001", 500.00);
        when(withdrawMoneyUseCase.withdrawMoney(anyString(), anyDouble()))
                        .thenReturn(true);
        mockMvc.perform(post("/api/accounts/withdrawMoney")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent()); // 204
    }

    @Test
    @DisplayName("Teste l'endpoint de retrait d'argent - Devrait terminer en erreur d'argument invalide")
    void testWithdrawMoney_error_InvalidArgument() throws Exception {
        WithdrawMoneyRequest request = new WithdrawMoneyRequest("ACC-001", -500.00);
        when(withdrawMoneyUseCase.withdrawMoney(anyString(), anyDouble()))
                .thenThrow(new InvalidAmountException("Montant doit être positif"));

        mockMvc.perform(post("/api/accounts/withdrawMoney")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // 400
    }

    @Test
    @DisplayName("Teste l'endpoint de retrait d'argent - Devrait terminer en erreur compte non trouvé")
    void testWithdrawMoney_error_NotFound() throws Exception {
        WithdrawMoneyRequest request = new WithdrawMoneyRequest("ACC-999", 500.00);
        when(withdrawMoneyUseCase.withdrawMoney(anyString(), anyDouble()))
                .thenThrow(new NoSuchElementException("Compte non trouvé"));

        mockMvc.perform(post("/api/accounts/withdrawMoney")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound()); // 404
    }

    @Test
    @DisplayName("Teste l'endpoint de retrait d'argent - Devrait terminer en erreur solde insuffisant")
    void testWithdrawMoney_error_InsufficientBalance() throws Exception {
        WithdrawMoneyRequest request = new WithdrawMoneyRequest("ACC-001", 10000.00);
        when(withdrawMoneyUseCase.withdrawMoney(anyString(), anyDouble()))
                .thenThrow(new OverdraftLimitExceededException("Solde insuffisant"));

        mockMvc.perform(post("/api/accounts/withdrawMoney")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict()); // 409
    }
}