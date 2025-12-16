-- Create database with proper charset
CREATE DATABASE IF NOT EXISTS microservices_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;
USE microservices_db;

-- Persons table
CREATE TABLE persons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    gender VARCHAR(20) NOT NULL,
    identification VARCHAR(50) UNIQUE NOT NULL,
    address VARCHAR(500),
    phone VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Customers table
CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    person_id BIGINT NOT NULL,
    password VARCHAR(255) NOT NULL,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer_person FOREIGN KEY (person_id) REFERENCES persons(id) ON DELETE CASCADE,
    INDEX idx_customer_person (person_id),
    INDEX idx_customer_status (status)
);

-- Accounts table
CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(50) UNIQUE NOT NULL,
    type VARCHAR(20) NOT NULL,
    initial_balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    current_balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    customer_id BIGINT NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_account_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE RESTRICT,
    INDEX idx_account_customer (customer_id),
    INDEX idx_account_status (status)
);

-- Movements table
CREATE TABLE movements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    type VARCHAR(20) NOT NULL,
    value DECIMAL(19,2) NOT NULL CHECK (value > 0),
    balance DECIMAL(19,2) NOT NULL,
    description VARCHAR(500),
    CONSTRAINT fk_movement_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE RESTRICT,
    INDEX idx_movement_account (account_id),
    INDEX idx_movement_date (date)
);

-- Sample data for testing (as per requirements)
INSERT INTO persons (name, gender, identification, address, phone) VALUES
('Jose Lema', 'MALE', '1234567890', 'Otavalo sn y principal', '098254785'),
('Marianela Montalvo', 'FEMALE', '0987654321', 'Amazonas y NNUU', '097548965'),
('Juan Osorio', 'MALE', '1122334455', '13 junio y Equinoccial', '098874587');

INSERT INTO customers (person_id, password, status) VALUES
(1, '$2a$10$N9qo8uLOickgx2ZMRZoMye', TRUE),
(2, '$2a$10$N9qo8uLOickgx2ZMRZoMye', TRUE),
(3, '$2a$10$N9qo8uLOickgx2ZMRZoMye', TRUE);

INSERT INTO accounts (number, type, initial_balance, current_balance, status, customer_id) VALUES
('478758', 'Ahorro', 2000.00, 2000.00, TRUE, 1),
('225487', 'Corriente', 100.00, 100.00, TRUE, 2),
('495878', 'Ahorros', 0.00, 0.00, TRUE, 3),
('496825', 'Ahorros', 540.00, 540.00, TRUE, 2),
('585545', 'Corriente', 1000.00, 1000.00, TRUE, 1);

