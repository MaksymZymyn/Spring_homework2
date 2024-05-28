--create tables
BEGIN;

DROP TABLE IF EXISTS accounts CASCADE;
DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS employers CASCADE;
DROP TABLE IF EXISTS customer_in_employers CASCADE;

-- Створення таблиці для сутності Customer
CREATE TABLE customers (
                           id SERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           email VARCHAR(255) NOT NULL UNIQUE,
                           age INTEGER NOT NULL
);

-- Створення таблиці для сутності Account
CREATE TABLE accounts (
                          id SERIAL PRIMARY KEY,
                          number UUID NOT NULL,
                          currency VARCHAR(255) NOT NULL,
                          balance DOUBLE PRECISION NOT NULL,
                          customer_id BIGINT NOT NULL,
                          FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Створення таблиці для сутності Employer
CREATE TABLE employers (
                           id SERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           address VARCHAR(255) NOT NULL UNIQUE
);

-- Створення таблиці-посередника для відношення багато-до-багатьох між Customers і Employers
CREATE TABLE customer_in_employers (
                                       customer_id BIGINT NOT NULL,
                                       employer_id BIGINT NOT NULL,
                                       PRIMARY KEY (customer_id, employer_id),
                                       FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
                                       FOREIGN KEY (employer_id) REFERENCES employers(id) ON DELETE CASCADE
);

COMMIT;
