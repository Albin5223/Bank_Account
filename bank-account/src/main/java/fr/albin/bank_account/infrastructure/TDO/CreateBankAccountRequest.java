package fr.albin.bank_account.infrastructure.TDO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;


/**
 * DTO de requête pour la création d'un compte bancaire.
 */
public record CreateBankAccountRequest(
    @NotNull
    @PositiveOrZero
    Double balance)
{}