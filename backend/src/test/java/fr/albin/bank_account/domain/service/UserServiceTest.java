package fr.albin.bank_account.domain.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.albin.bank_account.domain.exception.InvalidCredential;
import fr.albin.bank_account.domain.exception.UserAlreadyExistst;
import fr.albin.bank_account.domain.model.User;
import fr.albin.bank_account.domain.port.out.userPort.EncodePasswordPort;
import fr.albin.bank_account.domain.port.out.userPort.GenerateTokenPort;
import fr.albin.bank_account.domain.port.out.userPort.LoadUserPort;
import fr.albin.bank_account.domain.port.out.userPort.SaveUserPort;

@DisplayName("UserService - tests applicatifs")
public class UserServiceTest {

    private UserService setupService() {
        LoadUserPortImpl loadUserPort = new LoadUserPortImpl();
        GenerateTokenPortImpl generateTokenPort = new GenerateTokenPortImpl();
        EncodePasswordPortImpl encodePasswordPort = new EncodePasswordPortImpl();
        return new UserService(loadUserPort, loadUserPort, generateTokenPort, encodePasswordPort);
    }

    @Test
    @DisplayName("Enregistre un utilisateur et génère un token")
    void should_register_user_and_generate_token() {
        UserService service = setupService();

        String token = service.registerUser("parisa", "password123","parisa@example.com");
        assert token.equals("token-for-parisa");
    }

    @Test
    @DisplayName("Lance une exception pour un nom d'utilisateur déjà existant")
    void should_throw_exception_for_existing_username() {
        UserService service = setupService();

        service.registerUser("parisa", "password123","parisa@example.com");
        assertThrows(UserAlreadyExistst.class, () -> {
            service.registerUser("parisa", "password123","parisa@example.com");
        });
    }

    @Test
    @DisplayName("Lance une exception pour un mot de passe incorrect lors de la connexion")
    void should_throw_exception_for_invalid_password() {
        UserService service = setupService();
        service.registerUser("parisa", "password123","parisa@example.com");
        assertThrows(InvalidCredential.class, () -> {
            service.loginUser("parisa", "wrongpassword");
        });
    }


    @Test
    @DisplayName("Lance une exception pour un nom d'utilisateur inexistant lors de la connexion")
    void should_throw_exception_for_nonexistent_username() {
        UserService service = setupService();
        service.registerUser("parisa", "password123","parisa@example.com");
        assertThrows(InvalidCredential.class, () -> {
            service.loginUser("nonexistent", "password123");
        });
    }

    @Test
    @DisplayName("Connecte un utilisateur avec des identifiants valides et génère un token")
    void should_login_user_and_generate_token() {
        UserService service = setupService();
        service.registerUser("parisa", "password123","parisa@gmail.com");
        String token = service.loginUser("parisa", "password123");
        assert token.equals("token-for-parisa");
    }



    private static final class LoadUserPortImpl implements LoadUserPort, SaveUserPort {

        private final Map<String, User> usersByEmail = new HashMap<>();
        private final Map<String, User> usersByUsername = new HashMap<>();
        
        @Override
        public Optional<User> loadUserByEmail(String email) {
            return Optional.ofNullable(usersByEmail.get(email));
        }

        @Override
        public Optional<User> loadUserByUsername(String username) {
            return Optional.ofNullable(usersByUsername.get(username));
        }

        @Override
        public void save(User user) {
            usersByEmail.put(user.getEmail(), user);
            usersByUsername.put(user.getUsername(), user);
        }
    }

    private static final class GenerateTokenPortImpl implements GenerateTokenPort {
        @Override
        public String generateToken(User user) {
            return "token-for-" + user.getUsername();
        }
    }

    private static final class EncodePasswordPortImpl implements EncodePasswordPort {
        @Override
        public String encode(String rawPassword) {
            return "encoded-" + rawPassword;
        }

        @Override
        public boolean matches(String rawPassword, String encodedPassword) {
            return encodedPassword.equals(encode(rawPassword));
        }
    }
    
}
