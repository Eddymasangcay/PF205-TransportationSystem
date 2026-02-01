package InternalPages;

import Configuration.ConnectionConfig;
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

    private final DefaultTableModel tableModel;
    private final List<Integer> bookingIds = new ArrayList<>();
    private static final String[] STATUS_OPTIONS = {"In-Transit", "Stopped", "Arrived"};
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
    }

    private void performEdit() {
        int row = jTableBookings.getSelectedRow();
        if (row < 0 || row >= bookingIds.size()) {
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
            int id = bookingIds.get(row);
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
                tableModel.setValueAt(passenger, row, 1);
                tableModel.setValueAt(route, row, 2);
                tableModel.setValueAt(date, row, 3);
                tableModel.setValueAt(seat, row, 4);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to update: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                ConnectionConfig.close(conn);
            }
        }
    }

    private void performDelete() {
        int row = jTableBookings.getSelectedRow();
        if (row < 0 || row >= bookingIds.size()) {
            JOptionPane.showMessageDialog(this, "Please select a booking to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete this booking?", "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            int id = bookingIds.get(row);
            Connection conn = null;
            try {
                conn = ConnectionConfig.getConnection();
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM bookings WHERE b_id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                bookingIds.remove(row);
                tableModel.removeRow(row);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to delete: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                ConnectionConfig.close(conn);
            }
        }
    }

    private void performUpdateStatus() {
        int row = jTableBookings.getSelectedRow();
        if (row < 0 || row >= bookingIds.size()) {
            JOptionPane.showMessageDialog(this, "Please select a booking to update status.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Object v = tableModel.getValueAt(row, 5);
        String current = (v != null) ? v.toString() : "";
        String status = (String) JOptionPane.showInputDialog(this, "Select status:", "Update Status", JOptionPane.PLAIN_MESSAGE, null, STATUS_OPTIONS, current);
        if (status != null) {
            int id = bookingIds.get(row);
            Connection conn = null;
            try {
                conn = ConnectionConfig.getConnection();
                try (PreparedStatement ps = conn.prepareStatement("UPDATE bookings SET status = ? WHERE b_id = ?")) {
                    ps.setString(1, status);
                    ps.setInt(2, id);
                    ps.executeUpdate();
                }
                tableModel.setValueAt(status, row, 5);
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
        HEADER = new javax.swing.JPanel();
        HEADERTEXT = new javax.swing.JLabel();
        EditPanel = new javax.swing.JPanel();
        EditText = new javax.swing.JLabel();
        DeletePanel = new javax.swing.JPanel();
        DeleteText = new javax.swing.JLabel();
        UpStatusPanel = new javax.swing.JPanel();
        UpStatusText = new javax.swing.JLabel();
        Search = new javax.swing.JTextField();

        mainPanel.setBackground(new java.awt.Color(153, 153, 153));
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

        mainPanel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 780, 360));

        HEADER.setBackground(new java.awt.Color(102, 102, 102));

        HEADERTEXT.setBackground(new java.awt.Color(102, 102, 102));
        HEADERTEXT.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        HEADERTEXT.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        HEADERTEXT.setText("BOOKINGS");

        javax.swing.GroupLayout HEADERLayout = new javax.swing.GroupLayout(HEADER);
        HEADER.setLayout(HEADERLayout);
        HEADERLayout.setHorizontalGroup(
            HEADERLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(HEADERTEXT, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );
        HEADERLayout.setVerticalGroup(
            HEADERLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HEADERLayout.createSequentialGroup()
                .addComponent(HEADERTEXT, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        mainPanel.add(HEADER, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 30));

        EditPanel.setBackground(new java.awt.Color(102, 102, 102));

        EditText.setBackground(new java.awt.Color(153, 153, 153));
        EditText.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        EditText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EditText.setText("EDIT");

        javax.swing.GroupLayout EditPanelLayout = new javax.swing.GroupLayout(EditPanel);
        EditPanel.setLayout(EditPanelLayout);
        EditPanelLayout.setHorizontalGroup(
            EditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EditPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(EditText, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        EditPanelLayout.setVerticalGroup(
            EditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EditPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(EditText, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(EditPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, 30));

        DeletePanel.setBackground(new java.awt.Color(102, 102, 102));

        DeleteText.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        DeleteText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        DeleteText.setText("DELETE");

        javax.swing.GroupLayout DeletePanelLayout = new javax.swing.GroupLayout(DeletePanel);
        DeletePanel.setLayout(DeletePanelLayout);
        DeletePanelLayout.setHorizontalGroup(
            DeletePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DeletePanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(DeleteText, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        DeletePanelLayout.setVerticalGroup(
            DeletePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DeletePanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(DeleteText, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(DeletePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 50, 110, -1));

        UpStatusPanel.setBackground(new java.awt.Color(102, 102, 102));

        UpStatusText.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        UpStatusText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        UpStatusText.setText("UPDATE STATUS");

        javax.swing.GroupLayout UpStatusPanelLayout = new javax.swing.GroupLayout(UpStatusPanel);
        UpStatusPanel.setLayout(UpStatusPanelLayout);
        UpStatusPanelLayout.setHorizontalGroup(
            UpStatusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, UpStatusPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(UpStatusText, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        UpStatusPanelLayout.setVerticalGroup(
            UpStatusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, UpStatusPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(UpStatusText, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(UpStatusPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 50, -1, -1));

        Search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchActionPerformed(evt);
            }
        });
        mainPanel.add(Search, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 50, 200, 30));

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTableBookingsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableBookingsMouseClicked
    }//GEN-LAST:event_jTableBookingsMouseClicked

    private void SearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchActionPerformed
        performSearchByVehicleType();
    }//GEN-LAST:event_SearchActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel DeletePanel;
    private javax.swing.JLabel DeleteText;
    private javax.swing.JPanel EditPanel;
    private javax.swing.JLabel EditText;
    private javax.swing.JPanel HEADER;
    private javax.swing.JLabel HEADERTEXT;
    private javax.swing.JTextField Search;
    private javax.swing.JPanel UpStatusPanel;
    private javax.swing.JLabel UpStatusText;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableBookings;
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables
}
