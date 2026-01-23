package GuiForms;

import dao.categoriesDao;
import model.categories;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CategoriesForm extends JFrame {

    private JPanel panel1;
    private JPanel JPanelMain;

    private JTable table1;
    private JTextField nameField;
    private JTextField DescField;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JLabel JLabelSupplier;

    private JPanel JPanel1;
    private JPanel JPanel2;
    private JPanel JPanel4;

    private JLabel JLabelCompName;
    private JLabel JLabelAddress;

    private DefaultTableModel tableModel;
    private final categoriesDao dao = new categoriesDao();

    public CategoriesForm() {
        setContentPane(panel1);
        setTitle("Categories");

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(
                (int) (screen.width * 0.75),
                (int) (screen.height * 0.75)
        );
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        applyStyles();
        initTable();
        loadData();
        initListeners();
    }

    private void initTable() {
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Description"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table1.setModel(tableModel);
    }

    private void loadData() {
        tableModel.setRowCount(0);

        List<categories> list = dao.getAllCategories();
        for (categories c : list) {
            tableModel.addRow(new Object[]{
                    c.getCategory_id(),
                    c.getName(),
                    c.getDescription()
            });
        }
    }

    private void initListeners() {

        addButton.addActionListener(e -> addCategory());
        updateButton.addActionListener(e -> updateCategory());
        deleteButton.addActionListener(e -> deleteCategory());
        clearButton.addActionListener(e -> clearForm());

        table1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromTable();
            }
        });
    }


    private void addCategory() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required");
            return;
        }

        try {
            categories c = new categories();
            c.setName(nameField.getText());
            c.setDescription(DescField.getText());

            dao.addCategory(c);
            loadData();
            clearForm();

        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void updateCategory() {
        int row = table1.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select category first");
            return;
        }

        try {
            categories c = new categories();
            c.setCategory_id((int) tableModel.getValueAt(row, 0));
            c.setName(nameField.getText());
            c.setDescription(DescField.getText());

            dao.updateCategory(c);
            loadData();
            clearForm();

        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void deleteCategory() {
        int row = table1.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select category first");
            return;
        }

        try {
            int id = (int) tableModel.getValueAt(row, 0);
            dao.deleteCategory(id);

            loadData();
            clearForm();

        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void fillFormFromTable() {
        int row = table1.getSelectedRow();
        if (row == -1) return;

        nameField.setText(tableModel.getValueAt(row, 1).toString());

        Object desc = tableModel.getValueAt(row, 2);
        DescField.setText(desc != null ? desc.toString() : "");
    }

    private void clearForm() {
        nameField.setText("");
        DescField.setText("");
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
        StyleUtil.styleLabel(JLabelSupplier);
    }

}
