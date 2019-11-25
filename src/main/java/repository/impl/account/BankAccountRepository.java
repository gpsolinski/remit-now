package repository.impl.account;

import domain.BankAccount;
import repository.impl.BasicInMemoryRepository;

public class BankAccountRepository extends BasicInMemoryRepository<BankAccount<Long>, Long> {

    @Override
    public BankAccount<Long> findById(Long id) {
        return super.findById(id);
    }

    @Override
    public void save(BankAccount<Long> account) {
        super.save(account);
    }
}
