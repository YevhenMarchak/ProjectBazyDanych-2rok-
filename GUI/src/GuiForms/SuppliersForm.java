package GuiForms;

import dao.suppliersDao;
import model.Suppliers;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SuppliersForm extends JFrame {


    private JPanel panel1;
    private JTable table1;

    private JTextField companyNameField;
    private JTextField AddressField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField taxIdField;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;


    private JPanel JPanel1;
    private JPanel JPanel2;
    private JPanel JPanel4;

    private JLabel JLabelCompName;
    private JLabel JLabelAddress;
    private JLabel JLabelPhone;
    private JLabel JLabelEmail;
    private JLabel JLabelTax;
    private JLabel JLabelSupplier;


    private final suppliersDao dao = new suppliersDao();
    private DefaultTableModel model;
    private Integer selectedSupplierId = null;


    public SuppliersForm() {
        setContentPane(panel1);
        setTitle("Suppliers");

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(
                (int) (screen.width * 0.75),
                (int) (screen.height * 0.75)
        );
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initTable();
        loadSuppliers();
        initListeners();
        applyStyles();
    }



    private void initTable() {
        model = new DefaultTableModel(
                new Object[]{"ID", "Company", "Phone", "Email"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table1.setModel(model);
    }


    private void loadSuppliers() {
        model.setRowCount(0);
        List<Suppliers> list = dao.getAllSuppliers();

        for (Suppliers s : list) {
            model.addRow(new Object[]{
                    s.getSupplier_id(),
                    s.getCompany_name(),
                    s.getPhone(),
                    s.getEmail()
            });
        }
    }


    private void initListeners() {

        addButton.addActionListener(e -> addSupplier());
        updateButton.addActionListener(e -> updateSupplier());
        deleteButton.addActionListener(e -> deleteSupplier());
        clearButton.addActionListener(e -> clearForm());

        table1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromTable();
            }
        });
    }



    private void addSupplier() {
        try {
            Suppliers s = new Suppliers(
                    0,
                    companyNameField.getText(),
                    AddressField.getText(),
                    phoneField.getText(),
                    emailField.getText(),
                    taxIdField.getText()
            );

            dao.addSupplier(s);
            loadSuppliers();
            clearForm();

            JOptionPane.showMessageDialog(this, "Supplier added");

        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void updateSupplier() {
        if (selectedSupplierId == null) {
            JOptionPane.showMessageDialog(this, "Select supplier first");
            return;
        }

        try {
            Suppliers s = new Suppliers(
                    selectedSupplierId,
                    companyNameField.getText(),
                    AddressField.getText(),
                    phoneField.getText(),
                    emailField.getText(),
                    taxIdField.getText()
            );

            dao.updateSupplier(s);
            loadSuppliers();
            clearForm();

            JOptionPane.showMessageDialog(this, "Supplier updated");

        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void deleteSupplier() {
        if (selectedSupplierId == null) {
            JOptionPane.showMessageDialog(this, "Select supplier first");
            return;
        }

        try {
            dao.deleteSupplier(selectedSupplierId);
            loadSuppliers();
            clearForm();

            JOptionPane.showMessageDialog(this, "Supplier deleted");

        } catch (RuntimeException ex) {
            showError(ex);
        }
    }



    private void fillFormFromTable() {
        int row = table1.getSelectedRow();
        if (row == -1) return;

        selectedSupplierId = (int) model.getValueAt(row, 0);

        Suppliers s = dao.getSupplierById(selectedSupplierId);
        if (s == null) return;

        companyNameField.setText(s.getCompany_name());
        AddressField.setText(s.getAddress());
        phoneField.setText(s.getPhone());
        emailField.setText(s.getEmail());
        taxIdField.setText(s.getTax_id());
    }

    private void clearForm() {
        companyNameField.setText("");
        AddressField.setText("");
        phoneField.setText("");
        emailField.setText("");
        taxIdField.setText("");

        selectedSupplierId = null;
        table1.clearSelection();
    }

    private void showError(RuntimeException ex) {
        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
    private void applyStyles() {

        StyleUtil.stylePanel(panel1);
        StyleUtil.stylePanel(JPanel1);
        StyleUtil.stylePanel(JPanel2);
        StyleUtil.stylePanel(JPanel4);

        StyleUtil.styleButton(addButton);
        StyleUtil.styleButton(updateButton);
        StyleUtil.styleButton(deleteButton);
        StyleUtil.styleButton(clearButton);

        StyleUtil.styleLabel(JLabelCompName);
        StyleUtil.styleLabel(JLabelAddress);
        StyleUtil.styleLabel(JLabelPhone);
        StyleUtil.styleLabel(JLabelEmail);
        StyleUtil.styleLabel(JLabelTax);
        StyleUtil.styleLabel(JLabelSupplier);
    }

}
