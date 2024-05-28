package homework2.service;

import homework2.dao.AccountDao;
import homework2.dao.CustomerDao;
import homework2.dao.EmployerDao;
import homework2.domain.bank.Account;
import homework2.exceptions.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DefaultAccountService implements AccountService {

    private final AccountDao accountDao;

    @Override
    public Account save(Account account) {
        return accountDao.save(account);
    }

    @Override
    public boolean delete(Account account) {
        return accountDao.delete(account);
    }

    @Override
    public void deleteAll(List<Account> accounts) {
        accountDao.deleteAll(accounts);
    }

    @Override
    public void saveAll(List<Account> accounts) {
        accountDao.saveAll(accounts);
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = NoSuchElementException.class, timeout = 1000)
    public List<Account> findAll() {
        return accountDao.findAll();
    }

    @Override
    public boolean deleteById(Long id) {
        return accountDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getById(Long id) {
        return accountDao.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Account findByNumber(String accountNumber) {
        try {
            UUID number = UUID.fromString(accountNumber.toString());
            return accountDao.findByNumber(number);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid account number format");
        }
    }

    @Override
    public Account deposit(String accountNumber, double amount) {
        try {
            UUID number = UUID.fromString(accountNumber.toString());
            Account account = accountDao.findByNumber(number);
            if (account == null) {
                throw new AccountNotFoundException("Account with number " + number + " not found");
            }

            if (amount <= 0) {
                throw new IllegalArgumentException("Deposit amount must be greater than zero");
            }

            double balance = account.getBalance();
            account.setBalance(balance + amount);
            accountDao.save(account);
            return account;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid account number format");
        }
    }

    @Override
    @Transactional(rollbackFor = IllegalArgumentException.class, timeout = 1000)
    public Account withdraw(String number, double amount) {
        try {
            UUID accountNumber = UUID.fromString(number.toString());
            Account account = accountDao.findByNumber(accountNumber);
            if (account == null) {
                throw new AccountNotFoundException("Account with number " + accountNumber + " not found");
            }

            if (amount <= 0) {
                throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
            }

            double balance = account.getBalance();
            if (balance < amount) {
                throw new  InsufficientBalanceException("Insufficient balance in the account");
            }

            account.setBalance(balance - amount);
            log.info("Withdrawn {} from account with number {}", amount, accountNumber);
            accountDao.save(account);
            return account;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid account number format");
        }
    }

    @Override
    public void transfer(String fromNumber, String toNumber, double amount) {
        try {
            UUID fromAccountNumber = UUID.fromString(fromNumber.toString());
            UUID toAccountNumber = UUID.fromString(toNumber.toString());
            Account fromAccount = accountDao.findByNumber(fromAccountNumber);
            if (fromAccount == null) {
                throw new AccountNotFoundException("From account with number " + fromAccountNumber + " not found");
            }

            Account toAccount = accountDao.findByNumber(toAccountNumber);
            if (toAccount == null) {
                throw new AccountNotFoundException("To account with number " + toAccountNumber + " not found");
            }

            if (fromAccountNumber.equals(toAccountNumber)) {
                throw new SameAccountException("From and To account numbers cannot be the same");
            }

            if (amount <= 0) {
                throw new InvalidTransferAmountException("Transfer amount must be greater than 0");
            }

            double balance = fromAccount.getBalance();
            if (balance < amount) {
                throw new InsufficientBalanceException("Insufficient balance in the from account");
            }

            fromAccount.setBalance(balance - amount);
            toAccount.setBalance(toAccount.getBalance() + amount);
            accountDao.save(fromAccount);
            accountDao.save(toAccount);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid account number format");
        }
    }
}
