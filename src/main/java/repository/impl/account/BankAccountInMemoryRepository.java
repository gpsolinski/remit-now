package repository.impl.account;

import domain.Account;
import repository.impl.BasicInMemoryRepository;

public class BankAccountInMemoryRepository extends BasicInMemoryRepository<Account, Long> {

    @Override
    public Account findById(Long id) {
        return super.findById(id);
    }

    @Override
    public void save(Account account) {
        super.save(account);
    }
}
