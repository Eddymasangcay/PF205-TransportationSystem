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

public class UserBookings extends InternalPageFrame {
    
    Color navcolor = new Color(153,153,255);
    Color bodycolor = new Color(204,204,255);
    Color staycolor = new Color(204,204,255);
    
    private final DefaultTableModel tableModel;
    private final List<Integer> bookingIds = new ArrayList<>();
    private final int currentUserId;
    private static final String[] BOOKING_COLUMNS = {"ID", "Passenger", "Route", "Date", "Seat", "Status"};

    public UserBookings(int currentUserId) {
        this.currentUserId = currentUserId;
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
            String sql = "SELECT b_id, passenger, route, date, seat, status FROM bookings WHERE passenger_id = ?";
            if (filterByVehicleType != null && !filterByVehicleType.isEmpty()) {
                sql += " AND route LIKE ?";
            }
            sql += " ORDER BY b_id";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, currentUserId);
                if (filterByVehicleType != null && !filterByVehicleType.isEmpty()) {
                    ps.setString(2, "%" + filterByVehicleType.trim() + "%");
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
                performViewDetails();
            }
        });

        DeletePanel.setCursor(handCursor);
        DeletePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                performCancelBooking();
            }
        });
    }

    private void performViewDetails() {
        int row = jTableBookings.getSelectedRow();
        if (row < 0 || row >= tableModel.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Please select a booking to view.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int bookingId = (Integer) tableModel.getValueAt(row, 0);
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            String passenger = "", route = "", date = "", seat = "", status = "", vehicleType = "";
            int price = 0;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT b.passenger, b.route, b.date, b.seat, b.status, b.v_type, COALESCE(r.v_price, 0) AS price " +
                    "FROM bookings b LEFT JOIN routes r ON b.v_id = r.v_id WHERE b.b_id = ?")) {
                ps.setInt(1, bookingId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        passenger = nullToEmpty(rs.getString("passenger"));
                        route = nullToEmpty(rs.getString("route"));
                        date = nullToEmpty(rs.getString("date"));
                        seat = nullToEmpty(rs.getString("seat"));
                        status = nullToEmpty(rs.getString("status"));
                        vehicleType = nullToEmpty(rs.getString("v_type"));
                        price = rs.getInt("price");
                    }
                }
            }
            JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
            panel.add(new javax.swing.JLabel("Booking ID:"));
            panel.add(new javax.swing.JLabel(String.valueOf(bookingId)));
            panel.add(new javax.swing.JLabel("Passenger:"));
            panel.add(new javax.swing.JLabel(passenger));
            panel.add(new javax.swing.JLabel("Vehicle Type:"));
            panel.add(new javax.swing.JLabel(vehicleType));
            panel.add(new javax.swing.JLabel("Route:"));
            panel.add(new javax.swing.JLabel(route));
            panel.add(new javax.swing.JLabel("Seat:"));
            panel.add(new javax.swing.JLabel(seat.isEmpty() ? "N/A" : seat));
            panel.add(new javax.swing.JLabel("Price:"));
            panel.add(new javax.swing.JLabel(price + ""));
            panel.add(new javax.swing.JLabel("Status:"));
            panel.add(new javax.swing.JLabel(status));
            JOptionPane.showMessageDialog(this, panel, "Booking Details", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load booking details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectionConfig.close(conn);
        }
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private void performCancelBooking() {
        int row = jTableBookings.getSelectedRow();
        if (row < 0 || row >= tableModel.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String status = String.valueOf(tableModel.getValueAt(row, 5));
        if ("Arrived".equalsIgnoreCase(status) || "Cancelled".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "This booking cannot be cancelled (status: " + status + ").", "Cancel Booking", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Cancel this booking?", "Confirm Cancel", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            int id = (Integer) tableModel.getValueAt(row, 0);
            Connection conn = null;
            try {
                conn = ConnectionConfig.getConnection();
                try (PreparedStatement ps = conn.prepareStatement("UPDATE bookings SET status = 'Cancelled' WHERE b_id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Booking cancelled successfully.", "Cancel Booking", JOptionPane.INFORMATION_MESSAGE);
                loadBookingsFromDb(null);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to cancel: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        Search = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();

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
        EditText.setText("VIEW");

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

        mainPanel.add(EditPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 80, 70));

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
        DeleteText.setText("CANCEL");

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

        mainPanel.add(DeletePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 80, 70));

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel DeleteLogo;
    private javax.swing.JPanel DeletePanel;
    private javax.swing.JLabel DeleteText;
    private javax.swing.JLabel EditLogo;
    private javax.swing.JPanel EditPanel;
    private javax.swing.JLabel EditText;
    private javax.swing.JTextField Search;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTableBookings;
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables
}
