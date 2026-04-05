package fr.albin.bank_account.domain.port.in;

/**
 * Interface qui permet de retirer de l'argent
 */
public interface WithdrawMoneyUseCase {
    boolean withdrawMoney(String accountNumber, double money);
}
