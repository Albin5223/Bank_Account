package fr.albin.bank_account.infrastructure.adapter.in;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;
import fr.albin.bank_account.domain.exception.InvalidCredential;
import fr.albin.bank_account.domain.exception.UserAlreadyExistst;
import fr.albin.bank_account.domain.port.in.userUseCase.LoginUserUseCase;
import fr.albin.bank_account.domain.port.in.userUseCase.RegisterUserUseCase;
import fr.albin.bank_account.infrastructure.DTO.LoginRequest;
import fr.albin.bank_account.infrastructure.DTO.RegisterRequest;
import fr.albin.bank_account.infrastructure.security.JwtService;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Test /auth/ - AuthController")
class AuthControllerTest {
    
    @MockitoBean
    private LoginUserUseCase loginUserUseCase;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;


    @Test
    @DisplayName("Devrait enregistrer un utilisateur et retourner un token JWT")
    void shouldRegisterUserAndReturnJWTToken() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testuser", "testpassword", "test@example.com");
        when(registerUserUseCase.registerUser(anyString(), anyString(), anyString())).thenReturn("token12345");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("token12345"));
    }

    
    @Test
    @DisplayName("Devrait refuser l'enregistrement avec des données invalides")
    void shouldRejectRegistrationWithInvalidData() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("test", "testpassword", "invalid-email");
        when(registerUserUseCase.registerUser(anyString(), anyString(), anyString())).thenThrow(new IllegalArgumentException("Invalid email"));
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    
    @Test
    @DisplayName("Devrait refuser l'enregistrement avec un nom d'utilisateur déjà pris")
    void shouldRejectRegistrationWithTakenUsername() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("test", "testpassword", "invalid@domain.com");
        when(registerUserUseCase.registerUser(anyString(), anyString(), anyString())).thenThrow(new UserAlreadyExistst("Username already exists"));
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict());
    }

    
    @Test
    @DisplayName("Devrait connecter un utilisateur et retourner un token JWT")
    void shouldLoginUserAndReturnJWTToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testuser", "testpassword");
        when(loginUserUseCase.loginUser(anyString(), anyString())).thenReturn("token12345");
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token12345"));
    }

     
    @Test
    @DisplayName("Devrait refuser la connexion avec un username inexistant")
    void shouldRejectLoginWithNonExistentUsername() throws Exception {
        LoginRequest loginRequest = new LoginRequest("nonexistentuser", "testpassword");
        when(loginUserUseCase.loginUser(anyString(), anyString())).thenThrow(new InvalidCredential("Invalid username or password"));
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    
    @Test
    @DisplayName("Devrait refuser la connexion avec un mot de passe incorrect")
    void shouldRejectLoginWithIncorrectPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testuser", "wrongpassword");
        when(loginUserUseCase.loginUser(anyString(), anyString())).thenThrow(new InvalidCredential("Invalid username or password"));
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Devrait refuser la connexion avec des formats de données invalides")
    void shouldRejectLoginWithInvalidData() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testuser", "testpassword", "testexample.com");
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }
}
