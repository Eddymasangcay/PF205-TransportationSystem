package Main;

import InternalPages.AdminPage;
import InternalPages.Bookings;
import InternalPages.TransportationPage;
import java.awt.Cursor;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JInternalFrame;

public class MainPage extends javax.swing.JFrame {

    public MainPage() {
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
                openInternalFrame(new TransportationPage());
            }
        });

        BOOKINGS.setCursor(handCursor);
        BOOKINGS.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openInternalFrame(new Bookings());
            }
        });

        jPanel9.setCursor(handCursor);
        jPanel9.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openInternalFrame(new AdminPage());
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
        TRANSPORTATION = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        BOOKINGS = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        LOGO = new javax.swing.JLabel();
        jDesktopPane1 = new javax.swing.JDesktopPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        MainPanel.setBackground(new java.awt.Color(255, 255, 255));
        MainPanel.setBorder(new javax.swing.border.MatteBorder(null));
        MainPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        DashPanel.setBackground(new java.awt.Color(102, 102, 102));
        DashPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        DashPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        TRANSPORTATION.setBackground(new java.awt.Color(102, 102, 102));
        TRANSPORTATION.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("TRANSPORTATION");

        javax.swing.GroupLayout TRANSPORTATIONLayout = new javax.swing.GroupLayout(TRANSPORTATION);
        TRANSPORTATION.setLayout(TRANSPORTATIONLayout);
        TRANSPORTATIONLayout.setHorizontalGroup(
            TRANSPORTATIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
        );
        TRANSPORTATIONLayout.setVerticalGroup(
            TRANSPORTATIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
        );

        DashPanel.add(TRANSPORTATION, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 20, 120, 40));

        BOOKINGS.setBackground(new java.awt.Color(102, 102, 102));
        BOOKINGS.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setBackground(new java.awt.Color(153, 153, 153));
        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("BOOKINGS");

        javax.swing.GroupLayout BOOKINGSLayout = new javax.swing.GroupLayout(BOOKINGS);
        BOOKINGS.setLayout(BOOKINGSLayout);
        BOOKINGSLayout.setHorizontalGroup(
            BOOKINGSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
        );
        BOOKINGSLayout.setVerticalGroup(
            BOOKINGSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
        );

        DashPanel.add(BOOKINGS, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 20, 120, 40));

        jPanel9.setBackground(new java.awt.Color(102, 102, 102));
        jPanel9.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("SETTINGS");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
        );

        DashPanel.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 20, 120, 40));

        jLabel2.setFont(new java.awt.Font("Bahnschrift", 1, 12)); // NOI18N
        jLabel2.setText("UNITED");
        DashPanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, 50, -1));

        jLabel3.setFont(new java.awt.Font("Bahnschrift", 1, 12)); // NOI18N
        jLabel3.setText("TRANSPORTATIONS");
        DashPanel.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, 130, -1));

        LOGO.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Logo Small.png"))); // NOI18N
        LOGO.setText("jLabel1");
        DashPanel.add(LOGO, new org.netbeans.lib.awtextra.AbsoluteConstraints(-190, 0, 280, 80));

        MainPanel.add(DashPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 80));

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 470, Short.MAX_VALUE)
        );

        MainPanel.add(jDesktopPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 800, 470));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
        java.awt.EventQueue.invokeLater(() -> new MainPage().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BOOKINGS;
    private javax.swing.JPanel DashPanel;
    private javax.swing.JLabel LOGO;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JPanel TRANSPORTATION;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel9;
    // End of variables declaration//GEN-END:variables
}
