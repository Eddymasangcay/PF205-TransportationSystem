package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Modal dialog with scrollable content and a panel-as-button to close (no JButton).
 */
public final class DocumentDialog {

    public static final Color SHELL_BG = new Color(204, 204, 255);
    public static final Color ACCENT = new Color(153, 153, 255);
    public static final Color CLOSE_IDLE = new Color(204, 204, 255);

    private DocumentDialog() {
    }

    public static void show(Component parent, String title, JComponent content, int width, int height) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = owner != null
                ? new JDialog(owner, title, java.awt.Dialog.ModalityType.APPLICATION_MODAL)
                : new JDialog((java.awt.Frame) null, title, true);
        dialog.setLayout(new BorderLayout());

        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(SHELL_BG);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        scroll.getViewport().setBackground(SHELL_BG);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        shell.add(scroll, BorderLayout.CENTER);

        JPanel closePanel = createCloseStrip(dialog);
        shell.add(closePanel, BorderLayout.SOUTH);

        dialog.add(shell);
        dialog.setPreferredSize(new Dimension(width, height));
        dialog.pack();
        dialog.setMinimumSize(new Dimension(Math.min(width, 420), 280));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private static JPanel createCloseStrip(JDialog dialog) {
        JPanel strip = new JPanel(new BorderLayout());
        strip.setOpaque(true);
        strip.setBackground(CLOSE_IDLE);
        strip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(180, 180, 220)),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        JLabel label = new JLabel("CLOSE", SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 12f));
        label.setForeground(new Color(60, 60, 90));
        strip.add(label, BorderLayout.CENTER);

        Cursor hand = new Cursor(Cursor.HAND_CURSOR);
        strip.setCursor(hand);
        label.setCursor(hand);

        MouseAdapter hoverClose = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                strip.setBackground(ACCENT);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                strip.setBackground(CLOSE_IDLE);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                dialog.dispose();
            }
        };
        strip.addMouseListener(hoverClose);
        label.addMouseListener(hoverClose);

        return strip;
    }
}
