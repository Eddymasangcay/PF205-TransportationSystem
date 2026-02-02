package UserInternalPages;

import Configuration.ConnectionConfig;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class UserTransportationPage extends InternalPageFrame {
    
    Color navcolor = new Color(153,153,255);
    Color bodycolor = new Color(204,204,255);
    Color staycolor = new Color(204,204,255);

    private final DefaultTableModel tableModel;
    private final List<Integer> routeIds = new ArrayList<>();
    private final int currentUserId;
    private String currentUserName;
    private static final String[] ROUTE_COLUMNS = {"ID", "Vehicle Type", "Price", "Origin", "Destination"};

    public UserTransportationPage(int currentUserId) {
        this.currentUserId = currentUserId;
        initComponents();
        tableModel = (DefaultTableModel) jTableBookings.getModel();
        tableModel.setColumnIdentifiers(ROUTE_COLUMNS);
        loadCurrentUserName();
        setupPanelListeners();
        setupSearchByID();
        loadRoutesFromDb(null);
    }

    private void loadCurrentUserName() {
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            try (PreparedStatement ps = conn.prepareStatement("SELECT name FROM users WHERE u_id = ?")) {
                ps.setInt(1, currentUserId);
                try (ResultSet rs = ps.executeQuery()) {
                    currentUserName = rs.next() ? rs.getString("name") : "Passenger";
                }
            }
        } catch (SQLException e) {
            currentUserName = "Passenger";
        } finally {
            ConnectionConfig.close(conn);
        }
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private void loadRoutesFromDb(Integer filterById) {
        tableModel.setRowCount(0);
        routeIds.clear();
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            String sql = "SELECT v_id, v_type, COALESCE(v_price, 0) AS v_price, origin, destination FROM routes";
            if (filterById != null) {
                sql += " WHERE v_id = ?";
            }
            sql += " ORDER BY v_id";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (filterById != null) {
                    ps.setInt(1, filterById);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        routeIds.add(rs.getInt("v_id"));
                        tableModel.addRow(new Object[]{
                            rs.getInt("v_id"),
                            nullToEmpty(rs.getString("v_type")),
                            rs.getInt("v_price"),
                            nullToEmpty(rs.getString("origin")),
                            nullToEmpty(rs.getString("destination"))
                        });
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load transportation: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectionConfig.close(conn);
        }
    }

    private void setupSearchByID() {
        Search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { performSearchByID(); }
            @Override
            public void removeUpdate(DocumentEvent e) { performSearchByID(); }
            @Override
            public void changedUpdate(DocumentEvent e) { performSearchByID(); }
        });
    }

    private void performSearchByID() {
        String text = Search.getText().trim();
        if (text.isEmpty()) {
            loadRoutesFromDb(null);
            return;
        }
        try {
            int id = Integer.parseInt(text);
            loadRoutesFromDb(id);
        } catch (NumberFormatException ignored) {
            loadRoutesFromDb(null);
        }
    }

    private void setupPanelListeners() {
        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
        BookTransPanel.setCursor(handCursor);
        BookTransPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Book();
            }
        });
    }

    private void Book() {
        int row = jTableBookings.getSelectedRow();
        if (row < 0 || row >= tableModel.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Please select a route to book.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String vehicleType = String.valueOf(tableModel.getValueAt(row, 1));
        String origin = String.valueOf(tableModel.getValueAt(row, 3));
        String destination = String.valueOf(tableModel.getValueAt(row, 4));
        Object priceVal = tableModel.getValueAt(row, 2);
        int price = priceVal instanceof Number ? ((Number) priceVal).intValue() : 0;
        JTextField seatField = new JTextField(10);
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new javax.swing.JLabel("Route:"));
        panel.add(new javax.swing.JLabel(nullToEmpty(origin) + " → " + nullToEmpty(destination)));
        panel.add(new javax.swing.JLabel("Price:"));
        panel.add(new javax.swing.JLabel(price + ""));
        panel.add(new javax.swing.JLabel("Passenger:"));
        panel.add(new javax.swing.JLabel(currentUserName));
        panel.add(new javax.swing.JLabel("Seat (optional):"));
        panel.add(seatField);
        if (JOptionPane.showConfirmDialog(this, panel, "Book Route", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }
        String seat = seatField.getText().trim();
        int vId = (Integer) tableModel.getValueAt(row, 0);
        String routeDisplay = vehicleType + " " + nullToEmpty(origin) + " - " + nullToEmpty(destination);
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO bookings (passenger, passenger_id, v_type, v_id, route, seat, status) VALUES (?, ?, ?, ?, ?, ?, 'Pending')")) {
                ps.setString(1, currentUserName);
                ps.setInt(2, currentUserId);
                ps.setString(3, vehicleType);
                ps.setInt(4, vId);
                ps.setString(5, routeDisplay);
                ps.setString(6, seat);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Booking created successfully.", "Book", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to create booking: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectionConfig.close(conn);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableBookings = new javax.swing.JTable();
        Search = new javax.swing.JTextField();
        BookTransPanel = new javax.swing.JPanel();
        BookTransText = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        SearchSep = new javax.swing.JSeparator();

        mainPanel.setBackground(new java.awt.Color(204, 204, 255));
        mainPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        javax.swing.GroupLayout jScrollPane1Layout = new javax.swing.GroupLayout(jScrollPane1.getViewport());
        jScrollPane1.getViewport().setLayout(jScrollPane1Layout);
        jScrollPane1Layout.setHorizontalGroup(
            jScrollPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTableBookings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jScrollPane1Layout.setVerticalGroup(
            jScrollPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTableBookings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        mainPanel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 740, 310));

        Search.setBackground(new java.awt.Color(204, 204, 255));
        Search.setForeground(new java.awt.Color(255, 255, 255));
        Search.setText("Enter Transportation ID to search");
        Search.setBorder(null);
        Search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchActionPerformed(evt);
            }
        });
        mainPanel.add(Search, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, 660, 30));

        BookTransPanel.setBackground(new java.awt.Color(204, 204, 255));
        BookTransPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BookTransPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                BookTransPanelMouseExited(evt);
            }
        });

        BookTransText.setBackground(new java.awt.Color(204, 204, 255));
        BookTransText.setFont(new java.awt.Font("Bahnschrift", 1, 11)); // NOI18N
        BookTransText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        BookTransText.setText("BOOK");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-booking-48.png"))); // NOI18N

        javax.swing.GroupLayout BookTransPanelLayout = new javax.swing.GroupLayout(BookTransPanel);
        BookTransPanel.setLayout(BookTransPanelLayout);
        BookTransPanelLayout.setHorizontalGroup(
            BookTransPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BookTransPanelLayout.createSequentialGroup()
                .addGroup(BookTransPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                    .addComponent(BookTransText, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        BookTransPanelLayout.setVerticalGroup(
            BookTransPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BookTransPanelLayout.createSequentialGroup()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BookTransText, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(BookTransPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 80));
        mainPanel.add(SearchSep, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 70, 670, 10));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 790, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 415, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchActionPerformed
        performSearchByID();
    }//GEN-LAST:event_SearchActionPerformed

    private void BookTransPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BookTransPanelMouseEntered
        BookTransPanel.setBackground(navcolor);
    }//GEN-LAST:event_BookTransPanelMouseEntered

    private void BookTransPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BookTransPanelMouseExited
        BookTransPanel.setBackground(bodycolor);
    }//GEN-LAST:event_BookTransPanelMouseExited

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BookTransPanel;
    private javax.swing.JLabel BookTransText;
    private javax.swing.JTextField Search;
    private javax.swing.JSeparator SearchSep;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableBookings;
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables
}
