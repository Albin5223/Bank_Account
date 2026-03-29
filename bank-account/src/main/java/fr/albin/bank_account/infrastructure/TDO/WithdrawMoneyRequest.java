package fr.albin.bank_account.infrastructure.TDO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO de requête pour le retrait d'argent d'un compte bancaire.
 */
public record WithdrawMoneyRequest(
    @NotBlank String accountNumber,
    @NotNull @Positive double amount
) {
    
}
