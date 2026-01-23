package GuiForms;

import dao.employeeBonusDao;
import model.EmployeeBonusSummary;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EmployeeBonus extends JFrame {

    private JPanel panel1;          // ROOT
    private JPanel mainPanel;

    private JPanel topPanel;
    private JPanel downPanel;
    private JPanel CenterPanel;
    private JPanel BoxLayout;

    private JComboBox<Integer> comboBox1; // Month
    private JComboBox<Integer> comboBox2; // Year
    private JButton CalculateBonus;

    private JTextArea summaryTextArea;
    private JScrollPane summaryScroll;

    private JLabel titleLabel1;
    private JLabel Month;
    private JLabel year;

    private JLabel BonusLeg;
    private JLabel infoLeg;

    private final employeeBonusDao dao = new employeeBonusDao();

    public EmployeeBonus() {
        setContentPane(panel1);
        setTitle("Employee Bonus");

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
        summaryTextArea.setEditable(false);
        summaryTextArea.setLineWrap(false);
        summaryTextArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        summaryTextArea.setText(
                "Select month and year, then click \"Calculate Bonus\"."
        );
    }
    private void initListeners() {
        CalculateBonus.addActionListener(e -> loadBonusSummary());
    }

    private void loadBonusSummary() {
        int month = Integer.parseInt(comboBox1.getSelectedItem().toString());
        int year  = Integer.parseInt(comboBox2.getSelectedItem().toString());

        try {
            List<EmployeeBonusSummary> list =
                    dao.getEmployeeBonusSummary(month, year);

            if (list.isEmpty()) {
                summaryTextArea.setText(
                        "No bonus data available for the selected period."
                );
                return;
            }

            summaryTextArea.setText(
                    String.format(
                            "%-25s %-15s %-10s%n",
                            "Employee",
                            "Shipments",
                            "Bonus"
                    ) +
                            "------------------------------------------------------------\n"
            );

            for (EmployeeBonusSummary e : list) {
                summaryTextArea.append(
                        String.format(
                                "%-25s %-15d %-10s%n",
                                e.getFullName(),
                                e.getShipmentsCount(),
                                e.getBonusPercent() + "%"
                        )
                );
            }

        } catch (RuntimeException ex) {
            showError(ex);
        }
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
        StyleUtil.stylePanel(mainPanel);
        StyleUtil.stylePanel(topPanel);
        StyleUtil.stylePanel(downPanel);
        StyleUtil.stylePanel(CenterPanel);

        StyleUtil.styleButton(CalculateBonus);

        StyleUtil.styleLabel(titleLabel1);

        StyleUtil.styleLabel(BonusLeg);
        StyleUtil.styleLabel(infoLeg);
    }
}
