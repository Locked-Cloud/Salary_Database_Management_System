CREATE DATABASE IF NOT EXISTS SalaryMan;
USE SalaryMan;

-- Admin Table (From your Login.java logic)
CREATE TABLE IF NOT EXISTS Admins (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL
);

-- Employees Table (Added username and password for login)
CREATE TABLE IF NOT EXISTS Employees (
    employee_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    department VARCHAR(50),
    email VARCHAR(100) UNIQUE,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL
);

-- SalaryStructures
CREATE TABLE IF NOT EXISTS SalaryStructures (
    structure_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT,
    base_salary DECIMAL(10, 2),
    pay_grade VARCHAR(20),
    tax_bracket DECIMAL(5, 2),
    tax_amount DECIMAL(10, 2),
    net_after_tax DECIMAL(10, 2),
    FOREIGN KEY (employee_id) REFERENCES Employees(employee_id)
);

-- Payroll
CREATE TABLE IF NOT EXISTS Payroll (
    payroll_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT,
    net_salary DECIMAL(10, 2),
    payroll_date DATE,
    FOREIGN KEY (employee_id) REFERENCES Employees(employee_id)
);

-- Deductions
CREATE TABLE IF NOT EXISTS Deductions (
    deduction_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT,
    deduction_type VARCHAR(50),
    deduction_amount DECIMAL(10, 2),
    FOREIGN KEY (employee_id) REFERENCES Employees(employee_id)
);

-- Bonuses
CREATE TABLE IF NOT EXISTS Bonuses (
    bonus_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT,
    bonus_amount DECIMAL(10, 2),
    bonus_date DATE,
    reason VARCHAR(255),
    FOREIGN KEY (employee_id) REFERENCES Employees(employee_id)
);

-- CompensationHistory
CREATE TABLE IF NOT EXISTS CompensationHistory (
    history_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT,
    salary_amount DECIMAL(10, 2),
    change_type VARCHAR(50),
    effective_date DATE,
    FOREIGN KEY (employee_id) REFERENCES Employees(employee_id)
);

-- Insert Dummy Data for Testing Login
INSERT INTO Admins (username, password) VALUES ('admin', 'admin123');
INSERT INTO Employees (full_name, department, email, username, password) VALUES ('John Doe', 'IT', 'john@test.com', 'john', 'emp123');
INSERT INTO Employees (full_name, department, email, username, password) VALUES ('Jane Smith', 'HR', 'jane@test.com', 'jane', 'emp123');

-- Insert Dummy Data for Queries
INSERT INTO SalaryStructures (employee_id, base_salary, pay_grade, tax_bracket, tax_amount, net_after_tax) VALUES 
(1, 85000, 'A', 20, 17000, 68000),
(2, 45000, 'B', 10, 4500, 40500);

INSERT INTO Payroll (employee_id, net_salary, payroll_date) VALUES 
(1, 68000, '2025-03-01'),
(2, 40500, '2025-03-01');

INSERT INTO Bonuses (employee_id, bonus_amount, bonus_date, reason) VALUES 
(1, 1000, '2025-01-15', 'Performance'),
(2, 600, '2025-02-20', 'Project Completion');

INSERT INTO Deductions (employee_id, deduction_type, deduction_amount) VALUES 
(1, 'Health Insurance', 200),
(2, 'Health Insurance', 200);

INSERT INTO CompensationHistory (employee_id, salary_amount, change_type, effective_date) VALUES 
(1, 80000, 'Initial Hire', '2024-01-01'),
(1, 85000, 'Annual Raise', '2025-01-01'),
(2, 45000, 'Initial Hire', '2025-02-01');
