package GuiForms;

import dao.employeeProductivityDao;
import model.EmployeeProductivitySummary;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EmployeeProductivityForm extends JFrame {

    private JPanel panel1;
    private JPanel mainPanel;

    private JPanel topPanel;
    private JPanel centerPanel;

    private JLabel JLabelText;
    private JButton calculateProductivityButton;

    private JLabel productivity;
    private JTextArea textArea;
    private JScrollPane scrollPanel;

    private final employeeProductivityDao dao = new employeeProductivityDao();

    public EmployeeProductivityForm() {
        setContentPane(panel1);
        setTitle("Employee Productivity");

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
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        textArea.setText(
                "Click \"Calculate Productivity\" to generate the report."
        );
    }
    private void initListeners() {
        calculateProductivityButton.addActionListener(e -> loadData());
    }

    private void loadData() {
        try {
            List<EmployeeProductivitySummary> list =
                    dao.getEmployeeProductivity();

            if (list.isEmpty()) {
                textArea.setText("No productivity data available.");
                return;
            }

            textArea.setText(
                    String.format(
                            "%-25s %-15s %-10s%n",
                            "Employee",
                            "Clients",
                            "Level"
                    ) +
                            "-------------------------------------------------------\n"
            );

            for (EmployeeProductivitySummary e : list) {
                textArea.append(
                        String.format(
                                "%-25s %-15d %-10s%n",
                                e.getFullName(),
                                e.getClientsCount(),
                                e.getProductivityLevel()
                        )
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

        StyleUtil.styleButton(calculateProductivityButton);
        StyleUtil.styleLabel(JLabelText);
        StyleUtil.styleLabel(productivity);
    }
}
