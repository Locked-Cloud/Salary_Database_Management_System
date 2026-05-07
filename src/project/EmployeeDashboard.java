package project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class EmployeeDashboard extends JFrame {

    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private int loggedInEmpId;

    // DATABASE CONNECTION
    private static final String DB_URL = "jdbc:mysql://localhost:3306/SalaryMan";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    public EmployeeDashboard(int empId) {
        this.loggedInEmpId = empId;
        setTitle("Employee Dashboard - ID: " + empId);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
        setVisible(true);
    }

    private void initializeUI() {
        JPanel topPanel = new JPanel();
        
        JButton salaryBtn = new JButton("My Salary Info");
        JButton payrollBtn = new JButton("My Payroll History");

        topPanel.add(salaryBtn);
        topPanel.add(payrollBtn);

        tableModel = new DefaultTableModel();
        resultsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultsTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        salaryBtn.addActionListener(e -> fetchEmployeeData("SELECT * FROM SalaryStructures WHERE employee_id = ?"));
        payrollBtn.addActionListener(e -> fetchEmployeeData("SELECT * FROM Payroll WHERE employee_id = ?"));
    }

    private void fetchEmployeeData(String query) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, this.loggedInEmpId);
            ResultSet rs = stmt.executeQuery();
            
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
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
