package fr.albin.bank_account.domain.port.in;


import java.time.LocalDateTime;

import fr.albin.bank_account.domain.model.AccountStatement;

/**
 * Interface qui permet de récupérer le relevé de compte
 */
public interface GetAccountStatementUseCase {
    AccountStatement getAccountStatement(String accountNumber,LocalDateTime emissionDate);
}
