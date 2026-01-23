package GuiForms;

import javax.swing.*;

public class MainWindowForm extends JFrame {

    private JPanel Jpanel1;
    private JPanel JPanelMain;

    private JButton clientsButton;
    private JButton categoriesButton;
    private JButton employeesButton;

    private JButton productsButton;
    private JButton locationsButton;
    private JButton inventoryButton;

    private JButton suppliersButton;
    private JButton shipmentsButton;
    private JButton receiptsButton;

    private JPanel JPanel1;
    private JPanel JPanel2;
    private JPanel JPanel3;
    private JButton ProblemButton;
    private JPanel JPanel4;
    private JButton percentageButton;
    private JLabel textPytania;
    private JButton mostShippedButton;
    private JButton productivityButton;
    public MainWindowForm() {
        setContentPane(Jpanel1);
        setTitle("Warehouse Management System");

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        applyStyles();
        initListeners();
    }
    private void initListeners() {

        clientsButton.addActionListener(e ->
                new ClientsForm().setVisible(true));

        categoriesButton.addActionListener(e ->
                new CategoriesForm().setVisible(true));

        employeesButton.addActionListener(e ->
                new EmployeesForm().setVisible(true));

        productsButton.addActionListener(e ->
                new ProductsForm().setVisible(true));

        locationsButton.addActionListener(e ->
                new LocationsForm().setVisible(true));

        inventoryButton.addActionListener(e ->
                new InventoryForm().setVisible(true));

        suppliersButton.addActionListener(e ->
                new SuppliersForm().setVisible(true));

        shipmentsButton.addActionListener(e ->
                new ShipmentsForm().setVisible(true));

        receiptsButton.addActionListener(e ->
                new ReceiptsForm().setVisible(true));
        ProblemButton.addActionListener(e ->
                new EmployeeBonus().setVisible(true));
        percentageButton.addActionListener(e ->
                new PercentageForm().setVisible(true));
        mostShippedButton.addActionListener(e ->
                new MostShippedProductsForm().setVisible(true));
        productivityButton.addActionListener(e ->
                new EmployeeProductivityForm().setVisible(true));
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new MainWindowForm().setVisible(true)
        );
    }

    private void applyStyles() {

        StyleUtil.stylePanel(Jpanel1);
        StyleUtil.stylePanel(JPanelMain);
        StyleUtil.stylePanel(JPanel1);
        StyleUtil.stylePanel(JPanel2);
        StyleUtil.stylePanel(JPanel3);
        StyleUtil.stylePanel(JPanel4);

        StyleUtil.styleButton(clientsButton);
        StyleUtil.styleButton(categoriesButton);
        StyleUtil.styleButton(employeesButton);

        StyleUtil.styleButton(productsButton);
        StyleUtil.styleButton(locationsButton);
        StyleUtil.styleButton(inventoryButton);

        StyleUtil.styleButton(suppliersButton);
        StyleUtil.styleButton(shipmentsButton);
        StyleUtil.styleButton(receiptsButton);

        StyleUtil.styleButton(ProblemButton);
        StyleUtil.styleButton(percentageButton);
        StyleUtil.styleButton(mostShippedButton);
        StyleUtil.styleButton(productivityButton);
    }

}


