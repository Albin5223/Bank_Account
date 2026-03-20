package fr.albin.bank_account.infrastructure.TDO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateBankAccountOverdraftRequest(
    @NotNull @Positive double balance,
    @NotNull @Positive double overdraft
) {
    
}
