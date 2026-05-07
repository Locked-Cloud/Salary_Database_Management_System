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
    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/SalaryMan";

    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    public Login(JFrame parent) {

        super(parent);

        setTitle("Salary Management System - Login");
        setSize(500, 350);
        setMinimumSize(new Dimension(500, 350));
        setLocationRelativeTo(parent);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initializeComponents();

        setVisible(true);
    }

    private void initializeComponents() {

        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = new JLabel("LOGIN SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        loginButton = createModernButton("Login");
        signupButton = createModernButton("Sign Up");

        // TITLE
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10,10,20,10);

        loginPanel.add(titleLabel, gbc);

        // USERNAME LABEL
        gbc.gridy++;
        gbc.gridwidth = 1;

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

        // LOGIN ACTION
        loginButton.addActionListener(this::loginAction);

        // SIGNUP ACTION
        signupButton.addActionListener(e -> {

            dispose();

            Signup signup = new Signup();
            signup.setVisible(true);
        });

        setContentPane(loginPanel);
    }

    // LOGIN BUTTON ACTION
    private void loginAction(ActionEvent e) {

        String username = usernameField.getText().trim();
        String password = String.valueOf(passwordField.getPassword());

        // VALIDATION
        if(username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        // ADMIN LOGIN
        if(authenticateAdmin(username, password)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Admin Login Successful",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

            // OPEN ADMIN DASHBOARD
            new AdminDashboard();

        }

        // EMPLOYEE LOGIN
        else if(authenticateEmployee(username, password)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Employee Login Successful",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

            // OPEN EMPLOYEE DASHBOARD
            new EmployeeDashboard();

        }

        // INVALID LOGIN
        else {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid Username or Password",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ADMIN AUTHENTICATION
    private boolean authenticateAdmin(String username, String password) {

        String sql =
                "SELECT * FROM Admins WHERE username=? AND password=?";

        try (
                Connection conn =
                        DriverManager.getConnection(
                                DB_URL,
                                DB_USERNAME,
                                DB_PASSWORD
                        );

                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database Error: " + e.getMessage()
            );

            e.printStackTrace();
        }

        return false;
    }

    // EMPLOYEE AUTHENTICATION
    private boolean authenticateEmployee(
            String username,
            String password
    ) {

        String sql =
                "SELECT * FROM Employees WHERE username=? AND password=?";

        try (
                Connection conn =
                        DriverManager.getConnection(
                                DB_URL,
                                DB_USERNAME,
                                DB_PASSWORD
                        );

                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database Error: " + e.getMessage()
            );

            e.printStackTrace();
        }

        return false;
    }

    // MODERN BUTTON DESIGN
    private JButton createModernButton(String text) {

        JButton button = new JButton(text);

        button.setBackground(new Color(0, 120, 215));
        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);

        button.setFont(new Font("Arial", Font.BOLD, 14));

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        20,
                        10,
                        20
                )
        );

        return button;
    }

    // MAIN METHOD
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Login login = new Login(null);

            login.setVisible(true);
        });
    }
}