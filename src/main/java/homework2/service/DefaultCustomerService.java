package homework2.service;

import homework2.dao.AccountDao;
import homework2.dao.CustomerDao;
import homework2.dao.EmployerDao;
import homework2.domain.bank.Account;
import homework2.domain.bank.Currency;
import homework2.domain.bank.Customer;
import homework2.domain.bank.Employer;
import homework2.exceptions.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DefaultCustomerService implements CustomerService {

    private final CustomerDao customerDao;
    private final AccountDao accountDao;
    private final EmployerDao employerDao;

    @Override
    public Customer save(Customer customer) {
        Customer existingCustomer = customerDao.getByEmail(customer.getEmail());
        if (existingCustomer != null) {
            throw new SameCustomerException("Customer with email " + customer.getEmail() + " already exists");
        } else {
            customerDao.save(customer);
            return customer;
        }
    }

    @Override
    public boolean delete(Customer customer) {
        return customerDao.delete(customer);
    }

    @Override
    public void deleteAll(List<Customer> customers) {
        customerDao.deleteAll(customers);
    }

    @Override
    public void saveAll(List<Customer> customers) {
        customerDao.saveAll(customers);
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = NoSuchElementException.class, timeout = 1000)
    public List<Customer> findAll() {
        return customerDao.findAll();
    }

    @Override
    public boolean deleteById(Long id) {
        return customerDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getById(Long id) {
        return customerDao.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getByEmail(String email) {
        return customerDao.getByEmail(email);
    }

    @Override
    public Customer update(Customer customer) {
        if (customer == null) {
            throw new CustomerNotFoundException("Customer cannot be null");
        }

        Customer existingCustomer = customerDao.getById(customer.getId());
        if (existingCustomer == null) {
            throw new CustomerNotFoundException("Customer not found with id " + customer.getId());
        }

        updateCustomerData(existingCustomer, customer);

        return customerDao.save(existingCustomer);
    }

    private void updateCustomerData(Customer existingCustomer, Customer newCustomer) {
        if (existingCustomer.getName().equals(newCustomer.getName()) &&
                existingCustomer.getEmail().equals(newCustomer.getEmail()) &&
                existingCustomer.getAge().equals(newCustomer.getAge())) {

            throw new SameCustomerException("The provided customer data is identical to the existing data.");
        }

        existingCustomer.setName(newCustomer.getName());
        existingCustomer.setEmail(newCustomer.getEmail());
        existingCustomer.setAge(newCustomer.getAge());
    }

    @Override
    public void createAccount(Long customerId, Account account) {
        Customer customer = customerDao.getById(customerId);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found with id: " + customerId);
        }

        if (account == null) {
            throw new AccountNotFoundException("Account cannot be null");
        }

        try {
            customer.getAccounts().add(account);
            account.setCustomer(customer);
            customerDao.save(customer);
        } catch (SameAccountException e) {
            throw new SameAccountException("Account number " + account.getNumber() + " already exists");
        } catch (RuntimeException e) {
            throw new RuntimeException("Error adding employer to customer with id: " + customerId, e);
        }

    }

    @Override
    public void deleteAccount(Long customerId, UUID accountNumber) {
        Customer customer = customerDao.getById(customerId);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found with id: " + customerId);
        }

        // Знаходимо акаунт для видалення
        Account accountToDelete = customer.getAccounts().stream()
                .filter(account -> account.getNumber().equals(accountNumber))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException("Account with number " + accountNumber + " not found for customer with id " + customerId));

        // Видаляємо акаунт з акаунтів клієнта
        if (customer.getAccounts().removeIf(account -> account.getNumber().equals(accountNumber))) {
            accountDao.delete(accountToDelete);
            customer.getAccounts().remove(accountToDelete);
        }
    }

    public void addEmployer(Long customerId, Employer employer) {
        Customer customer = customerDao.getById(customerId);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found with id " + customerId);
        }

        if (employer == null) {
            throw new EmployerNotFoundException("Employer cannot be null");
        }

        try {
            customer.getEmployers().add(employer);
            employer.getCustomers().add(customer);
            customerDao.save(customer);
            log.info("Added employer with id {} for customer with id {}", employer.getId(), customerId);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error adding employer to customer with id: " + customerId, e);
        }
    }

    public void deleteEmployer(Long customerId, Long employerId) {
        Customer customer = customerDao.getById(customerId);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found with id " + customerId);
        }

        Employer employer = employerDao.getById(employerId);
        if (employer == null) {
            throw new EmployerNotFoundException("Employer not found with id " + employerId);
        }

        if (customer.getEmployers().remove(employer)) {
            employer.getCustomers().remove(customer);
            employerDao.save(employer);
            log.info("Employer with id {} removed from customer with id {}", employerId, customerId);
        } else {
            log.error("Employer with id {} not associated with customer with id {}", employerId, customerId);
            throw new EmployerForCustomerNotFoundException("Employer is not associated with customer");
        }

        customerDao.save(customer);
        log.info("Deleted employer with id {} for customer with id {}", employerId, customerId);
    }
}
