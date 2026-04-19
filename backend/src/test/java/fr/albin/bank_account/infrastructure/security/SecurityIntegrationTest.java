package fr.albin.bank_account.infrastructure.security;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import fr.albin.bank_account.infrastructure.DTO.CreateBankAccountRequest;
import fr.albin.bank_account.infrastructure.DTO.DepositMoneyRequest;
import fr.albin.bank_account.infrastructure.DTO.GetAccountStatementRequest;
import fr.albin.bank_account.infrastructure.DTO.LoginRequest;
import fr.albin.bank_account.infrastructure.DTO.RegisterRequest;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@DisplayName("Security Integration Test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Devrait refuser l'accès aux endpoints sécurisés sans token JWT")
    void shouldRejectAccessToSecuredEndpointsWithoutJWTToken() throws Exception {
        CreateBankAccountRequest request = new CreateBankAccountRequest(1000.00);
        mockMvc.perform(post("/api/accounts/createBankAccount")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Devrait refuser l'accès aux endpoints sécurisés sans token JWT si la requête est de type GET")
    void shouldRejectAccessToSecuredEndpointsWithoutJWTTokenWhenRequestIsGet() throws Exception {
        CreateBankAccountRequest request = new CreateBankAccountRequest(1000.00);
        mockMvc.perform(get("/api/accounts/getBankAccount")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("""
    1. Créer un utilisateur
    2. Ajouter un compte bancaire
    """)
    void shouldAllowAccessToSecuredEndpointsWithValidJWTToken() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testuser", "testpassword", "test@example.com");
        MvcResult mvcResult = mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isCreated())
            .andReturn();
        String responseContent = mvcResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseContent).get("token").stringValue();

        CreateBankAccountRequest createBankAccountRequest = new CreateBankAccountRequest(1000.00);
        mockMvc.perform(post("/api/accounts/createBankAccount")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createBankAccountRequest)))
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("""
    1. Créer un utilisateur
    2. Authentifier avec des données invalides pour vérifier que l'accès est refusé
    3. Authentifier avec les bonnes données pour obtenir un token JWT
    """)
    void shouldRejectAuthenticationWithInvalidCredentials() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testuser", "testpassword", "test@example.com");
        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isCreated());

        // Tentative d'authentification avec des données invalides
        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new LoginRequest("testuser", "invalidpassword"))))
            .andExpect(status().isBadRequest());

        // Tentative d'authentification avec les données correctes
        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new LoginRequest("testuser", "testpassword"))))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("""
    1. Créer un utilisateur
    2. Authentifier l'utilisateur pour obtenir un token JWT
    3. Créer un compte bancaire
    4. Ajouterde l'argent
    5. Se connecter 
    6. Vérifier que le solde est correct""")
    void shouldPerformFullAuthenticationAndAccountOperationsFlow() throws Exception {
        // 1. Créer un utilisateur
        RegisterRequest registerRequest = new RegisterRequest("testuser", "testpassword", "test@gmail.com");

        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isCreated())
            .andReturn();
        
        // 2. Authentifier l'utilisateur pour obtenir un token JWT
        LoginRequest loginRequest = new LoginRequest("testuser", "testpassword");
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn();
        String loginResponseContent = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(loginResponseContent).get("token").stringValue();

        // 3. Créer un compte bancaire
        CreateBankAccountRequest createBankAccountRequest = new CreateBankAccountRequest(1000.00);
        MvcResult createAccountResult = mockMvc.perform(post("/api/accounts/createBankAccount")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createBankAccountRequest)))
            .andExpect(status().isCreated())
            .andReturn();

        // 4. Ajouter de l'argent
        String responseContent = createAccountResult.getResponse().getContentAsString();
        String accountId = objectMapper.readTree(responseContent).get("accountNumber").stringValue();
        DepositMoneyRequest depositMoneyRequest = new DepositMoneyRequest(accountId, 500.00);
        mockMvc.perform(post("/api/accounts/depositMoney")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(depositMoneyRequest)))
            .andExpect(status().is(204));

        // 5. Se connecter (vérification que l'utilisateur peut se ré-authentifier)
        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk());

        // 6. Vérifier que le solde est correct (1000 + 500 = 1500)
        GetAccountStatementRequest accountStatementRequest = new GetAccountStatementRequest(accountId, LocalDateTime.now());
        mockMvc.perform(get("/api/accounts/accountStatement")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(accountStatementRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.balance").value(1500.0));
    }
}
