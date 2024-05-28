package homework2.service;

import homework2.dao.CustomerDao;
import homework2.dao.EmployerDao;
import homework2.domain.bank.Customer;
import homework2.domain.bank.Employer;
import homework2.exceptions.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DefaultEmployerService implements EmployerService {

    private final EmployerDao employerDao;
    private final CustomerDao customerDao;

    @Override
    public Employer save(Employer employer) {
        try {
            return employerDao.save(employer);
        } catch (Exception e) {
            throw new SameEmployerException("Employer with address " + employer.getAddress() + " already exists");
        }
    }

    @Override
    public boolean delete(Employer employer) {
        return employerDao.delete(employer);
    }

    @Override
    public void deleteAll(List<Employer> employers) {
        employerDao.deleteAll(employers);
    }

    @Override
    public void saveAll(List<Employer> employers) {
        employerDao.saveAll(employers);
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = NoSuchElementException.class, timeout = 1000)
    public List<Employer> findAll() {
        return employerDao.findAll();
    }

    @Override
    public boolean deleteById(Long id) {
        return employerDao.deleteById(id);
    }


    @Override
    @Transactional(readOnly = true)
    public Employer getById(Long id) {
        return employerDao.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Employer getByAddress(String address) {
        return employerDao.getByAddress(address);
    }

    @Override
    public Employer update(Employer employer) {
        Employer existingEmployer = employerDao.getById(employer.getId());
        try {
            if (existingEmployer != null) {
                existingEmployer.setName(employer.getName());
                existingEmployer.setAddress(employer.getAddress());
                return employerDao.save(existingEmployer);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new SameEmployerException("Employer with address " + employer.getAddress() + " already exists");
        }
    }

    @Override
    public void addCustomer(Long employerId, Customer customer) {
        Employer employer = employerDao.getById(employerId);
        if (employer == null) {
            throw new EmployerNotFoundException("Employer not found with id " + employerId);
        }

        if (customer == null) {
            throw new CustomerNotFoundException("Customer cannot be null");
        }

        // Перевіряємо, чи існує клієнт з таким email
        Customer existingCustomer = customerDao.getByEmail(customer.getEmail());
        if (existingCustomer != null) {
            // Оновлюємо інформацію про існуючого клієнта
            existingCustomer.setAge(customer.getAge());
            existingCustomer.setName(customer.getName());
            customer = existingCustomer;
        } else {
            // Додаємо нового клієнта
            customerDao.save(customer);
        }

        if (customerDao.findByEmployerId(employerId).contains(customer)) {
            log.info("Customer {} is already associated with employer {}", customer.getId(), employerId);
            throw new SameCustomerException("Customer " + customer.getId() + " is already associated with employer " + employerId);
        }

        employer.getCustomers().add(customer);
        customer.getEmployers().add(employer);
        employerDao.save(employer);
        log.info("Customer {} added to employer {}", customer.getId(), employerId);
    }

    @Override
    public void deleteCustomer(Long employerId, Long customerId) {
        Employer employer = employerDao.getById(employerId);
        if (employer == null) {
            throw new EmployerNotFoundException("Employer not found with id " + employerId);
        }

        Customer customer = customerDao.getById(customerId);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found with id " + customerId);
        }

        if (!employer.getCustomers().contains(customer)) {
            log.info("Customer {} is not associated with employer {}", customerId, employerId);
            throw new CustomerForEmployerNotFoundException("Customer " + customerId + " is not associated with employer " + employerId);
        }

        employer.getCustomers().remove(customer);
        customer.getEmployers().remove(employer);
        employerDao.save(employer);
        customerDao.save(customer);
        log.info("Customer {} removed from employer {}", customerId, employerId);
    }
}
