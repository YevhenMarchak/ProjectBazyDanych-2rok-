package GuiForms;

import dao.locationsDao;
import model.locations;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LocationsForm extends JFrame {

    private JPanel JLabelMain;
    private JScrollPane Scrl1;
    private JTable table1;

    private JTextField locationCodeField;
    private JTextField locationTypeField;
    private JTextField maxCapacityField;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JPanel panel1;
    private JPanel JPanel1;
    private JPanel JPanel2;
    private JPanel JPanel4;

    private JLabel JLabelLocCode;
    private JLabel JLabelLocType;
    private JLabel JLabelMaxCap;
    private JLabel JLabelSupplier;

    private final locationsDao dao = new locationsDao();
    private DefaultTableModel model;

    public LocationsForm() {
        setContentPane(JLabelMain);
        setTitle("Locations");

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(
                (int) (screen.width * 0.75),
                (int) (screen.height * 0.75)
        );
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initTable();
        loadLocations();
        initListeners();
        applyStyles();
    }

    private void initTable() {
        model = new DefaultTableModel(
                new Object[]{"ID", "Code", "Type", "Max capacity"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table1.setModel(model);
    }
    private void loadLocations() {
        model.setRowCount(0);
        List<locations> list = dao.getAllLocations();

        for (locations l : list) {
            model.addRow(new Object[]{
                    l.getLocation_id(),
                    l.getLocation_code(),
                    l.getLocation_type(),
                    l.getMax_capacity()
            });
        }
    }
    private void initListeners() {

        addButton.addActionListener(e -> addLocation());
        updateButton.addActionListener(e -> updateLocation());
        deleteButton.addActionListener(e -> deleteLocation());
        clearButton.addActionListener(e -> clearForm());

        table1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromTable();
            }
        });
    }

    private void addLocation() {
        try {
            locations l = new locations(
                    0,
                    locationCodeField.getText(),
                    locationTypeField.getText(),
                    Double.parseDouble(maxCapacityField.getText())
            );

            dao.addLocation(l);
            loadLocations();
            clearForm();

        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void updateLocation() {
        int row = table1.getSelectedRow();
        if (row == -1) return;

        try {
            int id = (int) model.getValueAt(row, 0);

            locations l = new locations(
                    id,
                    locationCodeField.getText(),
                    locationTypeField.getText(),
                    Double.parseDouble(maxCapacityField.getText())
            );

            dao.updateLocation(l);
            loadLocations();
            clearForm();

        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void deleteLocation() {
        int row = table1.getSelectedRow();
        if (row == -1) return;

        try {
            int id = (int) model.getValueAt(row, 0);
            dao.deleteLocation(id);

            loadLocations();
            clearForm();

        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void fillFormFromTable() {
        int row = table1.getSelectedRow();
        if (row == -1) return;

        locationCodeField.setText(model.getValueAt(row, 1).toString());
        locationTypeField.setText(model.getValueAt(row, 2).toString());
        maxCapacityField.setText(model.getValueAt(row, 3).toString());
    }

    private void clearForm() {
        locationCodeField.setText("");
        locationTypeField.setText("");
        maxCapacityField.setText("");
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

        StyleUtil.stylePanel(JLabelMain);
        StyleUtil.stylePanel(panel1);
        StyleUtil.stylePanel(JPanel1);
        StyleUtil.stylePanel(JPanel2);
        StyleUtil.stylePanel(JPanel4);

        StyleUtil.styleButton(addButton);
        StyleUtil.styleButton(updateButton);
        StyleUtil.styleButton(deleteButton);
        StyleUtil.styleButton(clearButton);

        StyleUtil.styleLabel(JLabelLocCode);
        StyleUtil.styleLabel(JLabelLocType);
        StyleUtil.styleLabel(JLabelMaxCap);
        StyleUtil.styleLabel(JLabelSupplier);
    }

}
