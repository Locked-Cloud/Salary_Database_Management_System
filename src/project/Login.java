package project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class Login extends JDialog {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JPanel loginPanel;

    // DATABASE CONNECTION
    private static final String DB_URL = "jdbc:mysql://localhost:3306/salaryman";

    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    public Login(JFrame parent) {

        super(parent, true);

        setTitle("Salary Management System - Login");
        setSize(500, 350);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initializeComponents();

        setVisible(true);
    }

    private void initializeComponents() {

        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = new JLabel("LOGIN SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        loginButton = createModernButton("Login");
        signupButton = createModernButton("Sign Up");

        gbc.insets = new Insets(10, 10, 10, 10);

        // TITLE
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        // USERNAME LABEL
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        loginPanel.add(new JLabel("Username:"), gbc);

        // USERNAME FIELD
        gbc.gridx = 1;
        loginPanel.add(usernameField, gbc);

        // PASSWORD LABEL
        gbc.gridx = 0;
        gbc.gridy++;
        loginPanel.add(new JLabel("Password:"), gbc);

        // PASSWORD FIELD
        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);

        // LOGIN BUTTON
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(loginButton, gbc);

        // SIGNUP BUTTON
        gbc.gridy++;
        loginPanel.add(signupButton, gbc);

        // ACTIONS
        loginButton.addActionListener(this::loginAction);

        signupButton.addActionListener(e -> {

            dispose();

            new Signup();
        });

        setContentPane(loginPanel);
    }

    // LOGIN ACTION
    private void loginAction(ActionEvent e) {

        String username = usernameField.getText().trim();
        String password = String.valueOf(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password");

            return;
        }

        // ADMIN LOGIN
        if (authenticateAdmin(username, password)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Admin Login Successful");

            dispose();

            new AdminDashboard();

        }

        // EMPLOYEE LOGIN
        else {
            int empId = authenticateEmployee(username, password);
            if (empId != -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Employee Login Successful");
                dispose();
                new EmployeeDashboard(empId);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid Username or Password");
            }
        }
    }

    // ADMIN AUTHENTICATION
    private boolean authenticateAdmin(
            String username,
            String password) {

        String sql = "SELECT * FROM admins WHERE username=? AND password=?";

        try (
                Connection conn = DriverManager.getConnection(
                        DB_URL,
                        DB_USERNAME,
                        DB_PASSWORD);

                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (SQLException ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Database Error:\n" + ex.getMessage());
        }

        return false;
    }

    // EMPLOYEE AUTHENTICATION
    private int authenticateEmployee(
            String username,
            String password) {

        String sql = "SELECT employee_id FROM Employees WHERE username=? AND password=?";

        try (
                Connection conn = DriverManager.getConnection(
                        DB_URL,
                        DB_USERNAME,
                        DB_PASSWORD);

                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("employee_id");
            }

        } catch (SQLException ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Database Error:\n" + ex.getMessage());
        }

        return -1;
    }

    // BUTTON DESIGN
    private JButton createModernButton(String text) {

        JButton button = new JButton(text);

        button.setBackground(new Color(0, 120, 215));
        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);

        button.setFont(
                new Font("Arial", Font.BOLD, 14));

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    // MAIN
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new Login(null);

        });
    }
}