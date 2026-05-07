package project;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/SalaryMan";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    public AdminDashboard() {
        setTitle("Admin Dashboard - Analytics & Menu");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- ANALYTICS PANEL ---
        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Live System Analytics"));
        statsPanel.setBackground(new Color(240, 248, 255)); // Light Blue background

        JLabel totalEmpLabel = new JLabel("Total Employees: Loading...");
        JLabel totalPayrollLabel = new JLabel("Total Payroll Dispensed: Loading...");
        JLabel totalBonusesLabel = new JLabel("Total Bonuses Granted: Loading...");

        totalEmpLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalPayrollLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalBonusesLabel.setFont(new Font("Arial", Font.BOLD, 14));

        statsPanel.add(totalEmpLabel);
        statsPanel.add(totalPayrollLabel);
        statsPanel.add(totalBonusesLabel);

        // Fetch Live Data
        fetchAnalytics(totalEmpLabel, totalPayrollLabel, totalBonusesLabel);

        // --- MENU PANEL ---
        JPanel menuPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        
        JLabel titleLabel = new JLabel("ADMINISTRATION MENU", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        menuPanel.add(titleLabel);

        JButton empBtn = new JButton("1. Manage Employees");
        JButton salaryBtn = new JButton("2. Salary Structures");
        JButton payrollBtn = new JButton("3. Process Payroll");
        JButton bonusBtn = new JButton("4. Bonuses & Deductions");
        JButton reportBtn = new JButton("5. Reports & Queries");

        menuPanel.add(empBtn);
        menuPanel.add(salaryBtn);
        menuPanel.add(payrollBtn);
        menuPanel.add(bonusBtn);
        menuPanel.add(reportBtn);

        empBtn.addActionListener(e -> { new ManageEmployees().setVisible(true); fetchAnalytics(totalEmpLabel, totalPayrollLabel, totalBonusesLabel); });
        salaryBtn.addActionListener(e -> new ManageSalary().setVisible(true));
        payrollBtn.addActionListener(e -> { new ManagePayroll().setVisible(true); fetchAnalytics(totalEmpLabel, totalPayrollLabel, totalBonusesLabel); });
        bonusBtn.addActionListener(e -> { new ManageBonuses().setVisible(true); fetchAnalytics(totalEmpLabel, totalPayrollLabel, totalBonusesLabel); });
        reportBtn.addActionListener(e -> new ReportsDashboard().setVisible(true));

        mainPanel.add(statsPanel, BorderLayout.NORTH);
        mainPanel.add(menuPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private void fetchAnalytics(JLabel empLabel, JLabel payrollLabel, JLabel bonusLabel) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Total Employees
            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) AS total FROM Employees");
            if (rs1.next()) empLabel.setText("Total Employees: " + rs1.getInt("total"));

            // Total Payroll Dispensed
            ResultSet rs2 = stmt.executeQuery("SELECT SUM(net_salary) AS total_payroll FROM Payroll");
            if (rs2.next()) {
                double total = rs2.getDouble("total_payroll");
                payrollLabel.setText(String.format("Total Payroll Dispensed: $%.2f", total));
            }

            // Total Bonuses
            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) AS total_bonuses FROM Bonuses");
            if (rs3.next()) bonusLabel.setText("Total Bonuses Granted: " + rs3.getInt("total_bonuses"));

        } catch (SQLException ex) {
            empLabel.setText("Total Employees: Error");
            payrollLabel.setText("Total Payroll Dispensed: Error");
            bonusLabel.setText("Total Bonuses Granted: Error");
        }
    }
}
