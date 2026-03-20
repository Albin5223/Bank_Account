package fr.albin.bank_account.infrastructure.TDO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateBankAccountRequest(
    @NotNull
    @PositiveOrZero
    Double balance)
{}