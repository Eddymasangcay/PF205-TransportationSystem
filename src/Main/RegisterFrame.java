package Main;

import Configuration.ConnectionConfig;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterFrame extends javax.swing.JFrame {

    public RegisterFrame() {
        initComponents();
        setupPanelListeners();
    }

    private void setupPanelListeners() {
        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

        LoginBtnPanel.setCursor(handCursor);
        LoginBtnPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                performRegister();
            }
        });
    }

    private void performRegister() {
        String username = UsernameField.getText().trim();
        String email = EmailField.getText().trim();
        String name = NameField.getText().trim();
        String password = PasswordField.getText().trim();

        if (username.isEmpty() || email.isEmpty() || name.isEmpty() || password.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Please fill in all fields.",
                "Registration",
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            try (PreparedStatement checkUser = conn.prepareStatement("SELECT u_id FROM users WHERE username = ?")) {
                checkUser.setString(1, username);
                try (ResultSet rs = checkUser.executeQuery()) {
                    if (rs.next()) {
                        javax.swing.JOptionPane.showMessageDialog(this,
                            "Username already exists. Please choose another.",
                            "Registration",
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }
            try (PreparedStatement checkEmail = conn.prepareStatement("SELECT u_id FROM users WHERE email = ?")) {
                checkEmail.setString(1, email);
                try (ResultSet rs = checkEmail.executeQuery()) {
                    if (rs.next()) {
                        javax.swing.JOptionPane.showMessageDialog(this,
                            "Email already registered. Please use another or log in.",
                            "Registration",
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (username, email, name, password) VALUES (?, ?, ?, ?)")) {
                ps.setString(1, username);
                ps.setString(2, email);
                ps.setString(3, name);
                ps.setString(4, password);
                ps.executeUpdate();
            }
            javax.swing.JOptionPane.showMessageDialog(this,
                "Registration successful! You can now log in.",
                "Registration",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            dispose();
            Mainframe mainframe = new Mainframe();
            mainframe.setLocationRelativeTo(null);
            mainframe.setVisible(true);
        } catch (SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(),
                "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectionConfig.close(conn);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MainFrame = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        Welcoming = new javax.swing.JLabel();
        Welcoming1 = new javax.swing.JLabel();
        Welcoming2 = new javax.swing.JLabel();
        EnterUsernameText = new javax.swing.JLabel();
        UsernameField = new javax.swing.JTextField();
        EnterEmailText = new javax.swing.JLabel();
        EmailField = new javax.swing.JTextField();
        EnterNameTest = new javax.swing.JLabel();
        NameField = new javax.swing.JTextField();
        EnterPasswordText = new javax.swing.JLabel();
        PasswordField = new javax.swing.JTextField();
        LoginBtnPanel = new javax.swing.JPanel();
        LoginText = new javax.swing.JLabel();
        Logo = new javax.swing.JLabel();
        Background = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        MainFrame.setBackground(new java.awt.Color(102, 102, 102));
        MainFrame.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(153, 153, 153));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Welcoming.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        Welcoming.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Welcoming.setText("Welcome to United Transportation System!");
        jPanel3.add(Welcoming, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 310, -1));

        Welcoming1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        Welcoming1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Welcoming1.setText("Please fill out the form to complete");
        jPanel3.add(Welcoming1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 310, -1));

        Welcoming2.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        Welcoming2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Welcoming2.setText("Registration, Thank you!");
        jPanel3.add(Welcoming2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 310, -1));

        EnterUsernameText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EnterUsernameText.setText("USERNAME");
        jPanel3.add(EnterUsernameText, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 70, 20));

        UsernameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UsernameFieldActionPerformed(evt);
            }
        });
        jPanel3.add(UsernameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 140, 230, -1));

        EnterEmailText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EnterEmailText.setText("EMAIL");
        jPanel3.add(EnterEmailText, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 180, 70, 20));

        EmailField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EmailFieldActionPerformed(evt);
            }
        });
        jPanel3.add(EmailField, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 180, 230, -1));

        EnterNameTest.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EnterNameTest.setText("NAME");
        jPanel3.add(EnterNameTest, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 220, 70, 20));

        NameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NameFieldActionPerformed(evt);
            }
        });
        jPanel3.add(NameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 220, 230, -1));

        EnterPasswordText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EnterPasswordText.setText("PASSWORD");
        jPanel3.add(EnterPasswordText, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 260, 70, 20));

        PasswordField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PasswordFieldActionPerformed(evt);
            }
        });
        jPanel3.add(PasswordField, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 260, 230, -1));

        LoginBtnPanel.setBackground(new java.awt.Color(204, 204, 204));

        LoginText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LoginText.setText("REGISTER");
        LoginText.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout LoginBtnPanelLayout = new javax.swing.GroupLayout(LoginBtnPanel);
        LoginBtnPanel.setLayout(LoginBtnPanelLayout);
        LoginBtnPanelLayout.setHorizontalGroup(
            LoginBtnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LoginText, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
        );
        LoginBtnPanelLayout.setVerticalGroup(
            LoginBtnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LoginText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );

        jPanel3.add(LoginBtnPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 320, 180, 40));

        MainFrame.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 0, 310, 420));

        Logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Logo with Blue and Grey Color Theme.png"))); // NOI18N
        MainFrame.add(Logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 70, -1, -1));

        Background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Copy of Welcome to United Transporat.png"))); // NOI18N
        Background.setText("jLabel3");
        MainFrame.add(Background, new org.netbeans.lib.awtextra.AbsoluteConstraints(-20, 70, 380, 420));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(MainFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void UsernameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UsernameFieldActionPerformed
    }//GEN-LAST:event_UsernameFieldActionPerformed

    private void PasswordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PasswordFieldActionPerformed
    }//GEN-LAST:event_PasswordFieldActionPerformed

    private void EmailFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EmailFieldActionPerformed
    }//GEN-LAST:event_EmailFieldActionPerformed

    private void NameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NameFieldActionPerformed
    }//GEN-LAST:event_NameFieldActionPerformed

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(RegisterFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new RegisterFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Background;
    private javax.swing.JTextField EmailField;
    private javax.swing.JLabel EnterEmailText;
    private javax.swing.JLabel EnterNameTest;
    private javax.swing.JLabel EnterPasswordText;
    private javax.swing.JLabel EnterUsernameText;
    private javax.swing.JPanel LoginBtnPanel;
    private javax.swing.JLabel LoginText;
    private javax.swing.JLabel Logo;
    private javax.swing.JPanel MainFrame;
    private javax.swing.JTextField NameField;
    private javax.swing.JTextField PasswordField;
    private javax.swing.JTextField UsernameField;
    private javax.swing.JLabel Welcoming;
    private javax.swing.JLabel Welcoming1;
    private javax.swing.JLabel Welcoming2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
}
