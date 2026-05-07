package project;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Signup extends JFrame {

    private JTextField nameField, deptField, emailField, usernameField;
    private JPasswordField passwordField;
    private JButton submitButton, backButton;

    // DATABASE CONNECTION
    private static final String DB_URL = "jdbc:mysql://localhost:3306/SalaryMan";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    public Signup() {
        setTitle("Salary Management System - Sign Up");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
        setVisible(true);
    }

    private void initializeUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("EMPLOYEE SIGN UP", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        nameField = new JTextField(15);
        deptField = new JTextField(15);
        emailField = new JTextField(15);
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        submitButton = new JButton("Register");
        submitButton.setBackground(new Color(0, 120, 215));
        submitButton.setForeground(Color.WHITE);

        backButton = new JButton("Back to Login");

        // Layout Components
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        panel.add(deptField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(submitButton, gbc);

        gbc.gridy++;
        panel.add(backButton, gbc);

        // Actions
        submitButton.addActionListener(e -> registerEmployee());
        backButton.addActionListener(e -> {
            dispose();
            new Login(null).setVisible(true);
        });

        add(panel);
    }

    private void registerEmployee() {
        String name = nameField.getText().trim();
        String dept = deptField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = String.valueOf(passwordField.getPassword());

        if (name.isEmpty() || dept.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
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
            stmt.setString(4, username);
            stmt.setString(5, password);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Registration Successful! You can now log in.");
                dispose();
                new Login(null).setVisible(true);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
