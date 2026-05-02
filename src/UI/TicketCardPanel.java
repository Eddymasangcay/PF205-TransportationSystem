package UI;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Wide boarding-pass layout: main panel, dashed tear line, stub; rounded ticket with side notches.
 */
public class TicketCardPanel extends JPanel {

    private static final String BRAND = "PF205 Transportation";
    private static final Color CARD_FILL = new Color(245, 245, 247);
    private static final Color BORDER_LINE = new Color(185, 185, 200);
    private static final Color LABEL_BLUE = new Color(47, 111, 173);
    private static final Color MUTED_FOOTER = new Color(110, 110, 130);

    public TicketCardPanel(int bookingId, String passenger, String route, String seat,
            String date, String status, String vehicleType, int price) {
        setOpaque(false);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        String[] fromTo = parseFromTo(route, vehicleType);
        String from = fromTo[0];
        String to = fromTo[1];
        String seatDisp = (seat == null || seat.isEmpty()) ? "\u2014" : seat.toUpperCase();
        String ticketNo = String.format("%011d", Math.max(0, bookingId));
        String dateDisp = nullToDash(date);
        String statusDisp = nullToDash(status);
        String fareDisp = formatPrice(price);
        String classLine = (vehicleType != null && !vehicleType.trim().isEmpty())
                ? vehicleType.trim().toUpperCase()
                : "SERVICE";

        JPanel inner = new JPanel(new GridBagLayout());
        inner.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0;
        g.gridx = 0;
        g.weightx = 1;
        g.weighty = 1;
        g.fill = GridBagConstraints.BOTH;
        g.insets = new Insets(12, 18, 12, 8);
        JPanel main = buildMainSection(bookingId, nullToDash(passenger), from, to, dateDisp, seatDisp,
                ticketNo, statusDisp, fareDisp, classLine);
        inner.add(main, g);

        g.gridx = 1;
        g.weightx = 0;
        g.fill = GridBagConstraints.VERTICAL;
        g.insets = new Insets(12, 0, 12, 0);
        inner.add(new VerticalDashSeparator(), g);

        g.gridx = 2;
        g.weightx = 0.38;
        g.fill = GridBagConstraints.BOTH;
        g.insets = new Insets(12, 8, 12, 18);
        inner.add(buildStub(nullToDash(passenger), from, to, dateDisp, seatDisp, ticketNo, bookingId), g);

        add(inner, BorderLayout.CENTER);

        setPreferredSize(new Dimension(680, 340));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int m = 4;
        float x = m;
        float y = m;
        float rw = w - 2f * m;
        float rh = h - 2f * m;
        float arc = 16f;
        float notchR = Math.min(11f, rh / 7f);

        RoundRectangle2D rr = new RoundRectangle2D.Double(x, y, rw, rh, arc, arc);
        Area area = new Area(rr);
        double cy = y + rh / 2;
        Ellipse2D leftBite = new Ellipse2D.Double(x - notchR, cy - notchR, notchR * 2, notchR * 2);
        Ellipse2D rightBite = new Ellipse2D.Double(x + rw - notchR, cy - notchR, notchR * 2, notchR * 2);
        area.subtract(new Area(leftBite));
        area.subtract(new Area(rightBite));

        g2.setClip(area);
        g2.setColor(new Color(210, 175, 130, 55));
        g2.fillArc((int) (x - rw * 0.08), (int) (y + rh * 0.52), (int) (rw * 0.5), (int) (rh * 0.52), 25, 130);
        g2.setColor(new Color(130, 165, 210, 48));
        g2.fillArc((int) (x - rw * 0.02), (int) (y + rh * 0.58), (int) (rw * 0.42), (int) (rh * 0.42), 35, 110);
        g2.setClip(null);

        g2.setColor(CARD_FILL);
        g2.fill(area);
        g2.setColor(BORDER_LINE);
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(area);
        g2.dispose();
    }

    private JPanel buildMainSection(int bookingId, String passenger, String from, String to,
            String dateDisp, String seatDisp, String ticketNo, String statusDisp, String fareDisp,
            String classLine) {
        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.setOpaque(false);
        JLabel brand = new JLabel(BRAND);
        brand.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
        brand.setForeground(Color.BLACK);
        header.add(brand, BorderLayout.WEST);
        JPanel deco = new JPanel();
        deco.setOpaque(true);
        deco.setBackground(new Color(195, 165, 125));
        deco.setPreferredSize(new Dimension(36, 14));
        header.add(deco, BorderLayout.EAST);

        JPanel sub = new JPanel(new BorderLayout());
        sub.setOpaque(false);
        JLabel bp = new JLabel("BOARDING PASS");
        bp.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        bp.setForeground(Color.BLACK);
        JLabel cls = new JLabel(classLine);
        cls.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        cls.setForeground(Color.BLACK);
        cls.setHorizontalAlignment(SwingConstants.RIGHT);
        sub.add(bp, BorderLayout.WEST);
        sub.add(cls, BorderLayout.EAST);

        JPanel northStack = new JPanel();
        northStack.setOpaque(false);
        northStack.setLayout(new BoxLayout(northStack, BoxLayout.Y_AXIS));
        northStack.add(header);
        northStack.add(Box.createVerticalStrut(6));
        northStack.add(sub);
        root.add(northStack, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        String passLine = passenger.contains("\u2014") ? passenger : passenger.toUpperCase();
        center.add(compactQuadRow(
                "Name", passLine,
                "Ticket type", "ONEWAY",
                "Fare base", "ADULT",
                "Issued by", BRAND.toUpperCase()));
        center.add(Box.createVerticalStrut(14));

        JPanel journey = new JPanel(new BorderLayout(28, 0));
        journey.setOpaque(false);
        JPanel routeCol = new JPanel();
        routeCol.setOpaque(false);
        routeCol.setLayout(new BoxLayout(routeCol, BoxLayout.Y_AXIS));
        routeCol.add(fieldColumn("From", from));
        routeCol.add(Box.createVerticalStrut(10));
        routeCol.add(fieldColumn("To", to));
        journey.add(routeCol, BorderLayout.WEST);
        journey.add(dateTimeColumn(dateDisp), BorderLayout.CENTER);
        center.add(journey);
        center.add(Box.createVerticalStrut(12));

        JPanel stats = new JPanel(new GridLayout(1, 2, 16, 0));
        stats.setOpaque(false);
        stats.add(fieldColumn("Fare", fareDisp));
        stats.add(fieldColumn("Status", statusDisp));
        center.add(stats);
        center.add(Box.createVerticalStrut(10));

        JPanel seatRow = new JPanel(new BorderLayout(16, 0));
        seatRow.setOpaque(false);
        JPanel seatLeft = new JPanel();
        seatLeft.setOpaque(false);
        seatLeft.setLayout(new BoxLayout(seatLeft, BoxLayout.Y_AXIS));
        seatLeft.add(caption("Seat"));
        JLabel seatBig = new JLabel(seatDisp);
        seatBig.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 34));
        seatBig.setForeground(Color.BLACK);
        seatLeft.add(seatBig);
        seatLeft.add(Box.createVerticalStrut(6));
        seatLeft.add(caption("Ticket number"));
        JLabel tid = new JLabel(ticketNo);
        tid.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        tid.setForeground(Color.BLACK);
        seatLeft.add(tid);
        seatLeft.add(Box.createVerticalStrut(4));
        JLabel bid = new JLabel("Booking #" + bookingId);
        bid.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        bid.setForeground(MUTED_FOOTER);
        seatLeft.add(bid);

        seatRow.add(seatLeft, BorderLayout.CENTER);
        QrPlaceholder qr = new QrPlaceholder(bookingId, 76);
        seatRow.add(qr, BorderLayout.EAST);

        center.add(seatRow);

        root.add(center, BorderLayout.CENTER);

        JLabel foot = new JLabel("Present this pass when boarding.");
        foot.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
        foot.setForeground(MUTED_FOOTER);
        foot.setHorizontalAlignment(SwingConstants.CENTER);
        root.add(foot, BorderLayout.SOUTH);

        return root;
    }

    private JPanel buildStub(String passenger, String from, String to, String dateDisp,
            String seatDisp, String ticketNo, int bookingId) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JLabel n = new JLabel(passenger.toUpperCase());
        n.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        n.setForeground(Color.BLACK);
        n.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(n);
        p.add(Box.createVerticalStrut(10));

        p.add(stubLine(caption("From"), valueSmall(from)));
        p.add(Box.createVerticalStrut(6));
        p.add(stubLine(caption("To"), valueSmall(to)));
        p.add(Box.createVerticalStrut(8));

        JPanel dt = new JPanel(new BorderLayout(4, 0));
        dt.setOpaque(false);
        dt.setAlignmentX(Component.LEFT_ALIGNMENT);
        dt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel clock = new JLabel("\u231A");
        clock.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        clock.setForeground(LABEL_BLUE);
        dt.add(clock, BorderLayout.WEST);
        JPanel dCol = new JPanel();
        dCol.setOpaque(false);
        dCol.setLayout(new BoxLayout(dCol, BoxLayout.Y_AXIS));
        dCol.add(caption("Date"));
        dCol.add(valueSmall(dateDisp));
        dt.add(dCol, BorderLayout.CENTER);
        JPanel timeCol = new JPanel();
        timeCol.setOpaque(false);
        timeCol.setLayout(new BoxLayout(timeCol, BoxLayout.Y_AXIS));
        timeCol.add(caption("Time"));
        timeCol.add(valueSmall("\u2014"));
        dt.add(timeCol, BorderLayout.EAST);
        p.add(dt);
        p.add(Box.createVerticalStrut(10));

        p.add(stubLine(caption("Ticket number"), valueSmall(ticketNo)));
        p.add(Box.createVerticalStrut(6));
        p.add(stubLine(caption("Seat"), valueSmall(seatDisp)));

        p.add(Box.createVerticalGlue());
        JPanel qrRow = new JPanel(new BorderLayout());
        qrRow.setOpaque(false);
        qrRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        qrRow.add(new QrPlaceholder(bookingId + 7919, 56), BorderLayout.WEST);
        p.add(qrRow);

        return p;
    }

    private JPanel stubLine(JLabel cap, JLabel val) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(cap);
        left.add(val);
        row.add(left, BorderLayout.CENTER);
        return row;
    }

    private JPanel compactQuadRow(String c1, String v1, String c2, String v2, String c3, String v3, String c4, String v4) {
        JPanel grid = new JPanel(new GridLayout(2, 4, 10, 4));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.add(caption(c1));
        grid.add(caption(c2));
        grid.add(caption(c3));
        grid.add(caption(c4));
        grid.add(valueSmall(v1));
        grid.add(valueSmall(v2));
        grid.add(valueSmall(v3));
        grid.add(valueSmall(v4));
        return grid;
    }

    private JPanel fieldColumn(String cap, String val) {
        JPanel c = new JPanel();
        c.setOpaque(false);
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.add(caption(cap));
        c.add(valueMedium(val));
        return c;
    }

    private JPanel dateTimeColumn(String dateDisp) {
        JPanel c = new JPanel(new BorderLayout(6, 0));
        c.setOpaque(false);
        JLabel clock = new JLabel("\u231A");
        clock.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        clock.setForeground(LABEL_BLUE);
        c.add(clock, BorderLayout.WEST);
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.add(caption("Date"));
        col.add(valueMedium(dateDisp));
        col.add(Box.createVerticalStrut(4));
        col.add(caption("Time"));
        col.add(valueMedium("\u2014"));
        c.add(col, BorderLayout.CENTER);
        return c;
    }

    private static JLabel caption(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        l.setForeground(LABEL_BLUE);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private static JLabel valueSmall(String text) {
        JLabel l = new JLabel(nullToDash(text));
        l.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        l.setForeground(Color.BLACK);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private static JLabel valueMedium(String text) {
        JLabel l = new JLabel(nullToDash(text));
        l.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        l.setForeground(Color.BLACK);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    /** Split route "Vehicle Origin - Destination" into from / to. */
    private static String[] parseFromTo(String route, String vehicleType) {
        if (route == null || route.trim().isEmpty()) {
            return new String[]{"\u2014", "\u2014"};
        }
        String r = route.trim();
        int dash = r.indexOf(" - ");
        if (dash < 0) {
            return new String[]{r, "\u2014"};
        }
        String left = r.substring(0, dash).trim();
        String right = r.substring(dash + 3).trim();
        if (vehicleType != null && !vehicleType.isEmpty()) {
            String vt = vehicleType.trim();
            if (left.startsWith(vt)) {
                left = left.substring(vt.length()).trim();
            }
        }
        if (left.isEmpty()) {
            left = "\u2014";
        }
        if (right.isEmpty()) {
            right = "\u2014";
        }
        return new String[]{left, right};
    }

    private static String formatPrice(int price) {
        return price <= 0 ? "\u2014" : price + " php";
    }

    private static String nullToDash(String s) {
        if (s == null || s.trim().isEmpty()) {
            return "\u2014";
        }
        return s;
    }

    private static final class VerticalDashSeparator extends JPanel {
        VerticalDashSeparator() {
            setOpaque(false);
            setPreferredSize(new Dimension(12, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            float[] dash = {4f, 5f};
            g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0f));
            g2.setColor(new Color(160, 160, 178));
            int cx = getWidth() / 2;
            g2.drawLine(cx, 8, cx, Math.max(8, getHeight() - 8));
            g2.dispose();
        }
    }

    private static final class QrPlaceholder extends JPanel {
        private final int seed;
        private final int modules;

        QrPlaceholder(int seed, int pxSize) {
            this.seed = seed;
            this.modules = 15;
            setOpaque(false);
            setPreferredSize(new Dimension(pxSize, pxSize));
            setMinimumSize(new Dimension(pxSize, pxSize));
            setMaximumSize(new Dimension(pxSize, pxSize));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth();
            int h = getHeight();
            int cell = Math.max(1, Math.min(w, h) / modules);
            int px = cell * modules;
            int ox = (w - px) / 2;
            int oy = (h - px) / 2;
            for (int i = 0; i < modules; i++) {
                for (int j = 0; j < modules; j++) {
                    int hash = (i * 31 + j * 17 + seed * 13) & 0x7fffffff;
                    boolean black = (hash % 5) != 0;
                    g.setColor(black ? Color.BLACK : Color.WHITE);
                    g.fillRect(ox + i * cell, oy + j * cell, cell, cell);
                }
            }
            g.setColor(Color.BLACK);
            g.drawRect(ox, oy, px - 1, px - 1);
        }
    }
}
