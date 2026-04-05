package fr.albin.bank_account.infrastructure.DTO;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


/**
 * DTO de requête pour l'obtention du relevé de compte bancaire à une date donnée.
 */
public record GetAccountStatementRequest(
    @NotNull @NotBlank
    String accountNumber,
    @NotNull
    LocalDateTime date
) {
    
}
