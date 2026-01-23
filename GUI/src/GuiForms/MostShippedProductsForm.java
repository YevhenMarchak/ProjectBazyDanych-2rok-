package GuiForms;

import dao.productStatsDao;
import model.MostShippedProduct;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MostShippedProductsForm extends JFrame {

    private JPanel panel1;
    private JPanel mainPanel;

    private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel downPanel;

    private JLabel minQtyLabel;
    private JTextField minQuantityField;
    private JButton calculateButton;

    private JTextArea summaryTextArea;
    private JScrollPane summaryScroll;

    private JLabel downLabel;
    private JLabel mostSPLabel;

    private final productStatsDao dao = new productStatsDao();

    public MostShippedProductsForm() {
        setContentPane(panel1);
        setTitle("Most Shipped Products");

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(
                (int) (screen.width * 0.6),
                (int) (screen.height * 0.6)
        );
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initDefaults();
        initListeners();
        applyStyles();
    }
    private void initDefaults() {
        minQuantityField.setText("1000");

        summaryTextArea.setEditable(false);
        summaryTextArea.setLineWrap(true);
        summaryTextArea.setWrapStyleWord(true);
        summaryTextArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        summaryTextArea.setText(
                "Enter minimum quantity and click \"Calculate\"."
        );
    }
    private void initListeners() {
        calculateButton.addActionListener(e -> loadData());
    }
    private void loadData() {
        double minQty;

        try {
            minQty = Double.parseDouble(minQuantityField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid number",
                    "Input error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            List<MostShippedProduct> list =
                    dao.getMostShippedProducts(minQty);

            if (list.isEmpty()) {
                summaryTextArea.setText(
                        "No products exceed the specified quantity threshold."
                );
                return;
            }

            summaryTextArea.setText(
                    "Most Shipped Products\n" +
                            "---------------------\n"
            );

            for (MostShippedProduct p : list) {
                summaryTextArea.append(
                        "Product: " + p.getProductName() + "\n" +
                                "Total shipped quantity: " + p.getTotalQuantity() + "\n\n"
                );
            }

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Database error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private void applyStyles() {
        StyleUtil.stylePanel(panel1);
        StyleUtil.stylePanel(mainPanel);
        StyleUtil.stylePanel(topPanel);
        StyleUtil.stylePanel(centerPanel);
        StyleUtil.stylePanel(downPanel);

        StyleUtil.styleButton(calculateButton);
        StyleUtil.styleLabel(minQtyLabel);
        StyleUtil.styleLabel(downLabel);
    }
}
