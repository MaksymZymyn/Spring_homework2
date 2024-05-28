package homework2.dao;

import homework2.domain.bank.Customer;
import homework2.domain.bank.Employer;
import homework2.exceptions.CustomerNotFoundException;
import homework2.exceptions.EmployerNotFoundException;
import homework2.exceptions.SameEmployerException;
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
public class EmployerDao implements Dao<Employer> {

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Override
    public Employer save(Employer employer) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            if (employer.getId() == null) {
                entityManager.persist(employer);
                log.info("New employer saved: {}", employer);
            } else {
                entityManager.merge(employer);
                log.info("Employer updated: {}", employer);
            }
            entityTransaction.commit();
            log.info("New employer saved: {}", employer);
            return employer;
        } catch (HibernateException e) {
            log.error("Error creating employer: {}", e.getMessage());
            if (entityTransaction != null) {
                entityTransaction.rollback();
            }
            return null;
        } catch (Exception e) {
            log.error("Error creating employer: {}", e.getMessage());
            if (entityTransaction != null) {
                entityTransaction.rollback();
            }
            throw new SameEmployerException("Employer with address " + employer.getAddress() + " already exists");
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Override
    public boolean delete(Employer employer) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            entityManager.remove(entityManager.contains(employer) ? employer : entityManager.merge(employer));
            log.info("Employer deleted: {}", employer);
            entityTransaction.commit();
            return true;
        } catch (HibernateException e) {
            log.error("Error with deleting employer: {}", e.getMessage());
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
    public void deleteAll(List<Employer> currentEmployers) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            for (Employer employer : currentEmployers) {
                entityManager.remove(entityManager.contains(employer) ? employer : entityManager.merge(employer));
                log.info("Employer deleted: {}", employer);
            }
            entityTransaction.commit();
        } catch (HibernateException e) {
            log.error("Error with deleting employers: {}", e.getMessage());
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
    public void saveAll(List<Employer> currentEmployers) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            for (Employer employer : currentEmployers) {
                if (employer.getId() == null) {
                    entityManager.persist(employer);
                    log.info("New employer saved: {}", employer);
                } else {
                    entityManager.merge(employer);
                    log.info("Employer updated: {}", employer);
                }
            }
            entityTransaction.commit();
        } catch (HibernateException e) {
            log.error("Error with saving employers: {}", e.getMessage());
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
    public List<Employer> findAll() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityGraph entityGraph = entityManager.getEntityGraph("employerWithCustomersAndAccountsAndOtherEmployers");
            return entityManager.createQuery("SELECT e FROM Employer e", Employer.class)
                    .setHint("jakarta.persistence.fetchgraph", entityGraph)
                    .getResultList();
        } catch (HibernateException e) {
            log.error("Error with getting employers", e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean deleteById(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();

            Employer employer = entityManager.find(Employer.class, id);
            if (employer == null) {
                log.error("Employer with id {} not found", id);
                throw new EmployerNotFoundException("Employer with id " + id + " not found");
            }

            if (!entityManager.contains(employer)) {
                employer = entityManager.merge(employer);
            }

            entityManager.remove(employer);
            log.info("Deleted employer with id {}", id);
            entityTransaction.commit();
            return true;
        } catch (HibernateException e) {
            log.error("Error with deleting employer with id {}: {}", id, e.getMessage());
            if (entityTransaction != null && entityTransaction.isActive()) {
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
    public Employer getById(Long id) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityGraph entityGraph = entityManager.createEntityGraph("employerWithCustomersAndAccountsAndOtherEmployers");

            Map<String, Object> hints = new HashMap<>();
            hints.put("jakarta.persistence.fetchgraph", entityGraph);

            log.info("Retrieving employer with id {}", id);
            Employer employer = entityManager.find(Employer.class, id, hints);
            if (employer == null) {
                log.error("Employer with {} not found " + id);
                throw new EmployerNotFoundException("Employer with {} not found " + id);
            }
            return employer;
        } catch (HibernateException e) {
            log.error("Error with getting employer by id: " + id, e);
            return null;
        }
    }

    public Employer getByAddress(String address) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityGraph graph = entityManager.createEntityGraph("employerWithCustomersAndAccountsAndOtherEmployers");

            return entityManager.createQuery(
                            "SELECT e FROM Employer e WHERE e.address = :address", Employer.class)
                    .setParameter("address", address)
                    .setHint("javax.persistence.fetchgraph", graph)
                    .getSingleResult();
        } catch (HibernateException e) {
            log.error("Error with getting employer by address {}: {}", address, e.getMessage());
            return null;
        } catch (Exception e) {
            throw new EmployerNotFoundException("No employer found by address " + address);
        }
    }
}
