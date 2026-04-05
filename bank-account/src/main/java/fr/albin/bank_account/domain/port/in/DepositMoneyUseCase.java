package fr.albin.bank_account.domain.port.in;

/**
 * Interface qui permet de déposer de l'argent
 */
public interface DepositMoneyUseCase {
    boolean depositMoney(String accountNumber, double money);
}