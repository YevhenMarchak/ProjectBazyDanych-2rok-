package GuiForms;

import dao.productsDao;
import model.products;
import dao.categoriesDao;
import dao.suppliersDao;
import model.categories;
import model.Suppliers;




import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductsForm extends JFrame {

    private JPanel panel1;
    private JPanel JPanelMain;

    private JTable table1;

    private JTextField skuField;
    private JTextField nameField;
    private JTextField textField1;
    private JTextField categoryField;
    private JTextField SupplierField;
    private JTextField weightField;
    private JTextField dimensionsField;

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
    private JLabel JLabelWeight;
    private JLabel JLabelDimensions;
    private JLabel JLabelSupplier;
    private JComboBox comboBox1;
    private JComboBox comboBox2;

    private final productsDao dao = new productsDao();
    private final categoriesDao categoriesDao = new categoriesDao();
    private final suppliersDao suppliersDao = new suppliersDao();



    private DefaultTableModel model;
    public ProductsForm() {
        setContentPane(panel1);
        setTitle("Products");

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(
                (int) (screen.width * 0.75),
                (int) (screen.height * 0.75)
        );
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initTable();
        loadProducts();

        loadCategories();
        loadSuppliers();

        initListeners();
        applyStyles();
    }
    private void initTable() {
        model = new DefaultTableModel(
                new Object[]{"ID", "SKU", "Name", "Description", "Category ID", "Supplier ID", "Weight", "Dimensions"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table1.setModel(model);
    }

    private void loadProducts() {
        model.setRowCount(0);
        List<products> list = dao.getAllProducts();

        for (products p : list) {
            model.addRow(new Object[]{
                    p.getProduct_id(),
                    p.getSku(),
                    p.getName(),
                    p.getDescription(),
                    p.getCategory_id(),
                    p.getSupplier_id(),
                    p.getWeight(),
                    p.getDimensions()
            });
        }
    }
    private void loadCategories() {
        comboBox1.removeAllItems();

        for (categories c : categoriesDao.getAllCategories()) {
            comboBox1.addItem(c);
        }

        comboBox1.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel();
            if (value != null) {
                categories c = (categories) value;
                lbl.setText(c.getCategory_id() + " | " + c.getName());
            }
            if (isSelected) {
                lbl.setOpaque(true);
                lbl.setBackground(list.getSelectionBackground());
                lbl.setForeground(list.getSelectionForeground());
            }
            return lbl;
        });

    }
    private void loadSuppliers() {
        comboBox2.removeAllItems();

        for (Suppliers s : suppliersDao.getAllSuppliers()) {
            comboBox2.addItem(s);
        }

        comboBox2.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel();
            if (value != null) {
                Suppliers s = (Suppliers) value;
                lbl.setText(s.getSupplier_id() + " | " + s.getCompany_name());
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

        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        clearButton.addActionListener(e -> clearForm());

        table1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromTable();
            }
        });
    }

    private void addProduct() {
        try {
            Integer supplierId = SupplierField.getText().trim().isEmpty()
                    ? null
                    : Integer.parseInt(SupplierField.getText());

            products p = new products(
                    0,
                    skuField.getText(),
                    nameField.getText(),
                    textField1.getText(), // description
                    Integer.parseInt(categoryField.getText()),
                    supplierId,
                    Double.parseDouble(weightField.getText()),
                    dimensionsField.getText()
            );

            dao.addProduct(p);
            loadProducts();
            clearForm();

        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void updateProduct() {
        int row = table1.getSelectedRow();
        if (row == -1) return;

        try {
            Integer supplierId = SupplierField.getText().trim().isEmpty()
                    ? null
                    : Integer.parseInt(SupplierField.getText());

            int id = (int) model.getValueAt(row, 0);

            products p = new products(
                    id,
                    skuField.getText(),
                    nameField.getText(),
                    textField1.getText(),
                    Integer.parseInt(categoryField.getText()),
                    supplierId,
                    Double.parseDouble(weightField.getText()),
                    dimensionsField.getText()
            );

            dao.updateProduct(p);
            loadProducts();
            clearForm();

        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void deleteProduct() {
        int row = table1.getSelectedRow();
        if (row == -1) return;

        try {
            int id = (int) model.getValueAt(row, 0);
            dao.deleteProduct(id);

            loadProducts();
            clearForm();

        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void fillFormFromTable() {
        int row = table1.getSelectedRow();
        if (row == -1) return;

        skuField.setText(model.getValueAt(row, 1).toString());
        nameField.setText(model.getValueAt(row, 2).toString());
        textField1.setText(model.getValueAt(row, 3).toString());

        int categoryId = (int) model.getValueAt(row, 4);
        Object supObj = model.getValueAt(row, 5);
        Integer supplierId = supObj != null ? (int) supObj : null;

        categoryField.setText(String.valueOf(categoryId));
        SupplierField.setText(supplierId != null ? supplierId.toString() : "");

        for (int i = 0; i < comboBox1.getItemCount(); i++) {
            categories c = (categories) comboBox1.getItemAt(i);
            if (c.getCategory_id() == categoryId) {
                comboBox1.setSelectedIndex(i);
                break;
            }
        }

        for (int i = 0; i < comboBox2.getItemCount(); i++) {
            Suppliers s = (Suppliers) comboBox2.getItemAt(i);
            if (s.getSupplier_id() == supplierId) {
                comboBox2.setSelectedIndex(i);
                break;
            }
        }


        weightField.setText(model.getValueAt(row, 6).toString());
        dimensionsField.setText(model.getValueAt(row, 7).toString());
    }


    private void clearForm() {
        skuField.setText("");
        nameField.setText("");
        textField1.setText("");
        categoryField.setText("");
        SupplierField.setText("");
        comboBox1.setSelectedIndex(-1);
        comboBox2.setSelectedIndex(-1);
        weightField.setText("");
        dimensionsField.setText("");
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
        StyleUtil.styleLabel(JLabelWeight);
        StyleUtil.styleLabel(JLabelDimensions);
        StyleUtil.styleLabel(JLabelSupplier);
    }

}
