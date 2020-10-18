/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
 */
package frames;

import helper.BarcodeGenerator;
import java.awt.CardLayout;
import java.awt.Point;
import java.awt.geom.RoundRectangle2D;

/**
 *
 * @author ASUS
 */
public class Login extends javax.swing.JFrame {

    /**
     * Creates new form Login
     */
    Point initialClick;
    CardLayout cardLayouter;
    
    public Login() {
        initComponents();
//        this.setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 50, 50));
        
        cardLayouter = (CardLayout) (panelBase.getLayout());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelBase = new javax.swing.JPanel();
        panelLogin = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        labelLinkLoginPhone = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        labelClose = new javax.swing.JLabel();
        panelPhoneLogin = new javax.swing.JPanel();
        labelBarcode = new javax.swing.JLabel();
        labelLinkNormalLogin = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Login");
        setUndecorated(true);
        setResizable(false);

        panelBase.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        panelBase.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panelBaseMouseDragged(evt);
            }
        });
        panelBase.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panelBaseMousePressed(evt);
            }
        });
        panelBase.setLayout(new java.awt.CardLayout());

        panelLogin.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Username:");
        panelLogin.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 150, -1));
        panelLogin.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 290, -1));

        jLabel2.setText("Password:");
        panelLogin.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 150, -1));
        panelLogin.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 290, -1));

        jButton1.setText("Login");
        panelLogin.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 200, 80, 40));

        labelLinkLoginPhone.setForeground(new java.awt.Color(0, 102, 255));
        labelLinkLoginPhone.setText("<html><u>Logging By Phone</u></html>");
        labelLinkLoginPhone.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelLinkLoginPhone.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelLinkLoginPhoneMouseClicked(evt);
            }
        });
        panelLogin.add(labelLinkLoginPhone, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 210, 100, 20));

        jPanel1.setBackground(new java.awt.Color(255, 0, 0));

        labelClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close.png"))); // NOI18N
        labelClose.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelCloseMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(355, Short.MAX_VALUE)
                .addComponent(labelClose)
                .addGap(21, 21, 21))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelClose, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelLogin.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 400, 50));

        panelBase.add(panelLogin, "panelLogin");

        panelPhoneLogin.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelBarcode.setText("Preview");
        panelPhoneLogin.add(labelBarcode, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, 300, 160));

        labelLinkNormalLogin.setForeground(new java.awt.Color(0, 102, 255));
        labelLinkNormalLogin.setText("<html><u>Back to Normal Login</u></html>");
        labelLinkNormalLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelLinkNormalLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelLinkNormalLoginMouseClicked(evt);
            }
        });
        panelPhoneLogin.add(labelLinkNormalLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 210, 100, 20));

        panelBase.add(panelPhoneLogin, "panelPhoneLogin");

        getContentPane().add(panelBase, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void panelBaseMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelBaseMousePressed
        
        initialClick = evt.getPoint();
        getComponentAt(initialClick);

    }//GEN-LAST:event_panelBaseMousePressed

    private void panelBaseMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelBaseMouseDragged
        
        int thisX = this.getLocation().x;
        int thisY = this.getLocation().y;

        // Determine how much the mouse moved since the initial click
        int xMoved = evt.getX() - initialClick.x;
        int yMoved = evt.getY() - initialClick.y;

        // Move window to this position
        int X = thisX + xMoved;
        int Y = thisY + yMoved;
        this.setLocation(X, Y);

    }//GEN-LAST:event_panelBaseMouseDragged

    private void labelLinkLoginPhoneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelLinkLoginPhoneMouseClicked
        BarcodeGenerator qr = new BarcodeGenerator();
        qr.create("sample", labelBarcode);
        
        cardLayouter.show(panelBase, "panelPhoneLogin");
    }//GEN-LAST:event_labelLinkLoginPhoneMouseClicked

    private void labelLinkNormalLoginMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelLinkNormalLoginMouseClicked
        cardLayouter.show(panelBase, "panelLogin");
    }//GEN-LAST:event_labelLinkNormalLoginMouseClicked

    private void labelCloseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelCloseMouseClicked
        System.exit(0);
    }//GEN-LAST:event_labelCloseMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("windows".equalsIgnoreCase(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JLabel labelBarcode;
    private javax.swing.JLabel labelClose;
    private javax.swing.JLabel labelLinkLoginPhone;
    private javax.swing.JLabel labelLinkNormalLogin;
    private javax.swing.JPanel panelBase;
    private javax.swing.JPanel panelLogin;
    private javax.swing.JPanel panelPhoneLogin;
    // End of variables declaration//GEN-END:variables
}
