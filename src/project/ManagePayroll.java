package project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class ManagePayroll extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/SalaryMan";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    private DefaultTableModel tableModel;
    private JTable payrollTable;

    public ManagePayroll() {
        setTitle("Process Payroll");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // --- TOP: Form ---
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Process Payroll"));

        JTextField empIdField = new JTextField();
        JTextField netSalaryField = new JTextField();
        JTextField dateField = new JTextField("YYYY-MM-DD");

        formPanel.add(new JLabel("Employee ID:")); formPanel.add(empIdField);
        formPanel.add(new JLabel("Net Salary ($):")); formPanel.add(netSalaryField);
        formPanel.add(new JLabel("Date:")); formPanel.add(dateField);

        JButton payBtn = new JButton("Add Payroll Record");

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(formPanel, BorderLayout.CENTER);
        topContainer.add(payBtn, BorderLayout.SOUTH);

        // --- CENTER: Table ---
        tableModel = new DefaultTableModel();
        payrollTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(payrollTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Payroll History"));

        mainPanel.add(topContainer, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);

        // --- Actions ---
        payBtn.addActionListener(e -> {
            String empIdStr = empIdField.getText().trim();
            String netStr = netSalaryField.getText().trim();
            String dateStr = dateField.getText().trim();

            if (empIdStr.isEmpty() || netStr.isEmpty() || dateStr.isEmpty() || dateStr.equals("YYYY-MM-DD")) {
                JOptionPane.showMessageDialog(this, "All fields are required and need valid data!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int empId = Integer.parseInt(empIdStr);
                double net = Double.parseDouble(netStr);

                String sql = "INSERT INTO Payroll (employee_id, net_salary, payroll_date) VALUES (?, ?, ?)";
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, empId);
                    stmt.setDouble(2, net);
                    stmt.setString(3, dateStr);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Payroll Processed!");
                    
                    empIdField.setText("");
                    netSalaryField.setText("");
                    dateField.setText("YYYY-MM-DD");
                    
                    loadPayrollData();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Employee ID and Net Salary must be valid numbers.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loadPayrollData();
    }

    private void loadPayrollData() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Payroll")) {

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
