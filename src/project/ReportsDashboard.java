package project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class ReportsDashboard extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/SalaryMan";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    public ReportsDashboard() {
        setTitle("Reports & Queries");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel();

        String[] queries = {
            "1. List all employees along with their base salary",
            "2. Retrieve all payroll records for the month of March",
            "3. Count the number of bonuses granted for each employee",
            "4. List all employees with a salary > $60,000 with department",
            "5. Retrieve all employees along with deductions and bonus details",
            "6. List all employees who received a bonus > $500 with reason",
            "7. Show salary structures with tax info for each employee",
            "8. List employees with compensation history for 2025",
            "9. List employees with salary higher than company average",
            "10. Find employees with bonus greater than average bonus",
            "11. Classify employees (Senior, Mid-Level, Junior)"
        };

        JComboBox<String> querySelector = new JComboBox<>(queries);
        JButton runQueryButton = new JButton("Run Query");

        topPanel.add(new JLabel("Select Report: "));
        topPanel.add(querySelector);
        topPanel.add(runQueryButton);

        DefaultTableModel tableModel = new DefaultTableModel();
        JTable resultsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultsTable);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        runQueryButton.addActionListener(e -> {
            int index = querySelector.getSelectedIndex();
            String sql = "";
            switch (index) {
                case 0: sql = "SELECT e.full_name, s.base_salary FROM Employees e JOIN SalaryStructures s ON e.employee_id = s.employee_id"; break;
                case 1: sql = "SELECT * FROM Payroll WHERE MONTH(payroll_date) = 3"; break;
                case 2: sql = "SELECT e.employee_id, e.full_name, COUNT(b.bonus_id) AS bonus_count FROM Employees e LEFT JOIN Bonuses b ON e.employee_id = b.employee_id GROUP BY e.employee_id, e.full_name"; break;
                case 3: sql = "SELECT e.full_name, e.department, s.base_salary FROM Employees e JOIN SalaryStructures s ON e.employee_id = s.employee_id WHERE s.base_salary > 60000"; break;
                case 4: sql = "SELECT e.full_name, d.deduction_type, d.deduction_amount, b.bonus_amount, b.reason FROM Employees e LEFT JOIN Deductions d ON e.employee_id = d.employee_id LEFT JOIN Bonuses b ON e.employee_id = b.employee_id"; break;
                case 5: sql = "SELECT e.full_name, b.bonus_amount, b.reason FROM Employees e JOIN Bonuses b ON e.employee_id = b.employee_id WHERE b.bonus_amount > 500"; break;
                case 6: sql = "SELECT e.full_name, s.base_salary, s.tax_bracket, s.tax_amount, s.net_after_tax FROM Employees e JOIN SalaryStructures s ON e.employee_id = s.employee_id"; break;
                case 7: sql = "SELECT e.full_name, c.salary_amount, c.change_type, c.effective_date FROM Employees e JOIN CompensationHistory c ON e.employee_id = c.employee_id WHERE YEAR(c.effective_date) = 2025"; break;
                case 8: sql = "SELECT e.full_name, s.base_salary FROM Employees e JOIN SalaryStructures s ON e.employee_id = s.employee_id WHERE s.base_salary > (SELECT AVG(base_salary) FROM SalaryStructures)"; break;
                case 9: sql = "SELECT e.full_name, b.bonus_amount FROM Employees e JOIN Bonuses b ON e.employee_id = b.employee_id WHERE b.bonus_amount > (SELECT AVG(bonus_amount) FROM Bonuses)"; break;
                case 10: sql = "SELECT e.full_name, s.base_salary, CASE WHEN s.base_salary >= 80000 THEN 'Senior' WHEN s.base_salary >= 40000 THEN 'Mid-Level' ELSE 'Junior' END AS Employee_Level FROM Employees e JOIN SalaryStructures s ON e.employee_id = s.employee_id"; break;
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                tableModel.setRowCount(0);
                tableModel.setColumnCount(0);

                Vector<String> columnNames = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    columnNames.add(metaData.getColumnName(i));
                }
                tableModel.setColumnIdentifiers(columnNames);

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.add(rs.getObject(i));
                    }
                    tableModel.addRow(row);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(panel);
    }
}
