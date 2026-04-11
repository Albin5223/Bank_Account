package fr.albin.bank_account.infrastructure.DTO;

public record LoginRequest(
    String username,
    String password
) {
    
}
