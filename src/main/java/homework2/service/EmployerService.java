package homework2.service;

import homework2.domain.bank.Customer;
import homework2.domain.bank.Employer;
import java.util.List;

public interface EmployerService {

    Employer save(Employer employer);

    boolean delete(Employer employer);

    void deleteAll(List<Employer> employers);

    void saveAll(List<Employer> employers);

    List<Employer> findAll();

    boolean deleteById(Long id);

    Employer getById(Long id);

    Employer getByAddress(String address);

    Employer update(Employer employer);

    void addCustomer(Long employerId, Customer customer);

    void deleteCustomer(Long employerId, Long customerId);
}
