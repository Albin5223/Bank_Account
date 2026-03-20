package fr.albin.bank_account.infrastructure.TDO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DepositMoneyRequest(
    @NotBlank String accountNumber,
    @NotNull @Positive double amount
) {}