--create tables
BEGIN;

-- Додавання компаній
INSERT INTO employers (name, address) VALUES ('Company A', 'Address A');
INSERT INTO employers (name, address) VALUES ('Company B', 'Address B');
INSERT INTO employers (name, address) VALUES ('Company C', 'Address C');

-- Додавання користувачів
INSERT INTO customers (name, email, age) VALUES ('John Doe', 'john@example.com', 30);
INSERT INTO customers (name, email, age) VALUES ('Jane Smith', 'jane@example.com', 25);
INSERT INTO customers (name, email, age) VALUES ('Michael Johnson', 'michael@example.com', 35);

-- Додавання рахунків для користувачів
INSERT INTO accounts (number, currency, balance, customer_id) VALUES (uuid_generate_v4(), 'USD', 1000.00, (SELECT id FROM customers WHERE name = 'John Doe'));
INSERT INTO accounts (number, currency, balance, customer_id) VALUES (uuid_generate_v4(), 'EUR', 500.00, (SELECT id FROM customers WHERE name = 'John Doe'));
INSERT INTO accounts (number, currency, balance, customer_id) VALUES (uuid_generate_v4(), 'USD', 2000.00, (SELECT id FROM customers WHERE name = 'Jane Smith'));
INSERT INTO accounts (number, currency, balance, customer_id) VALUES (uuid_generate_v4(), 'EUR', 1500.00, (SELECT id FROM customers WHERE name = 'Michael Johnson'));

-- Встановлення зв'язку між користувачами та компаніями
INSERT INTO customer_in_employers (customer_id, employer_id) VALUES ((SELECT id FROM customers WHERE name = 'John Doe'), (SELECT id FROM employers WHERE name = 'Company A'));
INSERT INTO customer_in_employers (customer_id, employer_id) VALUES ((SELECT id FROM customers WHERE name = 'John Doe'), (SELECT id FROM employers WHERE name = 'Company B'));
INSERT INTO customer_in_employers (customer_id, employer_id) VALUES ((SELECT id FROM customers WHERE name = 'Jane Smith'), (SELECT id FROM employers WHERE name = 'Company B'));
INSERT INTO customer_in_employers (customer_id, employer_id) VALUES ((SELECT id FROM customers WHERE name = 'Michael Johnson'), (SELECT id FROM employers WHERE name = 'Company C'));

--create indexes
CREATE INDEX idx_accounts_customer_id ON accounts(customer_id);
CREATE INDEX idx_customer_in_employers_customer_id ON customer_in_employers(customer_id);
CREATE INDEX idx_customer_in_employers_employer_id ON customer_in_employers(employer_id);

COMMIT;
