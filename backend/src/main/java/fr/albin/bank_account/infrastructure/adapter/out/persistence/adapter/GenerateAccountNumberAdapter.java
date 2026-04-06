package fr.albin.bank_account.infrastructure.adapter.out.persistence.adapter;


import org.springframework.stereotype.Component;

import fr.albin.bank_account.domain.port.out.bankAccountPort.GenerateAccountNumberPort;
/**
 * Implémentation de GenerateAccountNumberPort qui génère un numéro de compte aléatoire
 * avec un préfixe "ACC-" suivi de 12 chiffres.
 */
@Component
public class GenerateAccountNumberAdapter implements GenerateAccountNumberPort {

    private static final String ACCOUNT_NUMBER_PREFIX = "ACC-";
    private static final int ACCOUNT_NUMBER_LENGTH = 12;
    
    @Override
    public String generateAccountNumber() {
        long min = (long) Math.pow(10, ACCOUNT_NUMBER_LENGTH - 1);
        long max = (long) Math.pow(10, ACCOUNT_NUMBER_LENGTH) - 1;
        long value = min + (long) (Math.random() * (max - min + 1));
        return ACCOUNT_NUMBER_PREFIX + value;
    }
    
}