package GuiForms;

import dao.employeesDao;
import model.employees;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class EmployeesForm extends JFrame {

    private JPanel panel1;
    private JPanel JPanelMain;

    private JTable table1;

    private JTextField companyNameField;
    private JTextField textField1;
    private JTextField positionField;
    private JTextField HDField;
    private JTextField phoneField;
    private JTextField emailField;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;

    private JPanel JPanel1;
    private JPanel JPanel2;
    private JPanel JPanel4;

    private JLabel JLabelName;
    private JLabel JLabelLastName;
    private JLabel JLabelAddress;
    private JLabel JLabelPhone;
    private JLabel JLabelEmail;
    private JLabel JLabelSupplier;
    private JLabel position;

    private final employeesDao dao = new employeesDao();
    private DefaultTableModel model;

    public EmployeesForm() {
        setContentPane(panel1);
        setTitle("Employees");

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(
                (int) (screen.width * 0.75),
                (int) (screen.height * 0.75)
        );
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initTable();
        loadEmployees();
        initListeners();
        applyStyles();
    }
    private void initTable() {
        model = new DefaultTableModel(
                new Object[]{"ID", "First name", "Last name", "Position", "Hire date", "Phone", "Email"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table1.setModel(model);
    }
    private void loadEmployees() {
        model.setRowCount(0);

        List<employees> list = dao.getAllEmployees();
        for (employees e : list) {
            model.addRow(new Object[]{
                    e.getEmployee_id(),
                    e.getFirst_name(),
                    e.getLast_name(),
                    e.getPosition(),
                    e.getHire_date(),
                    e.getPhone(),
                    e.getEmail()
            });
        }
    }
    private void initListeners() {

        addButton.addActionListener(e -> addEmployee());
        updateButton.addActionListener(e -> updateEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());
        clearButton.addActionListener(e -> clearForm());

        table1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromTable();
            }
        });
    }

    private void addEmployee() {
        if (companyNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "First name is required");
            return;
        }

        try {
            employees emp = new employees(
                    0,
                    companyNameField.getText(),
                    textField1.getText(),
                    positionField.getText(),
                    parseDate(HDField.getText()),
                    phoneField.getText(),
                    emailField.getText()
            );

            dao.addEmployee(emp);
            loadEmployees();
            clearForm();

        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void updateEmployee() {
        int row = table1.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select employee first");
            return;
        }

        try {
            int id = (int) model.getValueAt(row, 0);

            employees emp = new employees(
                    id,
                    companyNameField.getText(),
                    textField1.getText(),
                    positionField.getText(),
                    parseDate(HDField.getText()),
                    phoneField.getText(),
                    emailField.getText()
            );

            dao.updateEmployee(emp);
            loadEmployees();
            clearForm();

        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void deleteEmployee() {
        int row = table1.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select employee first");
            return;
        }

        try {
            int id = (int) model.getValueAt(row, 0);
            dao.deleteEmployee(id);

            loadEmployees();
            clearForm();

        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void fillFormFromTable() {
        int row = table1.getSelectedRow();
        if (row == -1) return;

        companyNameField.setText(model.getValueAt(row, 1).toString());
        textField1.setText(model.getValueAt(row, 2).toString());
        positionField.setText(model.getValueAt(row, 3).toString());

        Object date = model.getValueAt(row, 4);
        HDField.setText(date != null ? date.toString() : "");

        phoneField.setText(model.getValueAt(row, 5).toString());
        emailField.setText(model.getValueAt(row, 6).toString());
    }

    private void clearForm() {
        companyNameField.setText("");
        textField1.setText("");
        positionField.setText("");
        HDField.setText("");
        phoneField.setText("");
        emailField.setText("");
        table1.clearSelection();
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        return LocalDate.parse(s);
    }

    private void showError(RuntimeException ex) {
        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Database error",
                JOptionPane.ERROR_MESSAGE
        );
    }
    private void applyStyles() {

        StyleUtil.stylePanel(panel1);
        StyleUtil.stylePanel(JPanelMain);
        StyleUtil.stylePanel(JPanel1);
        StyleUtil.stylePanel(JPanel2);
        StyleUtil.stylePanel(JPanel4);

        StyleUtil.styleButton(addButton);
        StyleUtil.styleButton(updateButton);
        StyleUtil.styleButton(deleteButton);
        StyleUtil.styleButton(clearButton);

        StyleUtil.styleLabel(JLabelName);
        StyleUtil.styleLabel(JLabelLastName);
        StyleUtil.styleLabel(JLabelAddress);
        StyleUtil.styleLabel(JLabelPhone);
        StyleUtil.styleLabel(JLabelEmail);
        StyleUtil.styleLabel(JLabelSupplier);
        StyleUtil.styleLabel(position);
    }

}
