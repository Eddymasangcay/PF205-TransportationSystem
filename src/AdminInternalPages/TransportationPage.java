package AdminInternalPages;

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

public class TransportationPage extends InternalPageFrame {
    
    Color navcolor = new Color(153,153,255);
    Color bodycolor = new Color(204,204,255);
    Color staycolor = new Color(204,204,255);

    private final DefaultTableModel tableModel;
    private final List<Integer> routeIds = new ArrayList<>();
    private static final String[] ROUTE_COLUMNS = {"ID", "Vehicle Type", "Price", "Origin", "Destination"};

    public TransportationPage() {
        initComponents();
        tableModel = (DefaultTableModel) jTableBookings.getModel();
        tableModel.setColumnIdentifiers(ROUTE_COLUMNS);
        setupPanelListeners();
        setupSearchByID();
        loadRoutesFromDb(null);
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private int safeGetInt(int row, int col) {
        Object o = tableModel.getValueAt(row, col);
        return (o instanceof Number) ? ((Number) o).intValue() : 0;
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

        EditPanel.setCursor(handCursor);
        EditPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                EditRoute();
            }
        });

        BookTransPanel.setCursor(handCursor);
        BookTransPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Book();
            }
        });

        AddTransportationPanel.setCursor(handCursor);
        AddTransportationPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AddRoute();
            }
        });

        RemoveTransportationPanel.setCursor(handCursor);
        RemoveTransportationPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                performRemoveRoute();
            }
        });
    }

    private void performRemoveRoute() {
        int row = jTableBookings.getSelectedRow();
        if (row < 0 || row >= tableModel.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Please select a transportation to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Remove this transportation?", "Confirm Remove", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        int vId = safeGetInt(row, 0);
        if (vId <= 0) return;
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM routes WHERE v_id = ?")) {
                ps.setInt(1, vId);
                ps.executeUpdate();
            }
            loadRoutesFromDb(null);
            JOptionPane.showMessageDialog(this, "Transportation removed.", "Remove", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to remove: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectionConfig.close(conn);
        }
    }

    private void AddRoute() {
        JTextField vehicleTypeField = new JTextField(20);
        JTextField priceField = new JTextField("0", 10);
        JTextField originField = new JTextField(20);
        JTextField destField = new JTextField(20);
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new javax.swing.JLabel("Vehicle Type:"));
        panel.add(vehicleTypeField);
        panel.add(new javax.swing.JLabel("Price:"));
        panel.add(priceField);
        panel.add(new javax.swing.JLabel("Origin:"));
        panel.add(originField);
        panel.add(new javax.swing.JLabel("Destination:"));
        panel.add(destField);
        if (JOptionPane.showConfirmDialog(this, panel, "Add Transportation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }
        String vehicleType = vehicleTypeField.getText().trim();
        String origin = originField.getText().trim();
        String dest = destField.getText().trim();
        if (vehicleType.isEmpty() || origin.isEmpty() || dest.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vehicle type, origin and destination are required.", "Add Transportation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int vPrice = 0;
        try {
            vPrice = Integer.parseInt(priceField.getText().trim());
            if (vPrice < 0) vPrice = 0;
        } catch (NumberFormatException ignored) { }
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO routes (v_type, v_price, origin, destination) VALUES (?, ?, ?, ?)")) {
                ps.setString(1, vehicleType);
                ps.setInt(2, vPrice);
                ps.setString(3, origin);
                ps.setString(4, dest);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Transportation added successfully.", "Add Transportation", JOptionPane.INFORMATION_MESSAGE);
            loadRoutesFromDb(null);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to add: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectionConfig.close(conn);
        }
    }

    private void EditRoute() {
        int row = jTableBookings.getSelectedRow();
        if (row < 0 || row >= tableModel.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Please select a transportation to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Object priceVal = tableModel.getValueAt(row, 2);
        int price = priceVal instanceof Number ? ((Number) priceVal).intValue() : 0;
        JTextField vehicleTypeField = new JTextField(String.valueOf(tableModel.getValueAt(row, 1)), 20);
        JTextField priceField = new JTextField(String.valueOf(price), 10);
        JTextField originField = new JTextField(String.valueOf(tableModel.getValueAt(row, 3)), 20);
        JTextField destField = new JTextField(String.valueOf(tableModel.getValueAt(row, 4)), 20);
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new javax.swing.JLabel("Vehicle Type:"));
        panel.add(vehicleTypeField);
        panel.add(new javax.swing.JLabel("Price:"));
        panel.add(priceField);
        panel.add(new javax.swing.JLabel("Origin:"));
        panel.add(originField);
        panel.add(new javax.swing.JLabel("Destination:"));
        panel.add(destField);
        if (JOptionPane.showConfirmDialog(this, panel, "Edit Transportation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }
        String vehicleType = vehicleTypeField.getText().trim();
        String origin = originField.getText().trim();
        String dest = destField.getText().trim();
        if (vehicleType.isEmpty() || origin.isEmpty() || dest.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vehicle type, origin and destination are required.", "Edit Transportation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int vPrice = 0;
        try {
            vPrice = Integer.parseInt(priceField.getText().trim());
            if (vPrice < 0) vPrice = 0;
        } catch (NumberFormatException ignored) { }
        int vId = safeGetInt(row, 0);
        if (vId <= 0) return;
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            try (PreparedStatement ps = conn.prepareStatement("UPDATE routes SET v_type = ?, v_price = ?, origin = ?, destination = ? WHERE v_id = ?")) {
                ps.setString(1, vehicleType);
                ps.setInt(2, vPrice);
                ps.setString(3, origin);
                ps.setString(4, dest);
                ps.setInt(5, vId);
                ps.executeUpdate();
            }
            loadRoutesFromDb(null);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectionConfig.close(conn);
        }
    }

    private void Book() {
        int row = jTableBookings.getSelectedRow();
        if (row < 0 || row >= tableModel.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Please select a transportation to book.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String vehicleType = String.valueOf(tableModel.getValueAt(row, 1));
        Object priceVal = tableModel.getValueAt(row, 2);
        int price = priceVal instanceof Number ? ((Number) priceVal).intValue() : 0;
        String origin = String.valueOf(tableModel.getValueAt(row, 3));
        String dest = String.valueOf(tableModel.getValueAt(row, 4));
        String routeDisplay = vehicleType + " " + nullToEmpty(origin) + " - " + nullToEmpty(dest);
        JTextField passengerField = new JTextField(20);
        JTextField seatField = new JTextField(10);
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new javax.swing.JLabel("Vehicle Type:"));
        panel.add(new javax.swing.JLabel(vehicleType));
        panel.add(new javax.swing.JLabel("Price:"));
        panel.add(new javax.swing.JLabel(price + ""));
        panel.add(new javax.swing.JLabel("Passenger Name:"));
        panel.add(passengerField);
        panel.add(new javax.swing.JLabel("Seat:"));
        panel.add(seatField);
        if (JOptionPane.showConfirmDialog(this, panel, "Book Route", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }
        String passenger = passengerField.getText().trim();
        if (passenger.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Passenger name is required.", "Book", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String seat = seatField.getText().trim();
        int vId = safeGetInt(row, 0);
        if (vId <= 0) return;
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            int newBookingId = -1;
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO bookings (passenger, v_type, v_id, route, seat, status) VALUES (?, ?, ?, ?, ?, 'Pending')")) {
                ps.setString(1, passenger);
                ps.setString(2, vehicleType);
                ps.setInt(3, vId);
                ps.setString(4, routeDisplay);
                ps.setString(5, seat);
                ps.executeUpdate();
            }
            // Get the newly created booking ID
            try (PreparedStatement ps = conn.prepareStatement("SELECT MAX(b_id) AS last_id FROM bookings")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        newBookingId = rs.getInt("last_id");
                    }
                }
            }
            // Create receipt immediately after booking
            if (newBookingId > 0) {
                try {
                    ReceiptUtil.createReceiptForBooking(conn, newBookingId);
                } catch (SQLException ex) {
                    System.err.println("Receipt creation warning: " + ex.getMessage());
                }
            }
            JOptionPane.showMessageDialog(this, "Booking created successfully! Receipt generated.", "Book", JOptionPane.INFORMATION_MESSAGE);
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
        EditPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        AddTransportationPanel = new javax.swing.JPanel();
        AddTransText = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        RemoveTransportationPanel = new javax.swing.JPanel();
        RemoveTransportationText = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
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

        mainPanel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 50, 670, 320));

        EditPanel.setBackground(new java.awt.Color(204, 204, 255));
        EditPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                EditPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                EditPanelMouseExited(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-edit-property-48.png"))); // NOI18N
        jLabel1.setBackground(EditPanel.getBackground());
        jLabel1.setOpaque(true);

        jLabel2.setFont(new java.awt.Font("Bahnschrift", 1, 11)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("EDIT");

        javax.swing.GroupLayout EditPanelLayout = new javax.swing.GroupLayout(EditPanel);
        EditPanel.setLayout(EditPanelLayout);
        EditPanelLayout.setHorizontalGroup(
            EditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        EditPanelLayout.setVerticalGroup(
            EditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EditPanelLayout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(EditPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 80));

        AddTransportationPanel.setBackground(new java.awt.Color(204, 204, 255));
        AddTransportationPanel.setPreferredSize(new java.awt.Dimension(80, 80));
        AddTransportationPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                AddTransportationPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                AddTransportationPanelMouseExited(evt);
            }
        });

        AddTransText.setFont(new java.awt.Font("Bahnschrift", 1, 11)); // NOI18N
        AddTransText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        AddTransText.setText("ADD");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-add-properties-48.png"))); // NOI18N

        javax.swing.GroupLayout AddTransportationPanelLayout = new javax.swing.GroupLayout(AddTransportationPanel);
        AddTransportationPanel.setLayout(AddTransportationPanelLayout);
        AddTransportationPanelLayout.setHorizontalGroup(
            AddTransportationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(AddTransText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
        );
        AddTransportationPanelLayout.setVerticalGroup(
            AddTransportationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddTransportationPanelLayout.createSequentialGroup()
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AddTransText, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(AddTransportationPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, -1, -1));

        RemoveTransportationPanel.setBackground(new java.awt.Color(204, 204, 255));
        RemoveTransportationPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                RemoveTransportationPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                RemoveTransportationPanelMouseExited(evt);
            }
        });

        RemoveTransportationText.setFont(new java.awt.Font("Bahnschrift", 1, 11)); // NOI18N
        RemoveTransportationText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        RemoveTransportationText.setText("REMOVE");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-remove-48.png"))); // NOI18N

        javax.swing.GroupLayout RemoveTransportationPanelLayout = new javax.swing.GroupLayout(RemoveTransportationPanel);
        RemoveTransportationPanel.setLayout(RemoveTransportationPanelLayout);
        RemoveTransportationPanelLayout.setHorizontalGroup(
            RemoveTransportationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(RemoveTransportationText, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        RemoveTransportationPanelLayout.setVerticalGroup(
            RemoveTransportationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RemoveTransportationPanelLayout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RemoveTransportationText, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(RemoveTransportationPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, 80, 80));

        Search.setBackground(new java.awt.Color(204, 204, 255));
        Search.setForeground(new java.awt.Color(255, 255, 255));
        Search.setText("Enter Transportation ID to search");
        Search.setBorder(null);
        Search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchActionPerformed(evt);
            }
        });
        mainPanel.add(Search, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 10, 660, 30));

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
                .addGap(0, 2, Short.MAX_VALUE))
        );
        BookTransPanelLayout.setVerticalGroup(
            BookTransPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BookTransPanelLayout.createSequentialGroup()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BookTransText, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(BookTransPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 80, 80));
        mainPanel.add(SearchSep, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 40, 670, 10));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void EditPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EditPanelMouseEntered
        EditPanel.setBackground(navcolor);
        jLabel1.setBackground(navcolor);
    }//GEN-LAST:event_EditPanelMouseEntered

    private void EditPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EditPanelMouseExited
        EditPanel.setBackground(bodycolor);
        jLabel1.setBackground(bodycolor);
    }//GEN-LAST:event_EditPanelMouseExited

    private void AddTransportationPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AddTransportationPanelMouseEntered
        AddTransportationPanel.setBackground(navcolor);
    }//GEN-LAST:event_AddTransportationPanelMouseEntered

    private void AddTransportationPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AddTransportationPanelMouseExited
        AddTransportationPanel.setBackground(bodycolor);
    }//GEN-LAST:event_AddTransportationPanelMouseExited

    private void RemoveTransportationPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_RemoveTransportationPanelMouseEntered
        RemoveTransportationPanel.setBackground(navcolor);
    }//GEN-LAST:event_RemoveTransportationPanelMouseEntered

    private void RemoveTransportationPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_RemoveTransportationPanelMouseExited
        RemoveTransportationPanel.setBackground(bodycolor);
    }//GEN-LAST:event_RemoveTransportationPanelMouseExited

    private void BookTransPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BookTransPanelMouseEntered
        BookTransPanel.setBackground(navcolor);
    }//GEN-LAST:event_BookTransPanelMouseEntered

    private void BookTransPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BookTransPanelMouseExited
        BookTransPanel.setBackground(bodycolor);
    }//GEN-LAST:event_BookTransPanelMouseExited

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AddTransText;
    private javax.swing.JPanel AddTransportationPanel;
    private javax.swing.JPanel BookTransPanel;
    private javax.swing.JLabel BookTransText;
    private javax.swing.JPanel EditPanel;
    private javax.swing.JPanel RemoveTransportationPanel;
    private javax.swing.JLabel RemoveTransportationText;
    private javax.swing.JTextField Search;
    private javax.swing.JSeparator SearchSep;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableBookings;
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables
}
