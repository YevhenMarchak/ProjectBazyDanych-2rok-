package GuiForms;

import javax.swing.*;
import java.awt.*;

public class StyleUtil {

    public static void styleButton(JButton button) {
        Color baseColor = new Color(102, 0, 204);
        Color hoverColor = new Color(80, 0, 160);

        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover efekt
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });
    }

    public static void stylePanel(JPanel panel) {
        panel.setBackground(new Color(234, 230, 248));
    }

    public static void styleLabel(JLabel label) {
        label.setForeground(new Color(60, 60, 60));
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
    }

}
