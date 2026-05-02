package UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 * Printable-style receipt card for completed-trip receipts.
 */
public class ReceiptCardPanel extends JPanel {

    private static final Color CARD_BG = new Color(252, 252, 255);
    private static final Color MUTED = new Color(100, 100, 120);
    private static final Color TITLE = new Color(40, 40, 70);

    public ReceiptCardPanel(int receiptId, int bookingId, String origin, String destination,
            String seat, String date, String usernameOrNull) {
        setOpaque(true);
        setBackground(CARD_BG);
        Border outer = BorderFactory.createLineBorder(new Color(190, 190, 215), 1);
        Border inner = BorderFactory.createEmptyBorder(14, 16, 14, 16);
        setBorder(BorderFactory.createCompoundBorder(outer, inner));

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 8, 0);

        JLabel header = new JLabel("RECEIPT");
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
        header.setForeground(TITLE);
        add(header, gbc);
        gbc.gridy = 1;

        JLabel sub = new JLabel("Trip completed — thank you for riding with us");
        sub.setFont(sub.getFont().deriveFont(Font.PLAIN, 11f));
        sub.setForeground(MUTED);
        gbc.insets = new Insets(0, 0, 10, 0);
        add(sub, gbc);
        gbc.gridy = 2;

        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 12, 0);
        add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(2, 0, 4, 16);

        int row = 3;
        row = addRow(gbc, row, "Receipt no.", String.valueOf(receiptId));
        row = addRow(gbc, row, "Booking no.", String.valueOf(bookingId));
        if (usernameOrNull != null && !usernameOrNull.isEmpty()) {
            row = addRow(gbc, row, "Passenger", usernameOrNull);
        }
        row = addRow(gbc, row, "From", nullToDash(origin));
        row = addRow(gbc, row, "To", nullToDash(destination));
        row = addRow(gbc, row, "Seat", seat == null || seat.isEmpty() ? "—" : seat);
        addRow(gbc, row, "Date", nullToDash(date));
    }

    private int addRow(GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel l = new JLabel(label);
        l.setFont(l.getFont().deriveFont(Font.PLAIN, 11f));
        l.setForeground(MUTED);
        add(l, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JLabel v = new JLabel(value);
        v.setFont(v.getFont().deriveFont(Font.BOLD, 12f));
        v.setForeground(TITLE);
        add(v, gbc);
        return row + 1;
    }

    private static String nullToDash(String s) {
        if (s == null || s.trim().isEmpty()) {
            return "—";
        }
        return s;
    }
}
