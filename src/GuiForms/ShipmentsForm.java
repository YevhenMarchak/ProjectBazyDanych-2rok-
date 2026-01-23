package GuiForms;

import dao.shipmentsDao;
import dao.shipment_detailsDao;
import dao.productsDao;
import model.shipments;
import model.shipment_details;
import model.products;
import dao.clientsDao;
import dao.employeesDao;
import model.clients;
import model.employees;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ShipmentsForm extends JFrame {

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

    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;

    private JButton button5;
    private JButton button6;
    private JButton button7;
    private JButton button8;
    private JComboBox<clients> comboBox2;
    private JComboBox<employees> comboBox3;

    private final shipmentsDao shipmentsDao = new shipmentsDao();
    private final shipment_detailsDao detailsDao = new shipment_detailsDao();
    private final productsDao productsDao = new productsDao();
    private final clientsDao clientsDao = new clientsDao();
    private final employeesDao employeesDao = new employeesDao();

    private DefaultTableModel shipmentsModel;
    private DefaultTableModel detailsModel;

    private Integer selectedShipmentId = null;
    public ShipmentsForm() {

        setContentPane(JPanelMain);
        setTitle("Shipments");

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(
                (int) (screen.width * 0.75),
                (int) (screen.height * 0.75)
        );
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initTables();
        loadShipments();
        loadClientsToCombo();
        loadEmployeesToCombo();
        loadProductsToCombo();
        initListeners();
        applyStyles();
    }
    private void initTables() {

        shipmentsModel = new DefaultTableModel(
                new Object[]{"ID", "Client", "Employee", "Date", "Order", "Status"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table1.setModel(shipmentsModel);

        detailsModel = new DefaultTableModel(
                new Object[]{"Product", "Quantity"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table2.setModel(detailsModel);
    }
    private void loadClientsToCombo() {
        comboBox2.removeAllItems();

        for (clients c : clientsDao.getAllClients()) {
            comboBox2.addItem(c);
        }

        comboBox2.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel();
            if (value != null) {
                clients c = (clients) value;
                lbl.setText(
                        c.getClient_id() + " | " + c.getCompany_name()
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


    private void loadShipments() {
        shipmentsModel.setRowCount(0);

        List<shipments> list = shipmentsDao.getAllShipments();
        for (shipments s : list) {
            shipmentsModel.addRow(new Object[]{
                    s.getShipment_id(),
                    s.getClient_id(),
                    s.getEmployee_id(),
                    s.getShipment_date(),
                    s.getClient_order_no(),
                    s.getStatus()
            });
        }
    }

    private void loadShipmentDetails(int shipmentId) {
        detailsModel.setRowCount(0);

        List<shipment_details> list =
                detailsDao.getShipmentDetailsByShipmentId(shipmentId);

        for (shipment_details d : list) {
            detailsModel.addRow(new Object[]{
                    d.getProduct_id(),
                    d.getQuantity_to_ship()
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
                l.setOpaque(true);
                l.setBackground(list.getSelectionBackground());
                l.setForeground(list.getSelectionForeground());
            }
            return l;
        });
    }
    private void initListeners() {

        table1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillShipmentFromTable();
        });

        table2.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillDetailFromTable();
        });

        button1.addActionListener(e -> addShipment());
        button2.addActionListener(e -> updateShipment());
        button3.addActionListener(e -> deleteShipment());
        button4.addActionListener(e -> clearShipmentForm());

        button5.addActionListener(e -> addDetail());
        button6.addActionListener(e -> updateDetail());
        button7.addActionListener(e -> deleteDetail());
        button8.addActionListener(e -> clearDetailForm());
    }
    private void fillShipmentFromTable() {
        int row = table1.getSelectedRow();
        if (row < 0) return;

        selectedShipmentId =
                Integer.parseInt(shipmentsModel.getValueAt(row, 0).toString());

        textField1.setText(shipmentsModel.getValueAt(row, 1).toString());
        textField2.setText(
                shipmentsModel.getValueAt(row, 2) != null
                        ? shipmentsModel.getValueAt(row, 2).toString()
                        : ""
        );
        textField3.setText(shipmentsModel.getValueAt(row, 3).toString());
        textField4.setText(shipmentsModel.getValueAt(row, 4).toString());
        textField5.setText(shipmentsModel.getValueAt(row, 5).toString());

        loadShipmentDetails(selectedShipmentId);

        int clientId = Integer.parseInt(shipmentsModel.getValueAt(row, 1).toString());
        Object empObj = shipmentsModel.getValueAt(row, 2);
        Integer employeeId = empObj != null ? Integer.parseInt(empObj.toString()) : null;

        textField1.setText(String.valueOf(clientId));
        textField2.setText(employeeId != null ? employeeId.toString() : "");

        for (int i = 0; i < comboBox2.getItemCount(); i++) {
            clients c = comboBox2.getItemAt(i);
            if (c.getClient_id() == clientId) {
                comboBox2.setSelectedIndex(i);
                break;
            }
        }

        if (employeeId != null) {
            for (int i = 0; i < comboBox3.getItemCount(); i++) {
                employees e = comboBox3.getItemAt(i);
                if (e.getEmployee_id() == employeeId) {
                    comboBox3.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            comboBox3.setSelectedIndex(-1);
        }

    }

    private void fillDetailFromTable() {
        int row = table2.getSelectedRow();
        if (row < 0) return;

        int productId =
                Integer.parseInt(detailsModel.getValueAt(row, 0).toString());

        for (int i = 0; i < comboBox1.getItemCount(); i++) {
            if (comboBox1.getItemAt(i).getProduct_id() == productId) {
                comboBox1.setSelectedIndex(i);
                break;
            }
        }

        textField6.setText(detailsModel.getValueAt(row, 1).toString());
    }
    private void addShipment() {
        try {
            shipments s = new shipments(
                    0,
                    Integer.parseInt(textField1.getText()),
                    textField2.getText().isEmpty()
                            ? null
                            : Integer.parseInt(textField2.getText()),
                    LocalDate.parse(textField3.getText()),
                    textField4.getText(),
                    textField5.getText()
            );
            shipmentsDao.addShipment(s);
            loadShipments();
        } catch (RuntimeException ex) {
            showDbError(ex);
        }
    }

    private void updateShipment() {
        if (selectedShipmentId == null) return;

        try {
            shipments s = new shipments(
                    selectedShipmentId,
                    Integer.parseInt(textField1.getText()),
                    textField2.getText().isEmpty()
                            ? null
                            : Integer.parseInt(textField2.getText()),
                    LocalDate.parse(textField3.getText()),
                    textField4.getText(),
                    textField5.getText()
            );
            shipmentsDao.updateShipment(s);
            loadShipments();
        } catch (RuntimeException ex) {
            showDbError(ex);
        }
    }

    private void deleteShipment() {
        if (selectedShipmentId == null) return;

        try {
            shipmentsDao.deleteShipment(selectedShipmentId);
            loadShipments();
            detailsModel.setRowCount(0);
            clearShipmentForm();
        } catch (RuntimeException ex) {
            showDbError(ex);
        }
    }
    private void addDetail() {
        if (selectedShipmentId == null) return;

        try {
            products p = (products) comboBox1.getSelectedItem();

            shipment_details d = new shipment_details(
                    selectedShipmentId,
                    p.getProduct_id(),
                    Double.parseDouble(textField6.getText())
            );

            detailsDao.addShipmentDetail(d);
            loadShipmentDetails(selectedShipmentId);
        } catch (RuntimeException ex) {
            showDbError(ex);
        }
    }

    private void updateDetail() {
        if (selectedShipmentId == null) return;

        try {
            products p = (products) comboBox1.getSelectedItem();

            shipment_details d = new shipment_details(
                    selectedShipmentId,
                    p.getProduct_id(),
                    Double.parseDouble(textField6.getText())
            );

            detailsDao.updateShipmentDetail(d);
            loadShipmentDetails(selectedShipmentId);
        } catch (RuntimeException ex) {
            showDbError(ex);
        }
    }

    private void deleteDetail() {
        if (selectedShipmentId == null) return;

        try {
            products p = (products) comboBox1.getSelectedItem();

            detailsDao.deleteShipmentDetail(
                    selectedShipmentId,
                    p.getProduct_id()
            );

            loadShipmentDetails(selectedShipmentId);
            clearDetailForm();
        } catch (RuntimeException ex) {
            showDbError(ex);
        }
    }
    private void clearShipmentForm() {
        textField1.setText("");
        textField2.setText("");
        textField3.setText("");
        textField4.setText("");
        textField5.setText("");

        comboBox2.setSelectedIndex(-1);
        comboBox3.setSelectedIndex(-1);

        selectedShipmentId = null;
        table1.clearSelection();
    }


    private void clearDetailForm() {
        comboBox1.setSelectedIndex(-1);
        textField6.setText("");
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
