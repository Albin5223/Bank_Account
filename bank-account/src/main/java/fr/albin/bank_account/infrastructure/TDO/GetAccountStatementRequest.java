package fr.albin.bank_account.infrastructure.TDO;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record GetAccountStatementRequest(
    @NotNull
    String accountNumber,
    @NotNull
    LocalDateTime date
) {
    
}
