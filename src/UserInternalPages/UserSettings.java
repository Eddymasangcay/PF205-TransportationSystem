package UserInternalPages;

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
import javax.swing.table.DefaultTableModel;

public class UserSettings extends InternalPageFrame {

    private final DefaultTableModel tableModel;
    private final List<Integer> userIds = new ArrayList<>();
    private static final String[] USER_COLUMNS = {"ID", "Username", "Email", "Name"};
    private final int currentUserId;
    private javax.swing.JTable jTableBookings;

    private javax.swing.JPanel LogoutPanel;

    public UserSettings(int currentUserId) {
        this.currentUserId = currentUserId;
        initComponents();
        tableModel = new DefaultTableModel(USER_COLUMNS, 0);
        jTableBookings = new javax.swing.JTable(tableModel);
        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(jTableBookings);
        jDesktopPane1.setVisible(false);
        mainPanel.add(scroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 10, 560, 200));
        setupLogoutPanel();
        setupPanelListeners();
        loadCurrentUser();
    }

    private void loadCurrentUser() {
        loadUsersFromDb(currentUserId);
    }

    private void loadUsersFromDb(Integer filterById) {
        tableModel.setRowCount(0);
        userIds.clear();
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            int id = filterById != null ? filterById : currentUserId;
            String sql = "SELECT u_id, username, email, name FROM users WHERE u_id = ? ORDER BY u_id";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
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
            JOptionPane.showMessageDialog(this, "Failed to load profile: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectionConfig.close(conn);
        }
        updateUserInfoPanel();
    }

    private void updateUserInfoPanel() {
        if (tableModel == null || tableModel.getRowCount() == 0) {
            return;
        }
        int row = 0;
        Object id = tableModel.getValueAt(row, 0);
        Object username = tableModel.getValueAt(row, 1);
        Object email = tableModel.getValueAt(row, 2);
        Object name = tableModel.getValueAt(row, 3);
    
        UserIdLabel.setText("User ID: " + String.valueOf(id));
        UsernameLabel.setText("Username: " + String.valueOf(username));
        NameLabel.setText("Name: " + String.valueOf(name));
        EmailLabel.setText("Email: " + String.valueOf(email));
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
        mainPanel.add(LogoutPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 10, 90, 30));
    }

    private void setupPanelListeners() {
        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

        EditPasswordPanel.setCursor(handCursor);
        EditPasswordPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                performChangePassword();
            }
        });

        BookingHistoryPanel.setCursor(handCursor);
        BookingHistoryPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openMyBookings();
            }
        });

        ViewReceiptsPanel.setCursor(handCursor);
        ViewReceiptsPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                performViewReceipts();
            }
        });
        if (jTableBookings != null) {
            jTableBookings.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 2 && jTableBookings.getSelectedRow() >= 0) {
                        performEditProfile();
                    }
                }
            });
        }

        LogoutPanel.setCursor(handCursor);
        LogoutPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                performLogout();
            }
        });

        // Add Edit Profile panel programmatically
        setupEditProfilePanel(handCursor);
    }

    private javax.swing.JPanel EditProfilePanel;
    Color navcolor = new Color(153, 153, 255);
    Color bodycolor = new Color(204, 204, 255);

    private void setupEditProfilePanel(Cursor handCursor) {
        EditProfilePanel = new javax.swing.JPanel();
        EditProfilePanel.setBackground(bodycolor);
        EditProfilePanel.setCursor(handCursor);
        
        javax.swing.JLabel editText = new javax.swing.JLabel("EDIT PROFILE");
        editText.setFont(new java.awt.Font("Tahoma", 1, 11));
        
        javax.swing.JLabel editIcon = new javax.swing.JLabel();
        editIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        editIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-edit-property-48.png")));
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(EditProfilePanel);
        EditProfilePanel.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(editIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editText)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(editIcon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        
        mainPanel.add(EditProfilePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 250, 180, 60));
        
        EditProfilePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                performEditProfile();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                EditProfilePanel.setBackground(navcolor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                EditProfilePanel.setBackground(bodycolor);
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

    private void openMyBookings() {
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            javax.swing.table.DefaultTableModel tm = new javax.swing.table.DefaultTableModel(
                    new String[]{"Booking ID", "Route", "Seat", "Date", "Status"}, 0);
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT b_id, route, seat, date, status FROM bookings WHERE passenger_id = ? ORDER BY b_id DESC")) {
                ps.setInt(1, currentUserId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        tm.addRow(new Object[]{
                                rs.getInt("b_id"),
                                nullToEmpty(rs.getString("route")),
                                nullToEmpty(rs.getString("seat")),
                                nullToEmpty(rs.getString("date")),
                                nullToEmpty(rs.getString("status"))
                        });
                    }
                }
            }
            javax.swing.JTable table = new javax.swing.JTable(tm);
            table.setEnabled(false);
            javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(table);
            scroll.setPreferredSize(new java.awt.Dimension(500, 300));
            JOptionPane.showMessageDialog(this, scroll, "My Booking History", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load booking history: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectionConfig.close(conn);
        }
    }

    private void performViewReceipts() {
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            javax.swing.table.DefaultTableModel tm = new javax.swing.table.DefaultTableModel(
                    new String[]{"Receipt ID", "Booking ID", "Origin", "Destination", "Seat", "Date"}, 0);
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT r_id, b_id, origin, destination, seat, date FROM receipts WHERE u_id = ? ORDER BY r_id DESC")) {
                ps.setInt(1, currentUserId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        tm.addRow(new Object[]{
                                rs.getInt("r_id"),
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
            scroll.setPreferredSize(new java.awt.Dimension(500, 300));
            JOptionPane.showMessageDialog(this, scroll, "My Receipts", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load receipts: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectionConfig.close(conn);
        }
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private void performEditProfile() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Profile not loaded.", "Settings", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int row = 0;
        JTextField usernameField = new JTextField(String.valueOf(tableModel.getValueAt(row, 1)), 20);
        JTextField emailField = new JTextField(String.valueOf(tableModel.getValueAt(row, 2)), 20);
        JTextField nameField = new JTextField(String.valueOf(tableModel.getValueAt(row, 3)), 20);
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new javax.swing.JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new javax.swing.JLabel("Email:"));
        panel.add(emailField);
        panel.add(new javax.swing.JLabel("Name:"));
        panel.add(nameField);
        if (JOptionPane.showConfirmDialog(this, panel, "Edit Profile", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String name = nameField.getText().trim();
        if (username.isEmpty() || email.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username, email and name are required.", "Edit Profile", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            try (PreparedStatement check = conn.prepareStatement("SELECT u_id FROM users WHERE username = ? AND u_id != ?")) {
                check.setString(1, username);
                check.setInt(2, currentUserId);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "Username already in use. Choose another.", "Edit Profile", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }
            try (PreparedStatement checkEmail = conn.prepareStatement("SELECT u_id FROM users WHERE email = ? AND u_id != ?")) {
                checkEmail.setString(1, email);
                checkEmail.setInt(2, currentUserId);
                try (ResultSet rs = checkEmail.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "Email already registered. Choose another.", "Edit Profile", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement("UPDATE users SET username = ?, email = ?, name = ? WHERE u_id = ?")) {
                ps.setString(1, username);
                ps.setString(2, email);
                ps.setString(3, name);
                ps.setInt(4, currentUserId);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Profile updated successfully.", "Edit Profile", JOptionPane.INFORMATION_MESSAGE);
            loadCurrentUser();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update profile: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectionConfig.close(conn);
        }
    }

    private void performChangePassword() {
        JTextField currentPasswordField = new JTextField(20);
        JTextField newPasswordField = new JTextField(20);
        JTextField confirmField = new JTextField(20);
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new javax.swing.JLabel("Current password:"));
        panel.add(currentPasswordField);
        panel.add(new javax.swing.JLabel("New password:"));
        panel.add(newPasswordField);
        panel.add(new javax.swing.JLabel("Confirm new password:"));
        panel.add(confirmField);
        if (JOptionPane.showConfirmDialog(this, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText().trim();
        String confirm = confirmField.getText();
        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "New password cannot be empty.", "Change Password", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!newPassword.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "New password and confirmation do not match.", "Change Password", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            try (PreparedStatement ps = conn.prepareStatement("SELECT password FROM users WHERE u_id = ?")) {
                ps.setInt(1, currentUserId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || !PasswordUtil.verifyPassword(currentPassword, rs.getString("password"))) {
                        JOptionPane.showMessageDialog(this, "Current password is incorrect.", "Change Password", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement("UPDATE users SET password = ? WHERE u_id = ?")) {
                ps.setString(1, PasswordUtil.hashPassword(newPassword));
                ps.setInt(2, currentUserId);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Password changed successfully.", "Change Password", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to change password: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectionConfig.close(conn);
        }
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
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, email, name, password) VALUES (?, ?, ?, ?)")) {
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
        EditPasswordPanel = new javax.swing.JPanel();
        EditUserIcon = new javax.swing.JLabel();
        EditUserText = new javax.swing.JLabel();
        BookingHistoryPanel = new javax.swing.JPanel();
        EditUserText1 = new javax.swing.JLabel();
        EditUserIcon2 = new javax.swing.JLabel();
        ViewReceiptsPanel = new javax.swing.JPanel();
        EditUserIcon1 = new javax.swing.JLabel();
        EditUserText2 = new javax.swing.JLabel();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        UserPanel = new javax.swing.JPanel();
        UserPanel2 = new javax.swing.JPanel();
        UserIcon1 = new javax.swing.JLabel();
        UserIdLabel = new javax.swing.JLabel();
        UsernameLabel = new javax.swing.JLabel();
        NameLabel = new javax.swing.JLabel();
        EmailLabel = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(790, 415));

        mainPanel.setBackground(new java.awt.Color(204, 204, 255));
        mainPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        EditPasswordPanel.setBackground(new java.awt.Color(204, 204, 255));

        EditUserIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EditUserIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-edit-profile-48.png"))); // NOI18N

        EditUserText.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        EditUserText.setText("EDIT PASSWORD");

        javax.swing.GroupLayout EditPasswordPanelLayout = new javax.swing.GroupLayout(EditPasswordPanel);
        EditPasswordPanel.setLayout(EditPasswordPanelLayout);
        EditPasswordPanelLayout.setHorizontalGroup(
            EditPasswordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EditPasswordPanelLayout.createSequentialGroup()
                .addComponent(EditUserIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(EditUserText)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        EditPasswordPanelLayout.setVerticalGroup(
            EditPasswordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EditPasswordPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(EditPasswordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(EditUserIcon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EditUserText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        mainPanel.add(EditPasswordPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 180, 60));

        BookingHistoryPanel.setBackground(new java.awt.Color(204, 204, 255));

        EditUserText1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        EditUserText1.setText("BOOKING HISTORY");

        EditUserIcon2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EditUserIcon2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-edit-profile-48.png"))); // NOI18N

        javax.swing.GroupLayout BookingHistoryPanelLayout = new javax.swing.GroupLayout(BookingHistoryPanel);
        BookingHistoryPanel.setLayout(BookingHistoryPanelLayout);
        BookingHistoryPanelLayout.setHorizontalGroup(
            BookingHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BookingHistoryPanelLayout.createSequentialGroup()
                .addComponent(EditUserIcon2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(EditUserText1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        BookingHistoryPanelLayout.setVerticalGroup(
            BookingHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BookingHistoryPanelLayout.createSequentialGroup()
                .addGroup(BookingHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(EditUserText1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EditUserIcon2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        mainPanel.add(BookingHistoryPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 90, 180, 60));

        ViewReceiptsPanel.setBackground(new java.awt.Color(204, 204, 255));

        EditUserIcon1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EditUserIcon1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-edit-profile-48.png"))); // NOI18N

        EditUserText2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        EditUserText2.setText("VIEW RECEIPTS");

        javax.swing.GroupLayout ViewReceiptsPanelLayout = new javax.swing.GroupLayout(ViewReceiptsPanel);
        ViewReceiptsPanel.setLayout(ViewReceiptsPanelLayout);
        ViewReceiptsPanelLayout.setHorizontalGroup(
            ViewReceiptsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ViewReceiptsPanelLayout.createSequentialGroup()
                .addComponent(EditUserIcon1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(EditUserText2, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE))
        );
        ViewReceiptsPanelLayout.setVerticalGroup(
            ViewReceiptsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ViewReceiptsPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(ViewReceiptsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(EditUserIcon1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EditUserText2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        mainPanel.add(ViewReceiptsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, 180, 60));

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 560, Short.MAX_VALUE)
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        mainPanel.add(jDesktopPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 10, 560, 200));

        UserPanel.setBackground(new java.awt.Color(153, 153, 255));

        UserPanel2.setBackground(new java.awt.Color(102, 102, 255));

        UserIcon1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        UserIcon1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-profile-100.png"))); // NOI18N

        javax.swing.GroupLayout UserPanel2Layout = new javax.swing.GroupLayout(UserPanel2);
        UserPanel2.setLayout(UserPanel2Layout);
        UserPanel2Layout.setHorizontalGroup(
            UserPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(UserIcon1, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
        );
        UserPanel2Layout.setVerticalGroup(
            UserPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(UserIcon1, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
        );

        UserIdLabel.setText("User ID:");

        UsernameLabel.setText("Username:");

        NameLabel.setText("Name:");

        EmailLabel.setText("Email:");

        javax.swing.GroupLayout UserPanelLayout = new javax.swing.GroupLayout(UserPanel);
        UserPanel.setLayout(UserPanelLayout);
        UserPanelLayout.setHorizontalGroup(
            UserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UserPanelLayout.createSequentialGroup()
                .addComponent(UserPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(UserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(UsernameLabel)
                    .addComponent(NameLabel)
                    .addComponent(UserIdLabel)
                    .addComponent(EmailLabel))
                .addContainerGap(240, Short.MAX_VALUE))
        );
        UserPanelLayout.setVerticalGroup(
            UserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(UserPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(UserPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(UsernameLabel)
                .addGap(18, 18, 18)
                .addComponent(NameLabel)
                .addGap(18, 18, 18)
                .addComponent(UserIdLabel)
                .addGap(18, 18, 18)
                .addComponent(EmailLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainPanel.add(UserPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 230, 560, 170));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 790, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BookingHistoryPanel;
    private javax.swing.JPanel EditPasswordPanel;
    private javax.swing.JLabel EditUserIcon;
    private javax.swing.JLabel EditUserIcon1;
    private javax.swing.JLabel EditUserIcon2;
    private javax.swing.JLabel EditUserText;
    private javax.swing.JLabel EditUserText1;
    private javax.swing.JLabel EditUserText2;
    private javax.swing.JLabel UserIcon1;
    private javax.swing.JPanel UserPanel;
    private javax.swing.JPanel UserPanel2;
    private javax.swing.JLabel UserIdLabel;
    private javax.swing.JLabel UsernameLabel;
    private javax.swing.JLabel NameLabel;
    private javax.swing.JLabel EmailLabel;
    private javax.swing.JPanel ViewReceiptsPanel;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables
}
