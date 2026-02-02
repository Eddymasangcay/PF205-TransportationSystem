package AdminInternalPages;

import Configuration.ConnectionConfig;
import Configuration.ReceiptUtil;
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

public class Bookings extends InternalPageFrame {
    
    Color navcolor = new Color(153,153,255);
    Color bodycolor = new Color(204,204,255);
    Color staycolor = new Color(204,204,255);
    
    private final DefaultTableModel tableModel;
    private final List<Integer> bookingIds = new ArrayList<>();
    private static final String[] STATUS_OPTIONS = {"Pending", "In-Transit", "Stopped", "Arrived", "Cancelled"};
    private static final String[] BOOKING_COLUMNS = {"ID", "Passenger", "Route", "Date", "Seat", "Status"};

    public Bookings() {
        initComponents();
        tableModel = (DefaultTableModel) jTableBookings.getModel();
        tableModel.setColumnIdentifiers(BOOKING_COLUMNS);
        setupPanelListeners();
        setupSearchByVehicleType();
        loadBookingsFromDb(null);
    }

    private void loadBookingsFromDb(String filterByVehicleType) {
        tableModel.setRowCount(0);
        bookingIds.clear();
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            String sql = "SELECT b_id, passenger, route, date, seat, status FROM bookings";
            if (filterByVehicleType != null && !filterByVehicleType.isEmpty()) {
                sql += " WHERE route LIKE ?";
            }
            sql += " ORDER BY b_id";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (filterByVehicleType != null && !filterByVehicleType.isEmpty()) {
                    ps.setString(1, "%" + filterByVehicleType.trim() + "%");
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        bookingIds.add(rs.getInt("b_id"));
                        tableModel.addRow(new Object[]{
                            rs.getInt("b_id"),
                            rs.getString("passenger"),
                            rs.getString("route"),
                            rs.getString("date"),
                            rs.getString("seat"),
                            rs.getString("status") != null ? rs.getString("status") : "Pending"
                        });
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load bookings: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectionConfig.close(conn);
        }
    }

    private void setupSearchByVehicleType() {
        Search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { performSearchByVehicleType(); }
            @Override
            public void removeUpdate(DocumentEvent e) { performSearchByVehicleType(); }
            @Override
            public void changedUpdate(DocumentEvent e) { performSearchByVehicleType(); }
        });
    }

    private void performSearchByVehicleType() {
        String text = Search.getText().trim();
        loadBookingsFromDb(text.isEmpty() ? null : text);
    }

    private void setupPanelListeners() {
        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

        EditPanel.setCursor(handCursor);
        EditPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                performEdit();
            }
        });

        DeletePanel.setCursor(handCursor);
        DeletePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                performDelete();
            }
        });

        UpStatusPanel.setCursor(handCursor);
        UpStatusPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                performUpdateStatus();
            }
        });

        // Add View Receipts panel programmatically
        setupViewReceiptsPanel(handCursor);
    }

    private javax.swing.JPanel ViewReceiptsPanel;

    private void setupViewReceiptsPanel(Cursor handCursor) {
        ViewReceiptsPanel = new javax.swing.JPanel();
        ViewReceiptsPanel.setBackground(bodycolor);
        ViewReceiptsPanel.setCursor(handCursor);
        
        javax.swing.JLabel receiptText = new javax.swing.JLabel("RECEIPTS");
        receiptText.setFont(new java.awt.Font("Tahoma", 1, 11));
        receiptText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        
        javax.swing.JLabel receiptIcon = new javax.swing.JLabel();
        receiptIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        receiptIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-booking-48.png")));
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(ViewReceiptsPanel);
        ViewReceiptsPanel.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(receiptText, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
            .addComponent(receiptIcon, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(receiptIcon, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(receiptText))
        );
        
        mainPanel.add(ViewReceiptsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 80, 70));
        
        ViewReceiptsPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                performViewAllReceipts();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                ViewReceiptsPanel.setBackground(navcolor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                ViewReceiptsPanel.setBackground(bodycolor);
            }
        });
    }

    private void performEdit() {
        int row = jTableBookings.getSelectedRow();
        if (row < 0 || row >= tableModel.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Please select a booking to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JTextField passengerField = new JTextField(String.valueOf(tableModel.getValueAt(row, 1)), 20);
        JTextField routeField = new JTextField(String.valueOf(tableModel.getValueAt(row, 2)), 20);
        JTextField dateField = new JTextField(String.valueOf(tableModel.getValueAt(row, 3)), 20);
        JTextField seatField = new JTextField(String.valueOf(tableModel.getValueAt(row, 4)), 10);
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new javax.swing.JLabel("Passenger:"));
        panel.add(passengerField);
        panel.add(new javax.swing.JLabel("Route:"));
        panel.add(routeField);
        panel.add(new javax.swing.JLabel("Date:"));
        panel.add(dateField);
        panel.add(new javax.swing.JLabel("Seat:"));
        panel.add(seatField);
        if (JOptionPane.showConfirmDialog(this, panel, "Edit Booking", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            String passenger = passengerField.getText().trim();
            String route = routeField.getText().trim();
            String date = dateField.getText().trim();
            String seat = seatField.getText().trim();
            if (passenger.isEmpty() || route.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Passenger and route are required.", "Edit Booking", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (Integer) tableModel.getValueAt(row, 0);
            Connection conn = null;
            try {
                conn = ConnectionConfig.getConnection();
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE bookings SET passenger = ?, route = ?, date = ?, seat = ? WHERE b_id = ?")) {
                    ps.setString(1, passenger);
                    ps.setString(2, route);
                    ps.setString(3, date);
                    ps.setString(4, seat);
                    ps.setInt(5, id);
                    ps.executeUpdate();
                }
                loadBookingsFromDb(null);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to update: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                ConnectionConfig.close(conn);
            }
        }
    }

    private void performDelete() {
        int row = jTableBookings.getSelectedRow();
        if (row < 0 || row >= tableModel.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Please select a booking to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete this booking?", "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            int id = (Integer) tableModel.getValueAt(row, 0);
            Connection conn = null;
            try {
                conn = ConnectionConfig.getConnection();
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM bookings WHERE b_id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                loadBookingsFromDb(null);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to delete: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                ConnectionConfig.close(conn);
            }
        }
    }

    private void performViewAllReceipts() {
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            javax.swing.table.DefaultTableModel tm = new javax.swing.table.DefaultTableModel(
                    new String[]{"Receipt ID", "Username", "Booking ID", "Origin", "Destination", "Seat", "Date"}, 0);
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT r_id, username, b_id, origin, destination, seat, date FROM receipts ORDER BY r_id DESC")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        tm.addRow(new Object[]{
                                rs.getInt("r_id"),
                                nullToEmpty(rs.getString("username")),
                                rs.getInt("b_id"),
                                nullToEmpty(rs.getString("origin")),
                                nullToEmpty(rs.getString("destination")),
                                nullToEmpty(rs.getString("seat")),
                                nullToEmpty(rs.getString("date"))
                        });
                    }
                }
            }
            javax.swing.JTable table = new javax.swing.JTable(tm);
            table.setEnabled(false);
            javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(table);
            scroll.setPreferredSize(new java.awt.Dimension(600, 350));
            JOptionPane.showMessageDialog(this, scroll, "All Receipts", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load receipts: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectionConfig.close(conn);
        }
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private void performUpdateStatus() {
        int row = jTableBookings.getSelectedRow();
        if (row < 0 || row >= tableModel.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Please select a booking to update status.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Object v = tableModel.getValueAt(row, 5);
        String current = (v != null) ? v.toString() : "";
        String status = (String) JOptionPane.showInputDialog(this, "Select status:", "Update Status", JOptionPane.PLAIN_MESSAGE, null, STATUS_OPTIONS, current);
        if (status != null && !status.trim().isEmpty()) {
            int id = (Integer) tableModel.getValueAt(row, 0);
            Connection conn = null;
            try {
                conn = ConnectionConfig.getConnection();
                try (PreparedStatement ps = conn.prepareStatement("UPDATE bookings SET status = ? WHERE b_id = ?")) {
                    ps.setString(1, status.trim());
                    ps.setInt(2, id);
                    ps.executeUpdate();
                }
                if ("Arrived".equalsIgnoreCase(status.trim())) {
                    try {
                        ReceiptUtil.createReceiptForBooking(conn, id);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Status updated but receipt could not be created: " + ex.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                }
                loadBookingsFromDb(null);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to update status: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                ConnectionConfig.close(conn);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableBookings = new javax.swing.JTable();
        EditPanel = new javax.swing.JPanel();
        EditText = new javax.swing.JLabel();
        EditLogo = new javax.swing.JLabel();
        DeletePanel = new javax.swing.JPanel();
        DeleteText = new javax.swing.JLabel();
        DeleteLogo = new javax.swing.JLabel();
        UpStatusPanel = new javax.swing.JPanel();
        UpStatusText = new javax.swing.JLabel();
        UpdateLogo = new javax.swing.JLabel();
        Search = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();

        setPreferredSize(new java.awt.Dimension(790, 420));
        setVisible(true);

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

        mainPanel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, 680, 320));

        EditPanel.setBackground(new java.awt.Color(204, 204, 255));
        EditPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                EditPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                EditPanelMouseExited(evt);
            }
        });

        EditText.setBackground(new java.awt.Color(153, 153, 153));
        EditText.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        EditText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EditText.setText("EDIT");

        EditLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EditLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-edit-property-48.png"))); // NOI18N

        javax.swing.GroupLayout EditPanelLayout = new javax.swing.GroupLayout(EditPanel);
        EditPanel.setLayout(EditPanelLayout);
        EditPanelLayout.setHorizontalGroup(
            EditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(EditText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(EditLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
        );
        EditPanelLayout.setVerticalGroup(
            EditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EditPanelLayout.createSequentialGroup()
                .addComponent(EditLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(EditText))
        );

        mainPanel.add(EditPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 70));

        DeletePanel.setBackground(new java.awt.Color(204, 204, 255));
        DeletePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                DeletePanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                DeletePanelMouseExited(evt);
            }
        });

        DeleteText.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        DeleteText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        DeleteText.setText("DELETE");

        DeleteLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        DeleteLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-remove-48.png"))); // NOI18N

        javax.swing.GroupLayout DeletePanelLayout = new javax.swing.GroupLayout(DeletePanel);
        DeletePanel.setLayout(DeletePanelLayout);
        DeletePanelLayout.setHorizontalGroup(
            DeletePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(DeleteText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(DeleteLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
        );
        DeletePanelLayout.setVerticalGroup(
            DeletePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DeletePanelLayout.createSequentialGroup()
                .addComponent(DeleteLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DeleteText))
        );

        mainPanel.add(DeletePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 80, 70));

        UpStatusPanel.setBackground(new java.awt.Color(204, 204, 255));
        UpStatusPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                UpStatusPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                UpStatusPanelMouseExited(evt);
            }
        });

        UpStatusText.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        UpStatusText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        UpStatusText.setText("UPDATE");

        UpdateLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        UpdateLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-installing-updates-48.png"))); // NOI18N

        javax.swing.GroupLayout UpStatusPanelLayout = new javax.swing.GroupLayout(UpStatusPanel);
        UpStatusPanel.setLayout(UpStatusPanelLayout);
        UpStatusPanelLayout.setHorizontalGroup(
            UpStatusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, UpStatusPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(UpStatusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(UpdateLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                    .addComponent(UpStatusText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        UpStatusPanelLayout.setVerticalGroup(
            UpStatusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, UpStatusPanelLayout.createSequentialGroup()
                .addComponent(UpdateLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(UpStatusText))
        );

        mainPanel.add(UpStatusPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 80, 70));

        Search.setBackground(new java.awt.Color(204, 204, 255));
        Search.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        Search.setForeground(new java.awt.Color(255, 255, 255));
        Search.setText("Enter Vehicle Type to search");
        Search.setBorder(null);
        Search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchActionPerformed(evt);
            }
        });
        mainPanel.add(Search, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 10, 660, 30));
        mainPanel.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, 670, 10));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 415, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTableBookingsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableBookingsMouseClicked
    }//GEN-LAST:event_jTableBookingsMouseClicked

    private void SearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchActionPerformed
        performSearchByVehicleType();
    }//GEN-LAST:event_SearchActionPerformed

    private void EditPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EditPanelMouseEntered
        EditPanel.setBackground(navcolor);
    }//GEN-LAST:event_EditPanelMouseEntered

    private void EditPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EditPanelMouseExited
        EditPanel.setBackground(bodycolor);
    }//GEN-LAST:event_EditPanelMouseExited

    private void DeletePanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DeletePanelMouseEntered
        DeletePanel.setBackground(navcolor);
    }//GEN-LAST:event_DeletePanelMouseEntered

    private void DeletePanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DeletePanelMouseExited
        DeletePanel.setBackground(bodycolor);
    }//GEN-LAST:event_DeletePanelMouseExited

    private void UpStatusPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UpStatusPanelMouseEntered
        UpStatusPanel.setBackground(navcolor);
    }//GEN-LAST:event_UpStatusPanelMouseEntered

    private void UpStatusPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UpStatusPanelMouseExited
        UpStatusPanel.setBackground(bodycolor);
    }//GEN-LAST:event_UpStatusPanelMouseExited

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel DeleteLogo;
    private javax.swing.JPanel DeletePanel;
    private javax.swing.JLabel DeleteText;
    private javax.swing.JLabel EditLogo;
    private javax.swing.JPanel EditPanel;
    private javax.swing.JLabel EditText;
    private javax.swing.JTextField Search;
    private javax.swing.JPanel UpStatusPanel;
    private javax.swing.JLabel UpStatusText;
    private javax.swing.JLabel UpdateLogo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTableBookings;
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables
}
