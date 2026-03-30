package fr.albin.bank_account.infrastructure.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


/**
 * DTO de requête pour la création d'un compte bancaire avec découvert autorisé.
 */
public record CreateBankAccountOverdraftRequest(
    @NotNull @Positive double balance,
    @NotNull @Positive double overdraft
) {
    
}
