package homework2.dao;

import homework2.domain.bank.Customer;
import homework2.domain.bank.Employer;
import homework2.exceptions.CustomerNotFoundException;
import homework2.exceptions.EmployerNotFoundException;
import homework2.exceptions.SameCustomerException;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class CustomerDao implements Dao<Customer> {

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Override
    public Customer save(Customer customer) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            if (customer.getEmail() == null) {
                entityManager.persist(customer);
                log.info("New customer saved: {}", customer);
            } else {
                entityManager.merge(customer);
                log.info("Customer updated: {}", customer);
            }
            entityTransaction.commit();
            log.info("New customer saved: {}", customer);
            return customer;
        } catch (Exception e) {
            log.error("Error creating customer: {}", e.getMessage());
            if (entityTransaction != null) {
                entityTransaction.rollback();
            }
            return null;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Override
    public boolean delete(Customer customer) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            entityManager.remove(entityManager.contains(customer) ? customer : entityManager.merge(customer));
            log.info("Customer deleted: {}", customer);
            entityTransaction.commit();
            return true;
        } catch (HibernateException e) {
            log.error("Error with deleting customer: {}", e.getMessage());
            if (entityTransaction != null) {
                entityTransaction.rollback();
            }
            return false;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Override
    public void deleteAll(List<Customer> currentCustomers) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            for (Customer customer : currentCustomers) {
                entityManager.remove(entityManager.contains(customer) ? customer : entityManager.merge(customer));
                log.info("Customer deleted: {}", customer);
            }
            entityTransaction.commit();
        } catch (HibernateException e) {
            log.error("Error with deleting customers: {}", e.getMessage());
            if (entityTransaction != null) {
                entityTransaction.rollback();
            }
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Override
    public void saveAll(List<Customer> currentCustomers) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            for (Customer customer : currentCustomers) {
                if (customer.getId() == null) {
                    entityManager.persist(customer);
                    log.info("New customer saved: {}", customer);
                } else {
                    entityManager.merge(customer);
                    log.info("Customer updated: {}", customer);
                }
            }
            entityTransaction.commit();
        } catch (HibernateException e) {
            log.error("Error with saving customers: {}", e.getMessage());
            if (entityTransaction != null) {
                entityTransaction.rollback();
            }
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Override
    public List<Customer> findAll() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityGraph entityGraph = entityManager.getEntityGraph("customerWithEmployersAndAccounts");
            return entityManager.createQuery("SELECT c FROM Customer c")
                    .setHint("jakarta.persistence.fetchgraph", entityGraph)
                    .getResultList();
        } catch (HibernateException e) {
            log.error("Error with getting customers", e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean deleteById(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();

            Customer customer = entityManager.find(Customer.class, id);
            if (customer == null) {
                log.error("Customer with {} not found " + id);
                throw new CustomerNotFoundException("Customer with {} not found " + id);
            }
            if (!entityManager.contains(customer)) {
                customer = entityManager.merge(customer);
            }
            entityManager.remove(customer);
            log.info("Deleted customer with id {}", id);
            entityTransaction.commit();
            return true;

        } catch (HibernateException e) {
            log.error("Error with deleting customer with id {}: {}", id, e.getMessage());
            if (entityTransaction != null) {
                entityTransaction.rollback();
            }
            return false;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Override
    public Customer getById(Long id) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityGraph graph = entityManager.createEntityGraph("customerWithEmployersAndAccounts");

            Map<String, Object> hints = new HashMap<>();
            hints.put("javax.persistence.fetchgraph", graph);
            Customer customer = entityManager.find(Customer.class, id, hints);
            if (customer == null) {
                log.error("Customer with {} not found " + id);
                throw new CustomerNotFoundException("Customer with {} not found " + id);
            }
            return customer;
        } catch (HibernateException e) {
            log.error("Error with getting customer by id: " + id, e);
            return null;
        }
    }

    public Customer getByEmail(String email) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityGraph graph = entityManager.createEntityGraph("customerWithEmployersAndAccounts");

            return entityManager.createQuery(
                            "SELECT c FROM Customer c WHERE c.email = :email", Customer.class)
                    .setParameter("email", email)
                    .setHint("javax.persistence.fetchgraph", graph)
                    .getSingleResult();
        } catch (Exception e) {
            log.error("Customer not found with email {}", email, e.getMessage());
            return null;
        }
    }

    public List<Customer> findByEmployerId(Long employerId) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            TypedQuery<Customer> query = entityManager.createQuery(
                    "SELECT c FROM Customer c JOIN c.employers e WHERE e.id = :employerId", Customer.class);
            query.setParameter("employerId", employerId);
            List<Customer> customers = query.getResultList();
            if (customers.isEmpty()) {
                log.info("No customers found for customer with id {}", employerId);
            }
            log.info("Customers found for customer with id {}", employerId);
            return customers;
        } catch (HibernateException e) {
            log.error("Error with finding customers by employer id {}: {}", employerId, e.getMessage());
            return new ArrayList<>();
        }
    }
}
