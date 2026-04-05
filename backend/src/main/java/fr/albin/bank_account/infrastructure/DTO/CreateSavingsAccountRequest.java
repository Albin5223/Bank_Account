package fr.albin.bank_account.infrastructure.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


/**
 * DTO de requête pour la création d'un compte d'épargne.
 */
public record CreateSavingsAccountRequest(
    @NotNull @Positive double balance,
    @NotNull @Positive double depositCap
) {
    
}
