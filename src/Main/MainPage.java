package Main;

import AdminInternalPages.AdminSettings;
import AdminInternalPages.Bookings;
import AdminInternalPages.TransportationPage;
import UserInternalPages.UserBookings;
import UserInternalPages.UserSettings;
import UserInternalPages.UserTransportationPage;
import java.awt.Cursor;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JInternalFrame;
import java.awt.Color;

public class MainPage extends javax.swing.JFrame {

    private final boolean isAdmin;
    private final int currentUserId;

    Color navcolor = new Color(204,204,255);
    Color bodycolor = new Color(153,153,255);
    Color staycolor = new Color(153,153,255);

    /** Admin dashboard (Transportation, Bookings, Settings). */
    public MainPage(boolean isAdmin, int currentUserId) {
        this.isAdmin = isAdmin;
        this.currentUserId = currentUserId;
        initComponents();
        setupPanelListeners();
        setupDesktopResizeListener();
    }

    private void setupDesktopResizeListener() {
        jDesktopPane1.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                fitAllInternalFrames();
            }
        });
    }

    private void fitAllInternalFrames() {
        int w = jDesktopPane1.getWidth();
        int h = jDesktopPane1.getHeight();
        for (JInternalFrame frame : jDesktopPane1.getAllFrames()) {
            if (frame.isVisible()) {
                frame.setBounds(0, 0, w, h);
            }
        }
    }

    private void setupPanelListeners() {
        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

        TRANSPORTATION.setCursor(handCursor);
        TRANSPORTATION.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isAdmin) {
                    openInternalFrame(new TransportationPage());
                } else {
                    openInternalFrame(new UserTransportationPage(currentUserId));
                }
            }
        });

        BOOKINGS.setCursor(handCursor);
        BOOKINGS.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isAdmin) {
                    openInternalFrame(new Bookings());
                } else {
                    openInternalFrame(new UserBookings(currentUserId));
                }
            }
        });

        SETTINGS.setCursor(handCursor);
        SETTINGS.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isAdmin) {
                    openInternalFrame(new AdminSettings());
                } else {
                    openInternalFrame(new UserSettings(currentUserId));
                }
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

    private void openInternalFrame(JInternalFrame frame) {
        jDesktopPane1.add(frame);
        frame.setBounds(0, 0, jDesktopPane1.getWidth(), jDesktopPane1.getHeight());
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException ex) { }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MainPanel = new javax.swing.JPanel();
        DashPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        LOGO = new javax.swing.JLabel();
        TRANSPORTATION = new javax.swing.JPanel();
        TRANSTEXT = new javax.swing.JLabel();
        TransLogo = new javax.swing.JLabel();
        BOOKINGS = new javax.swing.JPanel();
        BOOKINGTEXT = new javax.swing.JLabel();
        BookingLogo = new javax.swing.JLabel();
        SETTINGS = new javax.swing.JPanel();
        SETTINGSTEXT = new javax.swing.JLabel();
        SettingsLogo = new javax.swing.JLabel();
        UpperSep = new javax.swing.JSeparator();
        LowerSep = new javax.swing.JSeparator();
        LogoutPanel = new javax.swing.JPanel();
        LogoutText = new javax.swing.JLabel();
        LogoutLogo = new javax.swing.JLabel();
        jDesktopPane1 = new javax.swing.JDesktopPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        MainPanel.setBackground(new java.awt.Color(255, 255, 255));
        MainPanel.setBorder(new javax.swing.border.MatteBorder(null));
        MainPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        DashPanel.setBackground(new java.awt.Color(153, 153, 255));
        DashPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Bahnschrift", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("UNITED");
        DashPanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, 130, 20));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Bahnschrift", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("TRANSPORTATIONS");
        DashPanel.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, 140, 20));

        LOGO.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Logo Small.png"))); // NOI18N
        LOGO.setText("jLabel1");
        DashPanel.add(LOGO, new org.netbeans.lib.awtextra.AbsoluteConstraints(-210, 0, 420, 80));

        TRANSPORTATION.setBackground(new java.awt.Color(153, 153, 255));
        TRANSPORTATION.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TRANSPORTATIONMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                TRANSPORTATIONMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                TRANSPORTATIONMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                TRANSPORTATIONMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                TRANSPORTATIONMouseReleased(evt);
            }
        });

        TRANSTEXT.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        TRANSTEXT.setForeground(new java.awt.Color(255, 255, 255));
        TRANSTEXT.setText("TRANSPORTATION");

        TransLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        TransLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-public-transportation-48.png"))); // NOI18N

        javax.swing.GroupLayout TRANSPORTATIONLayout = new javax.swing.GroupLayout(TRANSPORTATION);
        TRANSPORTATION.setLayout(TRANSPORTATIONLayout);
        TRANSPORTATIONLayout.setHorizontalGroup(
            TRANSPORTATIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, TRANSPORTATIONLayout.createSequentialGroup()
                .addComponent(TransLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TRANSTEXT, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        TRANSPORTATIONLayout.setVerticalGroup(
            TRANSPORTATIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, TRANSPORTATIONLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(TRANSPORTATIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(TransLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(TRANSTEXT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        DashPanel.add(TRANSPORTATION, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 130, 210, -1));

        BOOKINGS.setBackground(new java.awt.Color(153, 153, 255));
        BOOKINGS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BOOKINGSMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                BOOKINGSMouseExited(evt);
            }
        });

        BOOKINGTEXT.setBackground(new java.awt.Color(153, 153, 153));
        BOOKINGTEXT.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        BOOKINGTEXT.setForeground(new java.awt.Color(255, 255, 255));
        BOOKINGTEXT.setText("BOOKINGS");

        BookingLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        BookingLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-booking-48.png"))); // NOI18N

        javax.swing.GroupLayout BOOKINGSLayout = new javax.swing.GroupLayout(BOOKINGS);
        BOOKINGS.setLayout(BOOKINGSLayout);
        BOOKINGSLayout.setHorizontalGroup(
            BOOKINGSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BOOKINGSLayout.createSequentialGroup()
                .addComponent(BookingLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BOOKINGTEXT, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        BOOKINGSLayout.setVerticalGroup(
            BOOKINGSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BOOKINGSLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(BOOKINGSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(BookingLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BOOKINGTEXT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        DashPanel.add(BOOKINGS, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 180, 210, -1));

        SETTINGS.setBackground(new java.awt.Color(153, 153, 255));
        SETTINGS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                SETTINGSMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                SETTINGSMouseExited(evt);
            }
        });

        SETTINGSTEXT.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        SETTINGSTEXT.setForeground(new java.awt.Color(255, 255, 255));
        SETTINGSTEXT.setText("SETTINGS");

        SettingsLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SettingsLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-settings-50.png"))); // NOI18N

        javax.swing.GroupLayout SETTINGSLayout = new javax.swing.GroupLayout(SETTINGS);
        SETTINGS.setLayout(SETTINGSLayout);
        SETTINGSLayout.setHorizontalGroup(
            SETTINGSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SETTINGSLayout.createSequentialGroup()
                .addComponent(SettingsLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SETTINGSTEXT, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        SETTINGSLayout.setVerticalGroup(
            SETTINGSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SETTINGSLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(SETTINGSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(SettingsLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(SETTINGSTEXT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        DashPanel.add(SETTINGS, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 210, -1));

        UpperSep.setBackground(new java.awt.Color(204, 204, 255));
        DashPanel.add(UpperSep, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 190, 10));

        LowerSep.setBackground(new java.awt.Color(204, 204, 255));
        DashPanel.add(LowerSep, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, 190, -1));

        LogoutPanel.setBackground(new java.awt.Color(153, 153, 255));
        LogoutPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                LogoutPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                LogoutPanelMouseExited(evt);
            }
        });

        LogoutText.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        LogoutText.setForeground(new java.awt.Color(255, 255, 255));
        LogoutText.setText("LOGOUT");

        LogoutLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LogoutLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icons8-logout-48.png"))); // NOI18N

        javax.swing.GroupLayout LogoutPanelLayout = new javax.swing.GroupLayout(LogoutPanel);
        LogoutPanel.setLayout(LogoutPanelLayout);
        LogoutPanelLayout.setHorizontalGroup(
            LogoutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LogoutPanelLayout.createSequentialGroup()
                .addComponent(LogoutLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(LogoutText, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
        );
        LogoutPanelLayout.setVerticalGroup(
            LogoutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LogoutLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
            .addComponent(LogoutText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        DashPanel.add(LogoutPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 340, 210, 50));

        MainPanel.add(DashPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 210, 420));

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 790, Short.MAX_VALUE)
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 420, Short.MAX_VALUE)
        );

        MainPanel.add(jDesktopPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 0, 790, 420));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TRANSPORTATIONMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TRANSPORTATIONMouseEntered
       TRANSPORTATION.setBackground(navcolor);
    }//GEN-LAST:event_TRANSPORTATIONMouseEntered

    private void TRANSPORTATIONMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TRANSPORTATIONMouseExited
        TRANSPORTATION.setBackground(bodycolor);
    }//GEN-LAST:event_TRANSPORTATIONMouseExited

    private void TRANSPORTATIONMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TRANSPORTATIONMousePressed
        TRANSPORTATION.setBackground(navcolor);
    }//GEN-LAST:event_TRANSPORTATIONMousePressed

    private void TRANSPORTATIONMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TRANSPORTATIONMouseClicked
        TRANSPORTATION.setBackground(navcolor);
    }//GEN-LAST:event_TRANSPORTATIONMouseClicked

    private void TRANSPORTATIONMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TRANSPORTATIONMouseReleased
        TRANSPORTATION.setBackground(navcolor);// TODO add your handling code here:
    }//GEN-LAST:event_TRANSPORTATIONMouseReleased

    private void BOOKINGSMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOOKINGSMouseEntered
        BOOKINGS.setBackground(navcolor);
    }//GEN-LAST:event_BOOKINGSMouseEntered

    private void BOOKINGSMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BOOKINGSMouseExited
        BOOKINGS.setBackground(bodycolor);
    }//GEN-LAST:event_BOOKINGSMouseExited

    private void SETTINGSMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SETTINGSMouseEntered
        SETTINGS.setBackground(navcolor);
    }//GEN-LAST:event_SETTINGSMouseEntered

    private void SETTINGSMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SETTINGSMouseExited
       SETTINGS.setBackground(bodycolor);
    }//GEN-LAST:event_SETTINGSMouseExited

    private void LogoutPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LogoutPanelMouseEntered
        LogoutPanel.setBackground(navcolor);
    }//GEN-LAST:event_LogoutPanelMouseEntered

    private void LogoutPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LogoutPanelMouseExited
       LogoutPanel.setBackground(bodycolor);
    }//GEN-LAST:event_LogoutPanelMouseExited

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(MainPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new MainPage(true, 1).setVisible(true));
    }

    private void performLogout() {
        dispose();
        Mainframe mainframe = new Mainframe();
        mainframe.setLocationRelativeTo(null);
        mainframe.setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BOOKINGS;
    private javax.swing.JLabel BOOKINGTEXT;
    private javax.swing.JLabel BookingLogo;
    private javax.swing.JPanel DashPanel;
    private javax.swing.JLabel LOGO;
    private javax.swing.JLabel LogoutLogo;
    private javax.swing.JPanel LogoutPanel;
    private javax.swing.JLabel LogoutText;
    private javax.swing.JSeparator LowerSep;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JPanel SETTINGS;
    private javax.swing.JLabel SETTINGSTEXT;
    private javax.swing.JLabel SettingsLogo;
    private javax.swing.JPanel TRANSPORTATION;
    private javax.swing.JLabel TRANSTEXT;
    private javax.swing.JLabel TransLogo;
    private javax.swing.JSeparator UpperSep;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
}
