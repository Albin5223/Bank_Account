package fr.albin.bank_account.infrastructure.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO de requête pour l'enregistrement d'un nouvel utilisateur dans l'application bancaire.
 */
public record RegisterRequest(

    @NotBlank
    String username,

    @NotBlank
    String password,

    @NotBlank
    @Email
    String email
) {
    
}
