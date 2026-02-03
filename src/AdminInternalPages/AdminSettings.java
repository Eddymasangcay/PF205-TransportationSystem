package AdminInternalPages;

import Configuration.ConnectionConfig;
import Configuration.PasswordUtil;
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

public class AdminSettings extends InternalPageFrame {

    private final DefaultTableModel tableModel;
    private final List<Integer> userIds = new ArrayList<>();
    private static final String[] USER_COLUMNS = {"ID", "Username", "Email", "Name"};
    private final int currentUserId;

    private javax.swing.JPanel LogoutPanel;

    public AdminSettings(int currentUserId) {
        this.currentUserId = currentUserId;
        initComponents();
        tableModel = (DefaultTableModel) jTableBookings.getModel();
        tableModel.setColumnIdentifiers(USER_COLUMNS);
        setupLogoutPanel();
        setupPanelListeners();
        setupSearchByID();
        loadUsersFromDb(null);
        loadLoggedInUserDetails();
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
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

    private void loadLoggedInUserDetails() {
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            String sql = "SELECT u_id, username, email, name FROM users WHERE u_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, currentUserId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("u_id");
                        String username = nullToEmpty(rs.getString("username"));
                        String email = nullToEmpty(rs.getString("email"));
                        String name = nullToEmpty(rs.getString("name"));

                        UserID.setText("User ID: " + id);
                        UserName.setText("Username: " + username);
                        Name.setText("Name: " + name);
                        Email.setText("Email: " + email);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load current user info: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            try (PreparedStatement checkEmail = conn.prepareStatement("SELECT u_id FROM users WHERE email = ?")) {
                checkEmail.setString(1, email);
                try (ResultSet rs = checkEmail.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "Email already registered.", "Add User", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO users (u_type, username, email, name, password) VALUES ('user', ?, ?, ?, ?)")) {
                ps.setString(1, username);
                ps.setString(2, email);
                ps.setString(3, name);
                ps.setString(4, PasswordUtil.hashPassword(password));
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
        if (row < 0 || row >= tableModel.getRowCount()) {
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
        int id = (Integer) tableModel.getValueAt(row, 0);
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
            try (PreparedStatement checkEmail = conn.prepareStatement("SELECT u_id FROM users WHERE email = ? AND u_id != ?")) {
                checkEmail.setString(1, email);
                checkEmail.setInt(2, id);
                try (ResultSet rs = checkEmail.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "Email already registered. Choose another.", "Edit User", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }
            if (!newPassword.isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement("UPDATE users SET username = ?, email = ?, name = ?, password = ? WHERE u_id = ?")) {
                    ps.setString(1, username);
                    ps.setString(2, email);
                    ps.setString(3, name);
                    ps.setString(4, PasswordUtil.hashPassword(newPassword));
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
            loadUsersFromDb(null);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectionConfig.close(conn);
        }
    }

    private void performDeleteUser() {
        int row = jTableBookings.getSelectedRow();
        if (row < 0 || row >= tableModel.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        int id = (Integer) tableModel.getValueAt(row, 0);
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE u_id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            loadUsersFromDb(null);
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
        EditUserPanel = new javax.swing.JPanel();
        EditUserText = new javax.swing.JLabel();
        EditUserIcon = new javax.swing.JLabel();
        Search = new javax.swing.JTextField();
        AddUserPanel = new javax.swing.JPanel();
        AddUserText = new javax.swing.JLabel();
        AddUserIcon = new javax.swing.JLabel();
        RemoveUserPanel = new javax.swing.JPanel();
        RemoveUserText = new javax.swing.JLabel();
        RemoveUserIcon = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        UserOutPanel = new javax.swing.JPanel();
        UserPanel = new javax.swing.JPanel();
        UserIcon = new javax.swing.JLabel();
        UserName = new javax.swing.JLabel();
        Name = new javax.swing.JLabel();
        UserID = new javax.swing.JLabel();
        Email = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(790, 415));

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

        mainPanel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 510, 320));

        EditUserPanel.setBackground(new java.awt.Color(204, 204, 255));

        EditUserText.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        EditUserText.setText("EDIT USER");

        EditUserIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EditUserIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-edit-profile-48.png"))); // NOI18N

        javax.swing.GroupLayout EditUserPanelLayout = new javax.swing.GroupLayout(EditUserPanel);
        EditUserPanel.setLayout(EditUserPanelLayout);
        EditUserPanelLayout.setHorizontalGroup(
            EditUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EditUserPanelLayout.createSequentialGroup()
                .addComponent(EditUserIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(EditUserText, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE))
        );
        EditUserPanelLayout.setVerticalGroup(
            EditUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EditUserPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(EditUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(EditUserText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EditUserIcon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        mainPanel.add(EditUserPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 160, 60));

        Search.setBackground(new java.awt.Color(204, 204, 255));
        Search.setForeground(new java.awt.Color(255, 255, 255));
        Search.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Search.setText("Enter user ID to search");
        Search.setBorder(null);
        Search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchActionPerformed(evt);
            }
        });
        mainPanel.add(Search, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, 220, 30));

        AddUserPanel.setBackground(new java.awt.Color(204, 204, 255));

        AddUserText.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        AddUserText.setText("ADD USER");

        AddUserIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        AddUserIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-add-user-group-woman-man-48.png"))); // NOI18N

        javax.swing.GroupLayout AddUserPanelLayout = new javax.swing.GroupLayout(AddUserPanel);
        AddUserPanel.setLayout(AddUserPanelLayout);
        AddUserPanelLayout.setHorizontalGroup(
            AddUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddUserPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(AddUserIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AddUserText, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        AddUserPanelLayout.setVerticalGroup(
            AddUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddUserPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(AddUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(AddUserText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AddUserIcon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        mainPanel.add(AddUserPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, -1, -1));

        RemoveUserPanel.setBackground(new java.awt.Color(204, 204, 255));

        RemoveUserText.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        RemoveUserText.setText("REMOVE USER");

        RemoveUserIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        RemoveUserIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-remove-user-48.png"))); // NOI18N

        javax.swing.GroupLayout RemoveUserPanelLayout = new javax.swing.GroupLayout(RemoveUserPanel);
        RemoveUserPanel.setLayout(RemoveUserPanelLayout);
        RemoveUserPanelLayout.setHorizontalGroup(
            RemoveUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RemoveUserPanelLayout.createSequentialGroup()
                .addComponent(RemoveUserIcon, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RemoveUserText, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        RemoveUserPanelLayout.setVerticalGroup(
            RemoveUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RemoveUserPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(RemoveUserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(RemoveUserIcon, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                    .addComponent(RemoveUserText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        mainPanel.add(RemoveUserPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 10, -1, -1));
        mainPanel.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 40, 220, 20));

        UserOutPanel.setBackground(new java.awt.Color(153, 153, 255));

        UserPanel.setBackground(new java.awt.Color(102, 102, 255));

        UserIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        UserIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-profile-100.png"))); // NOI18N

        javax.swing.GroupLayout UserPanelLayout = new javax.swing.GroupLayout(UserPanel);
        UserPanel.setLayout(UserPanelLayout);
        UserPanelLayout.setHorizontalGroup(
            UserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(UserIcon, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
        );
        UserPanelLayout.setVerticalGroup(
            UserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(UserIcon, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
        );

        UserName.setText("Username:");

        Name.setText("Name:");

        UserID.setText("User ID:");

        Email.setText("Email:");

        javax.swing.GroupLayout UserOutPanelLayout = new javax.swing.GroupLayout(UserOutPanel);
        UserOutPanel.setLayout(UserOutPanelLayout);
        UserOutPanelLayout.setHorizontalGroup(
            UserOutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(UserPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(UserOutPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(UserOutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(UserName)
                    .addComponent(Name)
                    .addComponent(UserID)
                    .addComponent(Email))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        UserOutPanelLayout.setVerticalGroup(
            UserOutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UserOutPanelLayout.createSequentialGroup()
                .addComponent(UserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(UserName)
                .addGap(18, 18, 18)
                .addComponent(Name)
                .addGap(19, 19, 19)
                .addComponent(UserID)
                .addGap(18, 18, 18)
                .addComponent(Email)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        mainPanel.add(UserOutPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 60, 240, 330));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchActionPerformed
        performSearchByID();
    }//GEN-LAST:event_SearchActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AddUserIcon;
    private javax.swing.JPanel AddUserPanel;
    private javax.swing.JLabel AddUserText;
    private javax.swing.JLabel EditUserIcon;
    private javax.swing.JPanel EditUserPanel;
    private javax.swing.JLabel EditUserText;
    private javax.swing.JLabel Email;
    private javax.swing.JLabel Name;
    private javax.swing.JLabel RemoveUserIcon;
    private javax.swing.JPanel RemoveUserPanel;
    private javax.swing.JLabel RemoveUserText;
    private javax.swing.JTextField Search;
    private javax.swing.JLabel UserID;
    private javax.swing.JLabel UserIcon;
    private javax.swing.JLabel UserName;
    private javax.swing.JPanel UserOutPanel;
    private javax.swing.JPanel UserPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTableBookings;
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables
}
