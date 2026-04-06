package fr.albin.bank_account.domain.port.out.bankAccountPort;
/**
 * Interface qui permet de générer un numéro de compte
 */
public interface GenerateAccountNumberPort {
    String generateAccountNumber();
}