package GuiForms;

import dao.receiptDao;

import javax.swing.*;
import java.awt.*;

public class PercentageForm extends JFrame {

    private JPanel panel1;
    private JPanel mainPanel;

    private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel downPanel;

    private JLabel JLabelReceipt;
    private JTextField textFieldReceipt;
    private JButton CalculateButton;

    private JLabel ReceiptJLabelCenter;
    private JLabel JLabelCenterResult;
    private JLabel JLabelStatus;
    private JLabel DownJLabel;

    private final receiptDao dao = new receiptDao();
    public PercentageForm() {
        setContentPane(panel1);
        setTitle("Receipt Completion Percentage");

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(
                (int) (screen.width * 0.5),
                (int) (screen.height * 0.5)
        );
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initDefaults();
        initListeners();
        applyStyles();
    }
    private void initDefaults() {
        JLabelCenterResult.setText("—");
        JLabelStatus.setText("Status: —");

        ReceiptJLabelCenter.setFont(new Font("Arial", Font.BOLD, 18));
        JLabelCenterResult.setFont(new Font("Arial", Font.BOLD, 40));
        JLabelStatus.setFont(new Font("Arial", Font.PLAIN, 14));

        ReceiptJLabelCenter.setHorizontalAlignment(SwingConstants.CENTER);
        JLabelCenterResult.setHorizontalAlignment(SwingConstants.CENTER);
        JLabelStatus.setHorizontalAlignment(SwingConstants.CENTER);

        ReceiptJLabelCenter.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabelCenterResult.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabelStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
    private void initListeners() {
        CalculateButton.addActionListener(e -> calculateCompletion());
    }

    private void calculateCompletion() {
        long receiptId;

        try {
            receiptId = Long.parseLong(textFieldReceipt.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid Receipt ID",
                    "Input error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            double percent = dao.getReceiptCompletionPercentage(receiptId);

            JLabelCenterResult.setText(
                    String.format("%.2f %%", percent)
            );

            String status =
                    percent == 100 ? "COMPLETED"
                            : percent >= 80 ? "PARTIALLY RECEIVED"
                            : "INCOMPLETE";

            JLabelStatus.setText("Status: " + status);

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

        StyleUtil.styleButton(CalculateButton);

        StyleUtil.styleLabel(JLabelReceipt);
        StyleUtil.styleLabel(ReceiptJLabelCenter);
        StyleUtil.styleLabel(JLabelStatus);
        StyleUtil.styleLabel(DownJLabel);
    }
}
