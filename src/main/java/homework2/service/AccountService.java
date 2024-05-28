package homework2.service;

import homework2.domain.bank.Account;
import java.util.List;

public interface AccountService {
    Account save(Account account);

    boolean delete(Account account);

    void deleteAll(List<Account> accounts);

    void saveAll(List<Account> accounts);

    List<Account> findAll();

    boolean deleteById(Long id);

    Account getById(Long id);

    Account findByNumber(String accountNumber);

    Account deposit(String number, double amount);

    Account withdraw(String accountNumber, double amount);

    void transfer(String from, String to, double amount);
}
