package GuiForms;

import dao.inventoryDao;
import dao.productsDao;
import dao.locationsDao;
import model.inventory;
import model.products;
import model.locations;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InventoryForm extends JFrame {

    private JPanel JLabelMain;
    private JTable table1;


    private JComboBox<products> comboBox1;
    private JComboBox<locations> comboBox2;
    private JTextField quantityField;
    private JTextField textField3;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;

    private JLabel JLabelInventory;
    private JLabel JLabelProduct;
    private JLabel JLabelLocation;
    private JLabel JLabelQuantity;
    private JLabel JLabelUpdate;

    private JPanel panel1;
    private JPanel JPanel1;
    private JPanel JPanel2;
    private JPanel JPanel4;
    private JTextField textField1;
    private JTextField textField2;


    private final inventoryDao inventoryDao = new inventoryDao();
    private final productsDao productsDao = new productsDao();
    private final locationsDao locationsDao = new locationsDao();

    private DefaultTableModel model;

    private Integer selectedInventoryId = null;
    public InventoryForm() {

        setContentPane(JLabelMain);
        setTitle("Inventory");

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(
                (int) (screen.width * 0.75),
                (int) (screen.height * 0.75)
        );
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        textField3.setEditable(false);

        initTable();
        loadInventory();
        loadProducts();
        loadLocations();
        initListeners();
        applyStyles();
    }
    private void initTable() {
        model = new DefaultTableModel(
                new Object[]{"ID", "Product ID", "Location ID", "Quantity", "Last updated"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table1.setModel(model);
    }
    private void loadInventory() {
        model.setRowCount(0);

        for (inventory i : inventoryDao.getAllInventory()) {
            model.addRow(new Object[]{
                    i.getInventory_id(),
                    i.getProduct_id(),
                    i.getLocation_id(),
                    i.getQuantity(),
                    i.getLast_updated()
            });
        }
    }


    private void loadProducts() {
        comboBox1.removeAllItems();

        for (products p : productsDao.getAllProducts()) {
            comboBox1.addItem(p);
        }

        comboBox1.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel();
            if (value != null) {
                lbl.setText(
                        value.getProduct_id() + " | " +
                                value.getName() + " | SKU: " + value.getSku()
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


    private void loadLocations() {
        comboBox2.removeAllItems();

        for (locations loc : locationsDao.getAllLocations()) {
            comboBox2.addItem(loc);
        }

        comboBox2.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel();
            if (value != null) {
                lbl.setText(
                        value.getLocation_id() + " | " +
                                value.getLocation_code() + " | " +
                                value.getLocation_type()
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
    private void initListeners() {

        table1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromTable();
        });

        addButton.addActionListener(e -> addInventory());
        updateButton.addActionListener(e -> updateInventory());
        deleteButton.addActionListener(e -> deleteInventory());
        clearButton.addActionListener(e -> clearForm());
    }
    private void addInventory() {
        try {
            products p = (products) comboBox1.getSelectedItem();
            locations l = (locations) comboBox2.getSelectedItem();

            inventory inv = new inventory(
                    0,
                    p.getProduct_id(),
                    l.getLocation_id(),
                    Double.parseDouble(quantityField.getText()),
                    null
            );

            inventoryDao.addInventory(inv);
            loadInventory();
            clearForm();

        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void updateInventory() {
        if (selectedInventoryId == null) return;

        try {
            products p = (products) comboBox1.getSelectedItem();
            locations l = (locations) comboBox2.getSelectedItem();

            inventory inv = new inventory(
                    selectedInventoryId,
                    p.getProduct_id(),
                    l.getLocation_id(),
                    Double.parseDouble(quantityField.getText()),
                    null
            );

            inventoryDao.updateInventory(inv);
            loadInventory();
            clearForm();

        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void deleteInventory() {
        if (selectedInventoryId == null) return;

        try {
            inventoryDao.deleteInventory(selectedInventoryId);
            loadInventory();
            clearForm();
        } catch (RuntimeException ex) {
            showError(ex);
        }
    }
    private void fillFormFromTable() {
        int row = table1.getSelectedRow();
        if (row < 0) return;

        selectedInventoryId = (int) model.getValueAt(row, 0);

        int productId = (int) model.getValueAt(row, 1);
        int locationId = (int) model.getValueAt(row, 2);

        textField1.setText(String.valueOf(productId));
        textField2.setText(String.valueOf(locationId));

        for (int i = 0; i < comboBox1.getItemCount(); i++) {
            if (comboBox1.getItemAt(i).getProduct_id() == productId) {
                comboBox1.setSelectedIndex(i);
                break;
            }
        }

        for (int i = 0; i < comboBox2.getItemCount(); i++) {
            if (comboBox2.getItemAt(i).getLocation_id() == locationId) {
                comboBox2.setSelectedIndex(i);
                break;
            }
        }

        quantityField.setText(model.getValueAt(row, 3).toString());

        Object lastUpdated = model.getValueAt(row, 4);
        textField3.setText(lastUpdated != null ? lastUpdated.toString() : "");
    }


    private void clearForm() {
        textField1.setText("");
        textField2.setText("");
        comboBox1.setSelectedIndex(-1);
        comboBox2.setSelectedIndex(-1);
        quantityField.setText("");
        textField3.setText("");
        selectedInventoryId = null;
        table1.clearSelection();
    }


    private void showError(RuntimeException ex) {
        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Database validation error",
                JOptionPane.ERROR_MESSAGE
        );
    }
    private void applyStyles() {
        StyleUtil.stylePanel(JLabelMain);
        StyleUtil.stylePanel(panel1);
        StyleUtil.stylePanel(JPanel1);
        StyleUtil.stylePanel(JPanel2);
        StyleUtil.stylePanel(JPanel4);

        StyleUtil.styleButton(addButton);
        StyleUtil.styleButton(updateButton);
        StyleUtil.styleButton(deleteButton);
        StyleUtil.styleButton(clearButton);

        StyleUtil.styleLabel(JLabelInventory);
        StyleUtil.styleLabel(JLabelProduct);
        StyleUtil.styleLabel(JLabelLocation);
        StyleUtil.styleLabel(JLabelQuantity);
        StyleUtil.styleLabel(JLabelUpdate);
    }
}
