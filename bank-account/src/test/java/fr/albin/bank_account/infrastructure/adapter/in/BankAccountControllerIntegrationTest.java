package fr.albin.bank_account.infrastructure.adapter.in;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import fr.albin.bank_account.infrastructure.adapter.out.persistence.repository.BankAccountRepository;
import fr.albin.bank_account.infrastructure.TDO.DepositMoneyRequest;
import fr.albin.bank_account.infrastructure.TDO.GetAccountStatementRequest;
import fr.albin.bank_account.infrastructure.TDO.WithdrawMoneyRequest;
import fr.albin.bank_account.infrastructure.TDO.CreateBankAccountRequest;
import fr.albin.bank_account.infrastructure.TDO.CreateSavingsAccountRequest;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("BankAccountController - test d'intégration")
class BankAccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @BeforeEach
    void cleanDatabase() {
        bankAccountRepository.deleteAll();
    }

    @Test
    @DisplayName("create -> deposit -> withdraw -> statement doit retourner les opérations persistées : avec un compte standard")
    void should_persist_operations_and_return_them_in_statement() throws Exception {
        CreateBankAccountRequest createRequest = new CreateBankAccountRequest(100.0);

        MvcResult createResult = mockMvc.perform(post("/api/accounts/createBankAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accountNumber").exists())
                .andReturn();

        String accountNumber = extractAccountNumberFromBody(createResult);

        DepositMoneyRequest depositRequest = new DepositMoneyRequest(accountNumber, 40.0);
        mockMvc.perform(post("/api/accounts/depositMoney")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isNoContent());

        WithdrawMoneyRequest withdrawRequest = new WithdrawMoneyRequest(accountNumber, 20.0);
        mockMvc.perform(post("/api/accounts/withdrawMoney")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isNoContent());

        GetAccountStatementRequest statementRequest = new GetAccountStatementRequest(
                accountNumber,
                LocalDateTime.now().plusDays(1));

        mockMvc.perform(get("/api/accounts/accountStatement")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statementRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(120.0))
                .andExpect(jsonPath("$.operations.length()").value(3));
    }


    @Test
    @DisplayName("create -> deposit -> withdraw -> statement doit retourner les opérations persistées : avec un compte épargne")
    void should_persist_operations_and_return_them_in_statement_with_savings_account() throws Exception {
        CreateSavingsAccountRequest createRequest = new CreateSavingsAccountRequest(100.0, 5000.0);

        MvcResult createResult = mockMvc.perform(post("/api/accounts/createSavingsAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accountNumber").exists())
                .andReturn();

        String accountNumber = extractAccountNumberFromBody(createResult);

        DepositMoneyRequest depositRequest = new DepositMoneyRequest(accountNumber, 40.0);
        mockMvc.perform(post("/api/accounts/depositMoney")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isNoContent());

        depositRequest = new DepositMoneyRequest(accountNumber, 40.0);
        mockMvc.perform(post("/api/accounts/depositMoney")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isNoContent());

        GetAccountStatementRequest statementRequest = new GetAccountStatementRequest(
                accountNumber,
                LocalDateTime.now().plusDays(1));

        mockMvc.perform(get("/api/accounts/accountStatement")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statementRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(180.0))
                .andExpect(jsonPath("$.operations.length()").value(3));
    }

    private String extractAccountNumberFromBody(MvcResult createResult) throws Exception {
        String content = createResult.getResponse().getContentAsString();
        Map<?, ?> body = objectMapper.readValue(content, Map.class);
        Object accountNumberObject = body.get("accountNumber");
        String accountNumber = accountNumberObject == null ? null : accountNumberObject.toString();
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalStateException("Champ accountNumber absent dans la réponse de création de compte");
        }
        return accountNumber;
    }
}
