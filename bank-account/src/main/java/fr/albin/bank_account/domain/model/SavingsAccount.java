package fr.albin.bank_account.domain.model;

import fr.albin.bank_account.domain.exception.DepositCapExceededException;
import fr.albin.bank_account.domain.exception.InvalidAmountException;
import fr.albin.bank_account.domain.model.enums.TypeAccount;

public class SavingsAccount extends BankAccount{

    protected double depositCap;

    /**
    * Cette classe représente un livret d'épargne sans autorisation de plafond et possédant un plafond
    * <p>
    * @param number est le numéro du compte
    * @param balance est le solde de départ du compte
    * @param limit est le plafond du livret
    * </p>
    */
    public SavingsAccount(String number, double balance, double depositCap) {
        super(number, balance);
        if (depositCap < 0) {
            throw new InvalidAmountException("Le plafond de dépôt ne peut pas être négatif");
        }
        if(balance>depositCap){
            throw new DepositCapExceededException("Plafond dépassé lors de la création du compte");
        }
        this.depositCap = depositCap;
    }

    public double getDepositCap(){
        return depositCap;
    }

    @Override
    protected TypeAccount getAccountType() {
        return TypeAccount.SavingsAccount;
    }

    @Override
    public void checkDepositValid(double depot){
        if (depot <= 0) {
            throw new InvalidAmountException("Le montant du dépôt doit être strictement positif");
        }
        if(super.balance + depot > depositCap){
            throw new DepositCapExceededException("Plafond dépassé");
        }
    }


    
    
}
