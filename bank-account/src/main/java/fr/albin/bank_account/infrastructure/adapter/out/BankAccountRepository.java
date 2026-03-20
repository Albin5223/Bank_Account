package fr.albin.bank_account.infrastructure.adapter.out;

import java.util.Optional;

import fr.albin.bank_account.domain.model.BankAccount;
import fr.albin.bank_account.domain.port.out.LoadBankAccountPort;
import fr.albin.bank_account.domain.port.out.SaveBankAccountPort;

public class BankAccountRepository implements SaveBankAccountPort,LoadBankAccountPort{

    @Override
    public Optional<BankAccount> loadByAccountNumber(String accountNumber) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadByAccountNumber'");
    }

    @Override
    public void save(BankAccount account) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

}
