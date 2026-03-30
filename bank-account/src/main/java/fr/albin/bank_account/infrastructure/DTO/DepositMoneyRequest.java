package fr.albin.bank_account.infrastructure.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO de requête pour le dépôt d'argent sur un compte bancaire.
 */
public record DepositMoneyRequest(
    @NotBlank @NotNull String accountNumber,
    @NotNull @Positive double amount
) {}