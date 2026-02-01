package InternalPages;

import Configuration.ConnectionConfig;
import Main.Mainframe;
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
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class AdminPage extends InternalPageFrame {

    private final DefaultTableModel tableModel;
    private final List<Integer> userIds = new ArrayList<>();
    private static final String[] USER_COLUMNS = {"ID", "Username", "Email", "Name"};

    private javax.swing.JPanel LogoutPanel;

    public AdminPage() {
        initComponents();
        tableModel = (DefaultTableModel) jTableBookings.getModel();
        tableModel.setColumnIdentifiers(USER_COLUMNS);
        setupLogoutPanel();
        setupPanelListeners();
        setupSearchByID();
        loadUsersFromDb(null);
    }

    private void loadUsersFromDb(Integer filterById) {
        tableModel.setRowCount(0);
        userIds.clear();
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            String sql = "SELECT u_id, username, email, name FROM users";
            if (filterById != null) {
                sql += " WHERE u_id = ?";
            }
            sql += " ORDER BY u_id";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (filterById != null) {
                    ps.setInt(1, filterById);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        userIds.add(rs.getInt("u_id"));
                        tableModel.addRow(new Object[]{
                            rs.getInt("u_id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("name")
                        });
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            loadUsersFromDb(null);
            return;
        }
        try {
            int id = Integer.parseInt(text);
            loadUsersFromDb(id);
        } catch (NumberFormatException ignored) {
            loadUsersFromDb(null);
        }
    }

    private void setupLogoutPanel() {
        LogoutPanel = new javax.swing.JPanel();
        LogoutPanel.setBackground(new Color(102, 102, 102));
        javax.swing.JLabel logoutLabel = new javax.swing.JLabel("LOGOUT");
        logoutLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        logoutLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(LogoutPanel);
        LogoutPanel.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logoutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logoutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        mainPanel.add(LogoutPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 50, 90, 30));
    }

    private void setupPanelListeners() {
        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

        EditUserPanel.setCursor(handCursor);
        EditUserPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                performEditUser();
            }
        });

        AddUserPanel.setCursor(handCursor);
        AddUserPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                performAddUser();
            }
        });

        RemoveUserPanel.setCursor(handCursor);
        RemoveUserPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                performDeleteUser();
            }
        });

        LogoutPanel.setCursor(handCursor);
        LogoutPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                performLogout();
            }
        });
    }

    private void performLogout() {
        java.awt.Window top = SwingUtilities.getWindowAncestor(this);
        if (top != null) {
            top.dispose();
        }
        Mainframe mainframe = new Mainframe();
        mainframe.setLocationRelativeTo(null);
        mainframe.setVisible(true);
    }

    private void performAddUser() {
        JTextField usernameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JTextField passwordField = new JTextField(20);
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new javax.swing.JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new javax.swing.JLabel("Email:"));
        panel.add(emailField);
        panel.add(new javax.swing.JLabel("Name:"));
        panel.add(nameField);
        panel.add(new javax.swing.JLabel("Password:"));
        panel.add(passwordField);
        if (JOptionPane.showConfirmDialog(this, panel, "Add User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String name = nameField.getText().trim();
        String password = passwordField.getText().trim();
        if (username.isEmpty() || email.isEmpty() || name.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Add User", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            try (PreparedStatement check = conn.prepareStatement("SELECT u_id FROM users WHERE username = ?")) {
                check.setString(1, username);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "Username already exists.", "Add User", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, email, name, password) VALUES (?, ?, ?, ?)")) {
                ps.setString(1, username);
                ps.setString(2, email);
                ps.setString(3, name);
                ps.setString(4, password);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "User added successfully.", "Add User", JOptionPane.INFORMATION_MESSAGE);
            loadUsersFromDb(null);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to add user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectionConfig.close(conn);
        }
    }

    private void performEditUser() {
        int row = jTableBookings.getSelectedRow();
        if (row < 0 || row >= userIds.size()) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JTextField usernameField = new JTextField(String.valueOf(tableModel.getValueAt(row, 1)), 20);
        JTextField emailField = new JTextField(String.valueOf(tableModel.getValueAt(row, 2)), 20);
        JTextField nameField = new JTextField(String.valueOf(tableModel.getValueAt(row, 3)), 20);
        JTextField passwordField = new JTextField("", 20);
        passwordField.setToolTipText("Leave blank to keep current password");
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new javax.swing.JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new javax.swing.JLabel("Email:"));
        panel.add(emailField);
        panel.add(new javax.swing.JLabel("Name:"));
        panel.add(nameField);
        panel.add(new javax.swing.JLabel("New password (optional):"));
        panel.add(passwordField);
        if (JOptionPane.showConfirmDialog(this, panel, "Edit User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String name = nameField.getText().trim();
        String newPassword = passwordField.getText().trim();
        if (username.isEmpty() || email.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username, email and name are required.", "Edit User", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = userIds.get(row);
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            try (PreparedStatement check = conn.prepareStatement("SELECT u_id FROM users WHERE username = ? AND u_id != ?")) {
                check.setString(1, username);
                check.setInt(2, id);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "Username already exists. Choose another.", "Edit User", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }
            if (!newPassword.isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement("UPDATE users SET username = ?, email = ?, name = ?, password = ? WHERE u_id = ?")) {
                    ps.setString(1, username);
                    ps.setString(2, email);
                    ps.setString(3, name);
                    ps.setString(4, newPassword);
                    ps.setInt(5, id);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement("UPDATE users SET username = ?, email = ?, name = ? WHERE u_id = ?")) {
                    ps.setString(1, username);
                    ps.setString(2, email);
                    ps.setString(3, name);
                    ps.setInt(4, id);
                    ps.executeUpdate();
                }
            }
            tableModel.setValueAt(username, row, 1);
            tableModel.setValueAt(email, row, 2);
            tableModel.setValueAt(name, row, 3);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectionConfig.close(conn);
        }
    }

    private void performDeleteUser() {
        int row = jTableBookings.getSelectedRow();
        if (row < 0 || row >= userIds.size()) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        int id = userIds.get(row);
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE u_id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            userIds.remove(row);
            tableModel.removeRow(row);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to delete user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        EditUserPanel = new javax.swing.JPanel();
        EditUserText = new javax.swing.JLabel();
        AddUserPanel = new javax.swing.JPanel();
        AddUserText = new javax.swing.JLabel();
        RemoveUserPanel = new javax.swing.JPanel();
        RemoveUserText = new javax.swing.JLabel();
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
        HEADERTEXT.setText("ADMINISTRATOR PAGE");

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

        EditUserPanel.setBackground(new java.awt.Color(102, 102, 102));
        EditUserPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        EditUserText.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        EditUserText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EditUserText.setText("EDIT USER");

        javax.swing.GroupLayout EditUserPanelLayout = new javax.swing.GroupLayout(EditUserPanel);
        EditUserPanel.setLayout(EditUserPanelLayout);
        EditUserPanelLayout.setHorizontalGroup(
            EditUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EditUserPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(EditUserText, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        EditUserPanelLayout.setVerticalGroup(
            EditUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EditUserPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(EditUserText, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(EditUserPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, 30));

        AddUserPanel.setBackground(new java.awt.Color(102, 102, 102));
        AddUserPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        AddUserText.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        AddUserText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        AddUserText.setText("ADD USER");

        javax.swing.GroupLayout AddUserPanelLayout = new javax.swing.GroupLayout(AddUserPanel);
        AddUserPanel.setLayout(AddUserPanelLayout);
        AddUserPanelLayout.setHorizontalGroup(
            AddUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddUserPanelLayout.createSequentialGroup()
                .addComponent(AddUserText, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        AddUserPanelLayout.setVerticalGroup(
            AddUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddUserPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(AddUserText, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(AddUserPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 50, 110, -1));

        RemoveUserPanel.setBackground(new java.awt.Color(102, 102, 102));
        RemoveUserPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        RemoveUserText.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        RemoveUserText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        RemoveUserText.setText("REMOVE USER");

        javax.swing.GroupLayout RemoveUserPanelLayout = new javax.swing.GroupLayout(RemoveUserPanel);
        RemoveUserPanel.setLayout(RemoveUserPanelLayout);
        RemoveUserPanelLayout.setHorizontalGroup(
            RemoveUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RemoveUserPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(RemoveUserText, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        RemoveUserPanelLayout.setVerticalGroup(
            RemoveUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RemoveUserPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(RemoveUserText, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(RemoveUserPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 50, -1, -1));

        Search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchActionPerformed(evt);
            }
        });
        mainPanel.add(Search, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 50, 200, 30));

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
    private javax.swing.JPanel AddUserPanel;
    private javax.swing.JLabel AddUserText;
    private javax.swing.JPanel EditUserPanel;
    private javax.swing.JLabel EditUserText;
    private javax.swing.JPanel HEADER;
    private javax.swing.JLabel HEADERTEXT;
    private javax.swing.JPanel RemoveUserPanel;
    private javax.swing.JLabel RemoveUserText;
    private javax.swing.JTextField Search;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableBookings;
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables
}
