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

public class TransportationPage extends InternalPageFrame {

    private final DefaultTableModel tableModel;
    private final List<Integer> routeIds = new ArrayList<>();
    private static final String[] ROUTE_COLUMNS = {"Transportation ID", "Vehicle Type", "Origin", "Destination"};

    public TransportationPage() {
        initComponents();
        tableModel = (DefaultTableModel) jTableBookings.getModel();
        tableModel.setColumnIdentifiers(ROUTE_COLUMNS);
        setupPanelListeners();
        setupSearchByID();
        loadRoutesFromDb(null);
    }

    private void loadRoutesFromDb(Integer filterById) {
        tableModel.setRowCount(0);
        routeIds.clear();
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            String sql = "SELECT v_id, v_type, origin, destination FROM routes";
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
                            rs.getString("v_type"),
                            rs.getString("origin"),
                            rs.getString("destination")
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
                performEditRoute();
            }
        });

        BookTransPanel.setCursor(handCursor);
        BookTransPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                performBook();
            }
        });

        AddTransportationPanel.setCursor(handCursor);
        AddTransportationPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                performAddRoute();
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
        if (row < 0 || row >= routeIds.size()) {
            JOptionPane.showMessageDialog(this, "Please select a transportation to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Remove this transportation?", "Confirm Remove", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        int vId = routeIds.get(row);
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM routes WHERE v_id = ?")) {
                ps.setInt(1, vId);
                ps.executeUpdate();
            }
            routeIds.remove(row);
            tableModel.removeRow(row);
            JOptionPane.showMessageDialog(this, "Transportation removed.", "Remove", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to remove: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectionConfig.close(conn);
        }
    }

    private void performAddRoute() {
        JTextField vehicleTypeField = new JTextField(20);
        JTextField originField = new JTextField(20);
        JTextField destField = new JTextField(20);
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new javax.swing.JLabel("Vehicle Type:"));
        panel.add(vehicleTypeField);
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
            JOptionPane.showMessageDialog(this, "All fields are required.", "Add Transportation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO routes (v_type, origin, destination) VALUES (?, ?, ?)")) {
                ps.setString(1, vehicleType);
                ps.setString(2, origin);
                ps.setString(3, dest);
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

    private void performEditRoute() {
        int row = jTableBookings.getSelectedRow();
        if (row < 0 || row >= routeIds.size()) {
            JOptionPane.showMessageDialog(this, "Please select a transportation to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JTextField vehicleTypeField = new JTextField(String.valueOf(tableModel.getValueAt(row, 1)), 20);
        JTextField originField = new JTextField(String.valueOf(tableModel.getValueAt(row, 2)), 20);
        JTextField destField = new JTextField(String.valueOf(tableModel.getValueAt(row, 3)), 20);
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new javax.swing.JLabel("Vehicle Type:"));
        panel.add(vehicleTypeField);
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
            JOptionPane.showMessageDialog(this, "All fields are required.", "Edit Transportation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int vId = routeIds.get(row);
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            try (PreparedStatement ps = conn.prepareStatement("UPDATE routes SET v_type = ?, origin = ?, destination = ? WHERE v_id = ?")) {
                ps.setString(1, vehicleType);
                ps.setString(2, origin);
                ps.setString(3, dest);
                ps.setInt(4, vId);
                ps.executeUpdate();
            }
            tableModel.setValueAt(vehicleType, row, 1);
            tableModel.setValueAt(origin, row, 2);
            tableModel.setValueAt(dest, row, 3);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectionConfig.close(conn);
        }
    }

    private void performBook() {
        int row = jTableBookings.getSelectedRow();
        if (row < 0 || row >= routeIds.size()) {
            JOptionPane.showMessageDialog(this, "Please select a transportation to book.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String vehicleType = String.valueOf(tableModel.getValueAt(row, 1));
        JTextField passengerField = new JTextField(20);
        JTextField seatField = new JTextField(10);
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new javax.swing.JLabel("Vehicle Type:"));
        panel.add(new javax.swing.JLabel(vehicleType));
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
        int vId = routeIds.get(row);
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO bookings (passenger, v_type, v_id, route, seat, status) VALUES (?, ?, ?, ?, ?, 'Pending')")) {
                ps.setString(1, passenger);
                ps.setString(2, vehicleType);
                ps.setInt(3, vId);
                ps.setString(4, vehicleType);
                ps.setString(5, seat);
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
        HEADER = new javax.swing.JPanel();
        HEADERTEXT = new javax.swing.JLabel();
        EditPanel = new javax.swing.JPanel();
        EditText = new javax.swing.JLabel();
        BookTransPanel = new javax.swing.JPanel();
        BookTransText = new javax.swing.JLabel();
        AddTransportationPanel = new javax.swing.JPanel();
        AddTransText = new javax.swing.JLabel();
        RemoveTransportationPanel = new javax.swing.JPanel();
        RemoveTransportationText = new javax.swing.JLabel();
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
        HEADERTEXT.setText("TRANSPORTATION");

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
        EditPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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

        BookTransPanel.setBackground(new java.awt.Color(102, 102, 102));
        BookTransPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        BookTransText.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        BookTransText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        BookTransText.setText("BOOK");

        javax.swing.GroupLayout BookTransPanelLayout = new javax.swing.GroupLayout(BookTransPanel);
        BookTransPanel.setLayout(BookTransPanelLayout);
        BookTransPanelLayout.setHorizontalGroup(
            BookTransPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BookTransPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(BookTransText, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        BookTransPanelLayout.setVerticalGroup(
            BookTransPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BookTransPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(BookTransText, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(BookTransPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 50, 110, -1));

        AddTransportationPanel.setBackground(new java.awt.Color(102, 102, 102));
        AddTransportationPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        AddTransText.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        AddTransText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        AddTransText.setText("ADD");

        javax.swing.GroupLayout AddTransportationPanelLayout = new javax.swing.GroupLayout(AddTransportationPanel);
        AddTransportationPanel.setLayout(AddTransportationPanelLayout);
        AddTransportationPanelLayout.setHorizontalGroup(
            AddTransportationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddTransportationPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(AddTransText, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        AddTransportationPanelLayout.setVerticalGroup(
            AddTransportationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddTransportationPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(AddTransText, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(AddTransportationPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 50, -1, -1));

        RemoveTransportationPanel.setBackground(new java.awt.Color(102, 102, 102));
        RemoveTransportationPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        RemoveTransportationText.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        RemoveTransportationText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        RemoveTransportationText.setText("REMOVE");

        javax.swing.GroupLayout RemoveTransportationPanelLayout = new javax.swing.GroupLayout(RemoveTransportationPanel);
        RemoveTransportationPanel.setLayout(RemoveTransportationPanelLayout);
        RemoveTransportationPanelLayout.setHorizontalGroup(
            RemoveTransportationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RemoveTransportationPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(RemoveTransportationText, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        RemoveTransportationPanelLayout.setVerticalGroup(
            RemoveTransportationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RemoveTransportationPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(RemoveTransportationText, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(RemoveTransportationPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 50, -1, -1));

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

    private void SearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchActionPerformed
        performSearchByID();
    }//GEN-LAST:event_SearchActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AddTransText;
    private javax.swing.JPanel AddTransportationPanel;
    private javax.swing.JPanel BookTransPanel;
    private javax.swing.JLabel BookTransText;
    private javax.swing.JPanel EditPanel;
    private javax.swing.JLabel EditText;
    private javax.swing.JPanel HEADER;
    private javax.swing.JLabel HEADERTEXT;
    private javax.swing.JPanel RemoveTransportationPanel;
    private javax.swing.JLabel RemoveTransportationText;
    private javax.swing.JTextField Search;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableBookings;
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables
}
