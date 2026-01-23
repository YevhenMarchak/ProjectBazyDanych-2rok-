package GuiForms;

import dao.clientsDao;
import model.clients;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClientsForm extends JFrame {

    private JPanel panel1;
    private JPanel JPanelMain;

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
    private JLabel JLabelClients;

    private final clientsDao dao = new clientsDao();
    private DefaultTableModel model;

    public ClientsForm() {
        setContentPane(panel1);
        setTitle("Clients");

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(
                (int) (screen.width * 0.75),
                (int) (screen.height * 0.75)
        );
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initTable();
        loadClients();
        initListeners();
        applyStyles();
    }



    private void initTable() {
        model = new DefaultTableModel(
                new Object[]{"ID", "Company", "Address", "Phone", "Email", "Tax ID"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table1.setModel(model);
    }

    private void loadClients() {
        model.setRowCount(0);

        List<clients> list = dao.getAllClients();
        for (clients c : list) {
            model.addRow(new Object[]{
                    c.getClient_id(),
                    c.getCompany_name(),
                    c.getDelivery_address(),
                    c.getPhone(),
                    c.getEmail(),
                    c.getTax_id()
            });
        }
    }
    private void initListeners() {

        addButton.addActionListener(e -> addClient());
        updateButton.addActionListener(e -> updateClient());
        deleteButton.addActionListener(e -> deleteClient());
        clearButton.addActionListener(e -> clearForm());

        table1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromTable();
            }
        });
    }

    private void addClient() {
        if (companyNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Company name is required");
            return;
        }

        try {
            clients c = new clients(
                    0,
                    companyNameField.getText(),
                    AddressField.getText(),
                    phoneField.getText(),
                    emailField.getText(),
                    taxIdField.getText()
            );

            dao.addClient(c);
            loadClients();
            clearForm();

        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void updateClient() {
        int row = table1.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select client first");
            return;
        }

        try {
            int id = (int) model.getValueAt(row, 0);

            clients c = new clients(
                    id,
                    companyNameField.getText(),
                    AddressField.getText(),
                    phoneField.getText(),
                    emailField.getText(),
                    taxIdField.getText()
            );

            dao.updateClient(c);
            loadClients();
            clearForm();

        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void deleteClient() {
        int row = table1.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select client first");
            return;
        }

        try {
            int id = (int) model.getValueAt(row, 0);
            dao.deleteClient(id);

            loadClients();
            clearForm();

        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void fillFormFromTable() {
        int row = table1.getSelectedRow();
        if (row == -1) return;

        companyNameField.setText(model.getValueAt(row, 1).toString());
        AddressField.setText(model.getValueAt(row, 2).toString());
        phoneField.setText(model.getValueAt(row, 3).toString());
        emailField.setText(model.getValueAt(row, 4).toString());
        taxIdField.setText(model.getValueAt(row, 5).toString());
    }

    private void clearForm() {
        companyNameField.setText("");
        AddressField.setText("");
        phoneField.setText("");
        emailField.setText("");
        taxIdField.setText("");
        table1.clearSelection();
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

        StyleUtil.styleLabel(JLabelCompName);
        StyleUtil.styleLabel(JLabelAddress);
        StyleUtil.styleLabel(JLabelPhone);
        StyleUtil.styleLabel(JLabelEmail);
        StyleUtil.styleLabel(JLabelTax);
        StyleUtil.styleLabel(JLabelClients);
    }

}
