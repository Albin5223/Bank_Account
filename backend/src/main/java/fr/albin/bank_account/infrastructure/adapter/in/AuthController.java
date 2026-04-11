package fr.albin.bank_account.infrastructure.adapter.in;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.albin.bank_account.domain.port.in.userUseCase.LoginUserUseCase;
import fr.albin.bank_account.domain.port.in.userUseCase.RegisterUserUseCase;
import fr.albin.bank_account.infrastructure.DTO.AuthResponse;
import fr.albin.bank_account.infrastructure.DTO.LoginRequest;
import fr.albin.bank_account.infrastructure.DTO.RegisterRequest;

/**
 * Contrôleur REST pour l'authentification des utilisateurs. 
 * Il gère les endpoints pour l'enregistrement et la connexion des utilisateurs.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginUserUseCase loginUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;

    public AuthController(LoginUserUseCase loginUserUseCase, RegisterUserUseCase registerUserUseCase) {
        this.loginUserUseCase = loginUserUseCase;
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> createBankAccount(@Validated @RequestBody RegisterRequest request) {
        String token = registerUserUseCase.registerUser(request.username(), request.password(), request.email());
        return ResponseEntity.status(201).body(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Validated @RequestBody LoginRequest request) {
        String token = loginUserUseCase.loginUser(request.username(), request.password());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
