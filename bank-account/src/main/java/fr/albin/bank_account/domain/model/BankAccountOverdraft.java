package fr.albin.bank_account.domain.model;

import java.time.LocalDateTime;

import fr.albin.bank_account.domain.exception.InvalidAmountException;
import fr.albin.bank_account.domain.exception.OverdraftLimitExceededException;
import fr.albin.bank_account.domain.model.enums.TypeOperation;

public class BankAccountOverdraft extends BankAccount {

    protected double limit;

    /**
    * Cette classe représent un compte bancaire qui possède une autorisation de découvert avec une limit
    * <p>
    * @param number est le numéro du compte
    * @param balance est le solde de départ du compte
    * @param limit est la limit de découvert > 0
    * </p>
    */
    public BankAccountOverdraft(String number, double balance, double limit) {
        super(number, balance);
        if (limit < 0) {
            throw new InvalidAmountException("La limite de découvert ne peut pas être négative");
        }
        this.limit = limit;
    }

    @Override
    public void withdraw(double retrait){
        if (retrait <= 0) {
            throw new InvalidAmountException("Le montant du retrait doit être strictement positif");
        }
        if (balance - retrait < -limit){
            throw new OverdraftLimitExceededException("Limite de découvert dépassée");
        }
        balance -= retrait;
        operations.add(new Operation(LocalDateTime.now(), TypeOperation.WITHDRAWAL, retrait));
    }
    
}
