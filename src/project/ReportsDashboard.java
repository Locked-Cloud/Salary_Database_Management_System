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
                case 0: sql = "SELECT full_name, base_salary FROM vw_Employee_Salaries"; break;
                case 1: sql = "SELECT * FROM vw_March_Payroll"; break;
                case 2: sql = "SELECT * FROM vw_Employee_Bonus_Count"; break;
                case 3: sql = "SELECT * FROM vw_High_Salary_Employees"; break;
                case 4: sql = "SELECT * FROM vw_Comprehensive_Compensation"; break;
                case 5: sql = "SELECT * FROM vw_High_Bonus_Employees"; break;
                case 6: sql = "SELECT full_name, base_salary, tax_bracket, tax_amount, net_after_tax FROM vw_Employee_Salaries"; break;
                case 7: sql = "SELECT * FROM vw_Compensation_History_2025"; break;
                case 8: sql = "SELECT * FROM vw_Above_Average_Salary"; break;
                case 9: sql = "SELECT * FROM vw_Above_Average_Bonus"; break;
                case 10: sql = "SELECT * FROM vw_Employee_Classifications"; break;
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
