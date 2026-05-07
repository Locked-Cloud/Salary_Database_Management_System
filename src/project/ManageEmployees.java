package project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class ManageEmployees extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/SalaryMan";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    private DefaultTableModel tableModel;
    private JTable employeeTable;

    public ManageEmployees() {
        setTitle("Manage Employees");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // --- TOP: Form ---
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add / Delete Employee"));

        JTextField nameField = new JTextField();
        JTextField deptField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JTextField idField = new JTextField("Enter ID to Delete");

        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Department:"));
        formPanel.add(deptField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Username:"));
        formPanel.add(userField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passField);

        JButton addButton = new JButton("Add Employee");
        JButton deleteButton = new JButton("Delete Employee");

        JPanel actionPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        actionPanel.add(addButton);
        actionPanel.add(new JLabel("")); // Spacer
        actionPanel.add(idField);
        actionPanel.add(deleteButton);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(formPanel, BorderLayout.CENTER);
        topContainer.add(actionPanel, BorderLayout.SOUTH);

        // --- CENTER: Table ---
        tableModel = new DefaultTableModel();
        employeeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Employees"));

        mainPanel.add(topContainer, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);

        // --- Actions ---
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String dept = deptField.getText().trim();
            String email = emailField.getText().trim();
            String user = userField.getText().trim();
            String pass = String.valueOf(passField.getPassword()).trim();

            if (name.isEmpty() || dept.isEmpty() || email.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "INSERT INTO Employees (full_name, department, email, username, password) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                    PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.setString(2, dept);
                stmt.setString(3, email);
                stmt.setString(4, user);
                stmt.setString(5, pass);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Employee Added!");

                nameField.setText("");
                deptField.setText("");
                emailField.setText("");
                userField.setText("");
                passField.setText("");

                loadEmployeeData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        deleteButton.addActionListener(e -> {
            String idText = idField.getText().trim();
            if (idText.isEmpty() || idText.equals("Enter ID to Delete")) {
                JOptionPane.showMessageDialog(this, "Please enter an Employee ID to delete.", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int empId = Integer.parseInt(idText);
                String sql = "DELETE FROM Employees WHERE employee_id=?";
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                        PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, empId);
                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Employee Deleted!");
                        idField.setText("");
                        loadEmployeeData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Employee ID not found.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Employee ID must be a valid number.", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Load data on start
        loadEmployeeData();
    }

    private void loadEmployeeData() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt
                        .executeQuery("SELECT employee_id, full_name, department, email, username FROM Employees")) {

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
