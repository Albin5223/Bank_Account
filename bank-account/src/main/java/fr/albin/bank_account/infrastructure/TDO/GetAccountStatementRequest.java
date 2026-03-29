package fr.albin.bank_account.infrastructure.TDO;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;


/**
 * DTO de requête pour l'obtention du relevé de compte bancaire à une date donnée.
 */
public record GetAccountStatementRequest(
    @NotNull
    String accountNumber,
    @NotNull
    LocalDateTime date
) {
    
}
