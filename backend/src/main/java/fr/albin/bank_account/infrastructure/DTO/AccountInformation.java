package fr.albin.bank_account.infrastructure.DTO;

import fr.albin.bank_account.domain.model.enums.TypeAccount;

public record AccountInformation(
    String accountNumber,
    double balance,
    double overdraftLimit,
    double depositLimit,
    TypeAccount typeAccount
) {}
