CREATE DATABASE IF NOT EXISTS SalaryMan;
USE SalaryMan;

-- Admin Table (From your Login.java logic)
CREATE TABLE IF NOT EXISTS Admins (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Employees Table (Added username and password for login)
CREATE TABLE IF NOT EXISTS Employees (
    employee_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    department VARCHAR(50),
    email VARCHAR(100) UNIQUE,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
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

-- Insert Dummy Data for Testing Login (Passwords are hashed 'admin123' and 'emp123')
INSERT INTO Admins (username, password) VALUES ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9');
INSERT INTO Employees (full_name, department, email, username, password) VALUES ('John Doe', 'IT', 'john@test.com', 'john', '33e387c9dc00ff51ec946b8ba268c17fc80e92fdd77dfed98c4d293881458e08');
INSERT INTO Employees (full_name, department, email, username, password) VALUES ('Jane Smith', 'HR', 'jane@test.com', 'jane', '33e387c9dc00ff51ec946b8ba268c17fc80e92fdd77dfed98c4d293881458e08');

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

-- ==========================================
-- DATABASE OPTIMIZATIONS (INDEXES & VIEWS)
-- ==========================================

-- INDEXES
CREATE INDEX idx_emp_username ON Employees(username);
CREATE INDEX idx_admin_username ON Admins(username);
CREATE INDEX idx_fk_salary_emp ON SalaryStructures(employee_id);
CREATE INDEX idx_fk_payroll_emp ON Payroll(employee_id);
CREATE INDEX idx_fk_bonus_emp ON Bonuses(employee_id);
CREATE INDEX idx_payroll_date ON Payroll(payroll_date);
CREATE INDEX idx_base_salary ON SalaryStructures(base_salary);

-- VIEWS

-- 1. vw_Employee_Salaries
CREATE OR REPLACE VIEW vw_Employee_Salaries AS
SELECT e.employee_id, e.full_name, e.department, s.base_salary, s.tax_bracket, s.tax_amount, s.net_after_tax
FROM Employees e
JOIN SalaryStructures s ON e.employee_id = s.employee_id;

-- 2. vw_Employee_Bonus_Count
CREATE OR REPLACE VIEW vw_Employee_Bonus_Count AS
SELECT e.employee_id, e.full_name, COUNT(b.bonus_id) AS bonus_count
FROM Employees e
LEFT JOIN Bonuses b ON e.employee_id = b.employee_id
GROUP BY e.employee_id, e.full_name;

-- 3. vw_Comprehensive_Compensation
CREATE OR REPLACE VIEW vw_Comprehensive_Compensation AS
SELECT e.employee_id, e.full_name, d.deduction_type, d.deduction_amount, b.bonus_amount, b.reason
FROM Employees e
LEFT JOIN Deductions d ON e.employee_id = d.employee_id
LEFT JOIN Bonuses b ON e.employee_id = b.employee_id;

-- 4. vw_Employee_Classifications
CREATE OR REPLACE VIEW vw_Employee_Classifications AS
SELECT e.employee_id, e.full_name, s.base_salary, 
CASE 
  WHEN s.base_salary >= 80000 THEN 'Senior' 
  WHEN s.base_salary >= 40000 THEN 'Mid-Level' 
  ELSE 'Junior' 
END AS Employee_Level
FROM Employees e
JOIN SalaryStructures s ON e.employee_id = s.employee_id;

-- 5. vw_March_Payroll
CREATE OR REPLACE VIEW vw_March_Payroll AS
SELECT * FROM Payroll WHERE MONTH(payroll_date) = 3;

-- 6. vw_High_Salary_Employees
CREATE OR REPLACE VIEW vw_High_Salary_Employees AS
SELECT e.full_name, e.department, s.base_salary 
FROM Employees e 
JOIN SalaryStructures s ON e.employee_id = s.employee_id 
WHERE s.base_salary > 60000;

-- 7. vw_High_Bonus_Employees
CREATE OR REPLACE VIEW vw_High_Bonus_Employees AS
SELECT e.full_name, b.bonus_amount, b.reason 
FROM Employees e 
JOIN Bonuses b ON e.employee_id = b.employee_id 
WHERE b.bonus_amount > 500;

-- 8. vw_Compensation_History_2025
CREATE OR REPLACE VIEW vw_Compensation_History_2025 AS
SELECT e.full_name, c.salary_amount, c.change_type, c.effective_date 
FROM Employees e 
JOIN CompensationHistory c ON e.employee_id = c.employee_id 
WHERE YEAR(c.effective_date) = 2025;

-- 9. vw_Above_Average_Salary
CREATE OR REPLACE VIEW vw_Above_Average_Salary AS
SELECT e.full_name, s.base_salary 
FROM Employees e 
JOIN SalaryStructures s ON e.employee_id = s.employee_id 
WHERE s.base_salary > (SELECT AVG(base_salary) FROM SalaryStructures);

-- 10. vw_Above_Average_Bonus
CREATE OR REPLACE VIEW vw_Above_Average_Bonus AS
SELECT e.full_name, b.bonus_amount 
FROM Employees e 
JOIN Bonuses b ON e.employee_id = b.employee_id 
WHERE b.bonus_amount > (SELECT AVG(bonus_amount) FROM Bonuses);

-- 11. vw_Admin_Analytics (For AdminDashboard live stats)
CREATE OR REPLACE VIEW vw_Admin_Analytics AS
SELECT 
  (SELECT COUNT(*) FROM Employees) AS total_employees,
  (SELECT SUM(net_salary) FROM Payroll) AS total_payroll,
  (SELECT COUNT(*) FROM Bonuses) AS total_bonuses;

-- 12. vw_Payroll_Details (For ManagePayroll table)
CREATE OR REPLACE VIEW vw_Payroll_Details AS
SELECT p.payroll_id, e.employee_id, e.full_name, p.net_salary, p.payroll_date
FROM Payroll p
JOIN Employees e ON p.employee_id = e.employee_id;

-- 13. vw_Bonus_Details (For ManageBonuses table)
CREATE OR REPLACE VIEW vw_Bonus_Details AS
SELECT b.bonus_id, e.employee_id, e.full_name, b.bonus_amount, b.bonus_date, b.reason
FROM Bonuses b
JOIN Employees e ON b.employee_id = e.employee_id;
