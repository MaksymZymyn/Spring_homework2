package homework2.dao;

import homework2.domain.bank.Account;
import homework2.exceptions.AccountNotFoundException;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
@Slf4j
public class AccountDao implements Dao<Account> {
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Override
    public Account save(Account account) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            if (account.getId() == null) {
                entityManager.persist(account);
                log.info("New account saved: {}", account);
            } else {
                entityManager.merge(account);
                log.info("Account updated: {}", account);
            }
            entityTransaction.commit();
            return account;
        } catch (HibernateException e) {
            log.error("Error saving or updating account: {}", e.getMessage());
            if (entityTransaction != null) {
                entityTransaction.rollback();
            }
            throw e;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Override
    public boolean delete(Account account) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            entityManager.remove(entityManager.contains(account) ? account : entityManager.merge(account));
            log.info("Account deleted: {}", account);
            entityTransaction.commit();
            return true;
        } catch (HibernateException e) {
            log.error("Error with deleting account: {}", e.getMessage());
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
    public void deleteAll(List<Account> currentAccounts) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            for (Account account : currentAccounts) {
                entityManager.remove(entityManager.contains(account) ? account : entityManager.merge(account));
                log.info("Account deleted: {}", account);
            }
            entityTransaction.commit();
        } catch (HibernateException e) {
            log.error("Error with deleting accounts: {}", e.getMessage());
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
    public void saveAll(List<Account> currentAccounts) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            for (Account account : currentAccounts) {
                if (account.getId() == null) {
                    entityManager.persist(account);
                    log.info("New account saved: {}", account);
                } else {
                    entityManager.merge(account);
                    log.info("Account updated: {}", account);
                }
            }
            entityTransaction.commit();
        } catch (HibernateException e) {
            log.error("Error with saving accounts: {}", e.getMessage());
            if (entityTransaction != null) {
                entityTransaction.rollback();
            }
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public List<Account> findAll() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityGraph entityGraph = entityManager.createEntityGraph("accountWithCustomerAndCustomerEmployers");
            return entityManager.createQuery("SELECT a FROM Account a", Account.class)
                    .setHint("jakarta.persistence.fetchgraph", entityGraph)
                    .getResultList();
        } catch (HibernateException e) {
            log.error("Error with getting accounts", e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean deleteById(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();

            Account account = getById(id);
            if (account == null) {
                log.error("Account with {} not found " + id);
                throw new AccountNotFoundException("Account with {} not found " + id);
            }
            entityManager.remove(account);
            log.info("Deleted account with id {}", id);
            entityTransaction.commit();
            return true;
        } catch (HibernateException e) {
            log.error("Error with deleting account with id {}: {}", id, e.getMessage());
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
    public Account getById(Long id) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityGraph<?> entityGraph = entityManager.getEntityGraph("accountWithCustomerAndCustomerEmployers");

            Map<String, Object> hints = new HashMap<>();
            hints.put("jakarta.persistence.fetchgraph", entityGraph);

            return entityManager.find(Account.class, id, hints);
        } catch (HibernateException e) {
            log.error("Error with getting account by id: " + id, e);
            return null;
        }
    }

    public Account findByNumber(UUID accountNumber) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityGraph entityGraph = entityManager.getEntityGraph("accountWithCustomerAndCustomerEmployers");
            return entityManager.createQuery(
                            "SELECT a FROM Account a WHERE a.number = :accountNumber", Account.class)
                    .setParameter("accountNumber", accountNumber)
                    .setHint("javax.persistence.fetchgraph", entityGraph)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new AccountNotFoundException("No account found with number " + accountNumber);
        } catch (HibernateException e) {
            log.error("Error with getting account by number: " + accountNumber, e);
            return null;
        }
    }
}
