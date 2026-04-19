package fr.albin.bank_account.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import fr.albin.bank_account.infrastructure.DTO.CreateBankAccountRequest;
import fr.albin.bank_account.infrastructure.DTO.RegisterRequest;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@DisplayName("Security Integration Test")
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
    @DisplayName("Devrait autoriser l'accès aux endpoints sécurisés avec un token JWT valide")
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
}
