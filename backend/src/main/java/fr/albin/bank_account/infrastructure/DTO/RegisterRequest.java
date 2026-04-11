package fr.albin.bank_account.infrastructure.DTO;

/**
 * DTO de requête pour l'enregistrement d'un nouvel utilisateur dans l'application bancaire.
 */
public record RegisterRequest(
    String username,
    String password,
    String email
) {
    
}
