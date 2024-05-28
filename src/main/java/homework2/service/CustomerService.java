package homework2.service;

import homework2.domain.bank.Account;
import homework2.domain.bank.Customer;
import homework2.domain.bank.Employer;
import java.util.UUID;
import java.util.List;

public interface CustomerService {

    Customer save(Customer customer);

    boolean delete(Customer customer);

    void deleteAll(List<Customer> customers);

    void saveAll(List<Customer> customers);

    List<Customer> findAll();

    boolean deleteById(Long id);

    Customer getById(Long id);

    Customer getByEmail(String email);

    Customer update(Customer customer);

    void createAccount(Long customerId, Account account);

    void deleteAccount(Long customerId, UUID accountNumber);

    void addEmployer(Long customerId, Employer employer);

    void deleteEmployer(Long customerId, Long employerId);
}
