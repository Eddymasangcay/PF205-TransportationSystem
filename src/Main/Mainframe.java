package Main;

import Configuration.ConnectionConfig;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Mainframe extends javax.swing.JFrame {

    public Mainframe() {
        initComponents();
        setupPanelListeners();
    }

    private void setupPanelListeners() {
        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

        LoginBtnPanel.setCursor(handCursor);
        LoginBtnPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                performLogin();
            }
        });

        RegisterPanel.setCursor(handCursor);
        RegisterPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openRegisterFrame();
            }
        });
    }

    private void performLogin() {
        String username = UsernameField.getText().trim();
        String password = PasswordField.getText().trim();
        if (username.isEmpty() || password.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Please enter username and password.",
                "Login",
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        Connection conn = null;
        try {
            conn = ConnectionConfig.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT u_id FROM users WHERE username = ? AND password = ?")) {
                ps.setString(1, username);
                ps.setString(2, password);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        javax.swing.JOptionPane.showMessageDialog(this,
                            "Invalid username or password.",
                            "Login Failed",
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }
            dispose();
            MainPage mainPage = new MainPage();
            mainPage.setLocationRelativeTo(null);
            mainPage.setVisible(true);
        } catch (SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(),
                "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectionConfig.close(conn);
        }
    }

    private void openRegisterFrame() {
        dispose();
        RegisterFrame registerFrame = new RegisterFrame();
        registerFrame.setLocationRelativeTo(null);
        registerFrame.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MainFrame = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        UsernameText = new javax.swing.JLabel();
        UsernameField = new javax.swing.JTextField();
        PasswordText = new javax.swing.JLabel();
        PasswordField = new javax.swing.JTextField();
        LoginBtnPanel = new javax.swing.JPanel();
        LoginText = new javax.swing.JLabel();
        RegisterPanel = new javax.swing.JPanel();
        RegisterText = new javax.swing.JLabel();
        Extra = new javax.swing.JLabel();
        Welcoming = new javax.swing.JLabel();
        Logo = new javax.swing.JLabel();
        Background = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        MainFrame.setBackground(new java.awt.Color(102, 102, 102));
        MainFrame.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(153, 153, 153));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        UsernameText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        UsernameText.setText("USERNAME");
        jPanel3.add(UsernameText, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 140, 170, 20));

        UsernameField.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        UsernameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UsernameFieldActionPerformed(evt);
            }
        });
        jPanel3.add(UsernameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 160, 170, -1));

        PasswordText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        PasswordText.setText("PASSWORD");
        jPanel3.add(PasswordText, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 190, 170, 20));

        PasswordField.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        PasswordField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PasswordFieldActionPerformed(evt);
            }
        });
        jPanel3.add(PasswordField, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 210, 170, -1));

        LoginBtnPanel.setBackground(new java.awt.Color(204, 204, 204));
        LoginBtnPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        LoginText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LoginText.setText("LOGIN");
        LoginText.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout LoginBtnPanelLayout = new javax.swing.GroupLayout(LoginBtnPanel);
        LoginBtnPanel.setLayout(LoginBtnPanelLayout);
        LoginBtnPanelLayout.setHorizontalGroup(
            LoginBtnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LoginText, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
        );
        LoginBtnPanelLayout.setVerticalGroup(
            LoginBtnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LoginText, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
        );

        jPanel3.add(LoginBtnPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 250, 130, 30));

        RegisterPanel.setBackground(new java.awt.Color(153, 153, 153));

        RegisterText.setBackground(new java.awt.Color(153, 153, 153));
        RegisterText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        RegisterText.setText("Register Now!");

        javax.swing.GroupLayout RegisterPanelLayout = new javax.swing.GroupLayout(RegisterPanel);
        RegisterPanel.setLayout(RegisterPanelLayout);
        RegisterPanelLayout.setHorizontalGroup(
            RegisterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(RegisterText, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
        );
        RegisterPanelLayout.setVerticalGroup(
            RegisterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RegisterPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(RegisterText, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.add(RegisterPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 310, 310, 20));

        Extra.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Extra.setText("Doesn't have an account? ");
        jPanel3.add(Extra, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 290, 180, -1));

        Welcoming.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        Welcoming.setText("Welcome to United Transportation System!");
        jPanel3.add(Welcoming, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, -1, -1));

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

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Mainframe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Mainframe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Mainframe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Mainframe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new Mainframe().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Background;
    private javax.swing.JLabel Extra;
    private javax.swing.JPanel LoginBtnPanel;
    private javax.swing.JLabel LoginText;
    private javax.swing.JLabel Logo;
    private javax.swing.JPanel MainFrame;
    private javax.swing.JTextField PasswordField;
    private javax.swing.JLabel PasswordText;
    private javax.swing.JPanel RegisterPanel;
    private javax.swing.JLabel RegisterText;
    private javax.swing.JTextField UsernameField;
    private javax.swing.JLabel UsernameText;
    private javax.swing.JLabel Welcoming;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
}
