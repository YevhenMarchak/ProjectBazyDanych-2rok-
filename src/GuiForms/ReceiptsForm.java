package GuiForms;

import dao.receiptsDao;
import dao.receipt_detailsDao;
import dao.productsDao;
import dao.suppliersDao;
import model.Suppliers;
import model.receipts;
import model.receipt_details;
import model.products;
import dao.employeesDao;
import model.employees;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ReceiptsForm extends JFrame {

    private JPanel JPanelMain;

    private JTable table1;
    private JTable table2;

    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;

    private JComboBox<products> comboBox1;
    private JTextField textField6;
    private JTextField textField7;

    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;

    private JButton button5;
    private JButton button6;
    private JButton button7;
    private JButton button8;
    private JComboBox comboBox2;
    private JComboBox comboBox3;

    private final receiptsDao receiptsDao = new receiptsDao();
    private final receipt_detailsDao detailsDao = new receipt_detailsDao();
    private final productsDao productsDao = new productsDao();
    private final suppliersDao suppliersDao = new suppliersDao();
    private final employeesDao employeesDao = new employeesDao();

    private DefaultTableModel receiptsModel;
    private DefaultTableModel detailsModel;

    private Integer selectedReceiptId = null;

    public ReceiptsForm() {

        setContentPane(JPanelMain);
        setTitle("Receipts");

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(
                (int) (screen.width * 0.75),
                (int) (screen.height * 0.75)
        );
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initTables();
        loadReceipts();
        loadSuppliersToCombo();
        loadEmployeesToCombo();
        loadProductsToCombo();
        applyStyles();
        initListeners();
    }

    private void initTables() {

        receiptsModel = new DefaultTableModel(
                new Object[]{"ID", "Supplier", "Employee", "Date", "Invoice", "Status"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table1.setModel(receiptsModel);

        detailsModel = new DefaultTableModel(
                new Object[]{"Product", "Expected", "Received"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table2.setModel(detailsModel);
    }
    private void loadSuppliersToCombo() {
        comboBox2.removeAllItems();


        for (Suppliers s : suppliersDao.getAllSuppliers()) {
            comboBox2.addItem(s);
        }

        comboBox2.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel();
            if (value != null) {
                Suppliers s = (Suppliers) value;
                lbl.setText(
                        s.getSupplier_id() + " | " + s.getCompany_name()
                );
            }
            if (isSelected) {
                lbl.setOpaque(true);
                lbl.setBackground(list.getSelectionBackground());
                lbl.setForeground(list.getSelectionForeground());
            }
            return lbl;
        });
    }
    private void loadEmployeesToCombo() {
        comboBox3.removeAllItems();

        for (employees e : employeesDao.getAllEmployees()) {
            comboBox3.addItem(e);
        }

        comboBox3.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel();
            if (value != null) {
                employees e = (employees) value;
                lbl.setText(
                        e.getEmployee_id() + " | " +
                                e.getFirst_name() + " " + e.getLast_name()
                );
            }
            if (isSelected) {
                lbl.setOpaque(true);
                lbl.setBackground(list.getSelectionBackground());
                lbl.setForeground(list.getSelectionForeground());
            }
            return lbl;
        });
    }



    private void loadReceipts() {
        receiptsModel.setRowCount(0);

        List<receipts> list = receiptsDao.getAllReceipts();
        for (receipts r : list) {
            receiptsModel.addRow(new Object[]{
                    r.getReceipt_id(),
                    r.getSupplier_id(),
                    r.getEmployee_id(),
                    r.getReceipt_date(),
                    r.getExternal_invoice_no(),
                    r.getStatus()
            });
        }
    }

    private void loadReceiptDetails(long receiptId) {
        detailsModel.setRowCount(0);

        List<receipt_details> list =
                detailsDao.getReceiptDetailsByReceiptId(receiptId);

        for (receipt_details d : list) {
            detailsModel.addRow(new Object[]{
                    d.getProduct_id(),
                    d.getExpected_quantity(),
                    d.getReceived_quantity()
            });
        }
    }

    private void loadProductsToCombo() {
        comboBox1.removeAllItems();

        for (products p : productsDao.getAllProducts()) {
            comboBox1.addItem(p);
        }

        comboBox1.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel l = new JLabel();
            if (value != null) {
                l.setText(value.getName() + " | SKU: " + value.getSku());
            }
            if (isSelected) {
                l.setBackground(list.getSelectionBackground());
                l.setForeground(list.getSelectionForeground());
                l.setOpaque(true);
            }
            return l;
        });
    }
    private void initListeners() {

        table1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillReceiptFromTable();
        });

        table2.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillDetailFromTable();
        });

        button1.addActionListener(e -> addReceipt());
        button2.addActionListener(e -> updateReceipt());
        button3.addActionListener(e -> deleteReceipt());
        button4.addActionListener(e -> clearReceiptForm());

        button5.addActionListener(e -> addDetail());
        button6.addActionListener(e -> updateDetail());
        button7.addActionListener(e -> deleteDetail());
        button8.addActionListener(e -> clearDetailForm());
    }
    private void fillReceiptFromTable() {
        int row = table1.getSelectedRow();
        if (row < 0) return;

        selectedReceiptId =
                Integer.parseInt(receiptsModel.getValueAt(row, 0).toString());

        int supplierId = Integer.parseInt(
                receiptsModel.getValueAt(row, 1).toString()
        );

        Object empObj = receiptsModel.getValueAt(row, 2);
        Integer employeeId =
                empObj != null ? Integer.parseInt(empObj.toString()) : null;

        textField1.setText(String.valueOf(supplierId));
        textField2.setText(employeeId != null ? employeeId.toString() : "");

        textField3.setText(receiptsModel.getValueAt(row, 3).toString());
        textField4.setText(receiptsModel.getValueAt(row, 4).toString());
        textField5.setText(receiptsModel.getValueAt(row, 5).toString());

        for (int i = 0; i < comboBox2.getItemCount(); i++) {
            Suppliers s = (Suppliers) comboBox2.getItemAt(i);
            if (s.getSupplier_id() == supplierId) {
                comboBox2.setSelectedIndex(i);
                break;
            }
        }

        if (employeeId != null) {
            for (int i = 0; i < comboBox3.getItemCount(); i++) {
                employees e = (employees) comboBox3.getItemAt(i);
                if (e.getEmployee_id() == employeeId) {
                    comboBox3.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            comboBox3.setSelectedIndex(-1);
        }


        loadReceiptDetails(selectedReceiptId);
    }


    private void fillDetailFromTable() {
        int row = table2.getSelectedRow();
        if (row < 0) return;

        Long productId =
                Long.parseLong(detailsModel.getValueAt(row, 0).toString());

        for (int i = 0; i < comboBox1.getItemCount(); i++) {
            if (comboBox1.getItemAt(i).getProduct_id() == productId) {
                comboBox1.setSelectedIndex(i);
                break;
            }
        }

        textField6.setText(detailsModel.getValueAt(row, 1).toString());
        textField7.setText(detailsModel.getValueAt(row, 2).toString());
    }
    private void addReceipt() {
        try {
            receipts r = new receipts(
                    0,
                    Integer.parseInt(textField1.getText()),
                    textField2.getText().isEmpty()
                            ? null
                            : Integer.parseInt(textField2.getText()),
                    LocalDate.parse(textField3.getText()),
                    textField4.getText(),
                    textField5.getText()
            );

            receiptsDao.addReceipt(r);
            loadReceipts();
        } catch (RuntimeException ex) {
            showDbError(ex);
        }
    }

    private void updateReceipt() {
        if (selectedReceiptId == null) return;

        try {
            receipts r = new receipts(
                    selectedReceiptId,
                    Integer.parseInt(textField1.getText()),
                    textField2.getText().isEmpty()
                            ? null
                            : Integer.parseInt(textField2.getText()),
                    LocalDate.parse(textField3.getText()),
                    textField4.getText(),
                    textField5.getText()
            );

            receiptsDao.updateReceipt(r);
            loadReceipts();
        } catch (RuntimeException ex) {
            showDbError(ex);
        }
    }

    private void deleteReceipt() {
        if (selectedReceiptId == null) return;

        try {
            receiptsDao.deleteReceipt(selectedReceiptId);
            loadReceipts();
            detailsModel.setRowCount(0);
            clearReceiptForm();
        } catch (RuntimeException ex) {
            showDbError(ex);
        }
    }
    private void addDetail() {
        if (selectedReceiptId == null) return;

        try {
            products p = (products) comboBox1.getSelectedItem();

            receipt_details d = new receipt_details(
                    selectedReceiptId,
                    p.getProduct_id(),
                    new BigDecimal(textField6.getText()),
                    new BigDecimal(textField7.getText())
            );

            detailsDao.addReceiptDetail(d);
            loadReceiptDetails(selectedReceiptId);
        } catch (RuntimeException ex) {
            showDbError(ex);
        }
    }

    private void updateDetail() {
        if (selectedReceiptId == null) return;

        try {
            products p = (products) comboBox1.getSelectedItem();

            receipt_details d = new receipt_details(
                    selectedReceiptId,
                    p.getProduct_id(),
                    new BigDecimal(textField6.getText()),
                    new BigDecimal(textField7.getText())
            );

            detailsDao.updateReceiptDetail(d);
            loadReceiptDetails(selectedReceiptId);
        } catch (RuntimeException ex) {
            showDbError(ex);
        }
    }

    private void deleteDetail() {
        if (selectedReceiptId == null) return;

        try {
            products p = (products) comboBox1.getSelectedItem();

            detailsDao.deleteReceiptDetail(
                    selectedReceiptId,
                    p.getProduct_id()
            );

            loadReceiptDetails(selectedReceiptId);
            clearDetailForm();
        } catch (RuntimeException ex) {
            showDbError(ex);
        }
    }
    private void clearReceiptForm() {
        textField1.setText("");
        textField2.setText("");
        textField3.setText("");
        textField4.setText("");
        textField5.setText("");

        comboBox2.setSelectedIndex(-1);
        comboBox3.setSelectedIndex(-1);

        selectedReceiptId = null;
        table1.clearSelection();
    }
    private void clearDetailForm() {
        comboBox1.setSelectedIndex(-1);
        textField6.setText("");
        textField7.setText("");
        table2.clearSelection();
    }
    private void showDbError(RuntimeException ex) {
        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Database validation error",
                JOptionPane.ERROR_MESSAGE
        );
    }
    private void applyStyles() {

        StyleUtil.stylePanel(JPanelMain);

        for (Component c : JPanelMain.getComponents()) {
            styleLabelsRecursively(c);
        }

        StyleUtil.styleButton(button1);
        StyleUtil.styleButton(button2);
        StyleUtil.styleButton(button3);
        StyleUtil.styleButton(button4);

        StyleUtil.styleButton(button5);
        StyleUtil.styleButton(button6);
        StyleUtil.styleButton(button7);
        StyleUtil.styleButton(button8);
    }

    private void styleLabelsRecursively(Component c) {
        if (c instanceof JLabel label) {
            StyleUtil.styleLabel(label);
        }
        if (c instanceof Container container) {
            for (Component child : container.getComponents()) {
                styleLabelsRecursively(child);
            }
        }
    }
}
