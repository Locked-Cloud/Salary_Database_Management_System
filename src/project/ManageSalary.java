package project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class ManageSalary extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/SalaryMan";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    private DefaultTableModel tableModel;
    private JTable salaryTable;

    public ManageSalary() {
        setTitle("Salary Structures");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // --- TOP: Form ---
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Set Salary Structure"));

        JTextField empIdField = new JTextField();
        JTextField baseSalaryField = new JTextField();
        JTextField payGradeField = new JTextField();
        JTextField taxBracketField = new JTextField(); 

        formPanel.add(new JLabel("Employee ID:")); formPanel.add(empIdField);
        formPanel.add(new JLabel("Base Salary ($):")); formPanel.add(baseSalaryField);
        formPanel.add(new JLabel("Pay Grade (A, B):")); formPanel.add(payGradeField);
        formPanel.add(new JLabel("Tax Bracket (%):")); formPanel.add(taxBracketField);

        JButton saveBtn = new JButton("Save Salary Structure");
        
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(formPanel, BorderLayout.CENTER);
        topContainer.add(saveBtn, BorderLayout.SOUTH);

        // --- CENTER: Table ---
        tableModel = new DefaultTableModel();
        salaryTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(salaryTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Salary Structures"));

        mainPanel.add(topContainer, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);

        // --- Actions ---
        saveBtn.addActionListener(e -> {
            String empIdStr = empIdField.getText().trim();
            String baseStr = baseSalaryField.getText().trim();
            String grade = payGradeField.getText().trim();
            String taxStr = taxBracketField.getText().trim();

            if (empIdStr.isEmpty() || baseStr.isEmpty() || grade.isEmpty() || taxStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int empId = Integer.parseInt(empIdStr);
                double base = Double.parseDouble(baseStr);
                double taxP = Double.parseDouble(taxStr);
                
                double taxAmount = base * (taxP / 100.0);
                double net = base - taxAmount;

                String sql = "INSERT INTO SalaryStructures (employee_id, base_salary, pay_grade, tax_bracket, tax_amount, net_after_tax) VALUES (?, ?, ?, ?, ?, ?)";
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, empId);
                    stmt.setDouble(2, base);
                    stmt.setString(3, grade);
                    stmt.setDouble(4, taxP);
                    stmt.setDouble(5, taxAmount);
                    stmt.setDouble(6, net);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Salary Structure Saved! Net Salary: $" + net);
                    
                    empIdField.setText("");
                    baseSalaryField.setText("");
                    payGradeField.setText("");
                    taxBracketField.setText("");
                    
                    loadSalaryData();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Employee ID, Base Salary, and Tax Bracket must be valid numbers.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loadSalaryData();
    }

    private void loadSalaryData() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM SalaryStructures")) {

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
            ex.printStackTrace();
        }
    }
}
