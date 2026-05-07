package project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class ManageBonuses extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/SalaryMan";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    private DefaultTableModel tableModel;
    private JTable bonusTable;

    public ManageBonuses() {
        setTitle("Bonuses & Deductions");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // --- TOP: Form ---
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Bonus / Deduction"));

        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Bonus", "Deduction"});
        JTextField empIdField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField dateField = new JTextField("YYYY-MM-DD");
        JTextField reasonField = new JTextField();

        formPanel.add(new JLabel("Type:")); formPanel.add(typeBox);
        formPanel.add(new JLabel("Employee ID:")); formPanel.add(empIdField);
        formPanel.add(new JLabel("Amount ($):")); formPanel.add(amountField);
        formPanel.add(new JLabel("Date:")); formPanel.add(dateField);
        formPanel.add(new JLabel("Reason:")); formPanel.add(reasonField);

        JButton saveBtn = new JButton("Add Record");
        
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(formPanel, BorderLayout.CENTER);
        topContainer.add(saveBtn, BorderLayout.SOUTH);

        // --- CENTER: Table ---
        tableModel = new DefaultTableModel();
        bonusTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bonusTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Bonuses"));

        mainPanel.add(topContainer, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);

        // --- Actions ---
        saveBtn.addActionListener(e -> {
            String empIdStr = empIdField.getText().trim();
            String amountStr = amountField.getText().trim();
            String dateStr = dateField.getText().trim();
            String reasonStr = reasonField.getText().trim();
            String type = typeBox.getSelectedItem().toString();

            if (empIdStr.isEmpty() || amountStr.isEmpty() || reasonStr.isEmpty() || (type.equals("Bonus") && (dateStr.isEmpty() || dateStr.equals("YYYY-MM-DD")))) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int empId = Integer.parseInt(empIdStr);
                double amount = Double.parseDouble(amountStr);

                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                    if (type.equals("Bonus")) {
                        String sql = "INSERT INTO Bonuses (employee_id, bonus_amount, bonus_date, reason) VALUES (?, ?, ?, ?)";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, empId);
                        stmt.setDouble(2, amount);
                        stmt.setString(3, dateStr);
                        stmt.setString(4, reasonStr);
                        stmt.executeUpdate();
                    } else {
                        String sql = "INSERT INTO Deductions (employee_id, deduction_type, deduction_amount) VALUES (?, ?, ?)";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, empId);
                        stmt.setString(2, reasonStr);
                        stmt.setDouble(3, amount);
                        stmt.executeUpdate();
                    }
                    JOptionPane.showMessageDialog(this, type + " Added successfully!");
                    
                    empIdField.setText("");
                    amountField.setText("");
                    dateField.setText("YYYY-MM-DD");
                    reasonField.setText("");
                    
                    loadBonusData();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Employee ID and Amount must be valid numbers.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loadBonusData();
    }

    private void loadBonusData() {
        // We will just show Bonuses for simplicity. Alternatively, we could show Deductions based on a toggle.
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Bonuses")) {

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
