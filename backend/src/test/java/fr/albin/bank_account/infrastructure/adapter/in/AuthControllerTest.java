package fr.albin.bank_account.infrastructure.adapter.in;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.albin.bank_account.domain.port.in.userUseCase.LoginUserUseCase;
import fr.albin.bank_account.domain.port.in.userUseCase.RegisterUserUseCase;

@WebMvcTest(AuthController.class)
@DisplayName("Test /api/auth/ - AuthController")
class AuthControllerTest {
    /* 
    @MockitoBean
    private LoginUserUseCase loginUserUseCase;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("Devrait enregistrer un utilisateur et retourner un token JWT")
    void shouldRegisterUserAndReturnJWTToken() {
    }

    @Test
    @DisplayName("Devrait refuser l'enregistrement avec des données invalides")
    void shouldRejectRegistrationWithInvalidData() {
    }

    @Test
    @DisplayName("Devrait refuser l'enregistrement avec un nom d'utilisateur déjà pris")
    void shouldRejectRegistrationWithTakenUsername() {
    }

    @Test
    @DisplayName("Devrait connecter un utilisateur et retourner un token JWT")
    void shouldLoginUserAndReturnJWTToken() {
    }

    @Test
    @DisplayName("Devrait refuser la connexion avec un username inexistant")
    void shouldRejectLoginWithNonExistentUsername() {
    }

    @Test
    @DisplayName("Devrait refuser la connexion avec un mot de passe incorrect")
    void shouldRejectLoginWithIncorrectPassword() {
    }

    */
}


