/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
 */
package frames;

import beans.AccessToken;

import com.google.gson.Gson;
import helper.BarcodeGenerator;
import helper.HttpCall;
import helper.JSONChecker;
import helper.SWTKey;
import helper.SWThreadWorker;
import helper.UIDragger;
import helper.UIEffect;
import helper.preferences.Keys;
import helper.preferences.SettingPreference;
import java.awt.CardLayout;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;

/**
 *
 * @author ASUS
 */
public class LoginFrame extends javax.swing.JFrame implements HttpCall.HttpProcess {

    /**
     * Creates new form Login
     */
    Point initialClick;
    CardLayout cardLayouter;
    boolean internetExist, formCompleted;

    public LoginFrame() {
        initComponents();
//        this.setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 50, 50));
        UIDragger.setFrame(this);
        UIEffect.iconChanger(this);

        labelSpacing.setText("");
        labelLoading.setVisible(false);
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
        textfieldUsername = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        buttonLogin = new javax.swing.JButton();
        labelLinkLoginPhone = new javax.swing.JLabel();
        panelHeader = new javax.swing.JPanel();
        labelClose = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        textfieldPass = new javax.swing.JPasswordField();
        labelSpacing = new javax.swing.JLabel();
        labelLoading = new javax.swing.JLabel();
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
        panelLogin.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 70, 150, -1));

        textfieldUsername.setText("admin");
        textfieldUsername.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textfieldUsernameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textfieldUsernameFocusLost(evt);
            }
        });
        textfieldUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textfieldUsernameActionPerformed(evt);
            }
        });
        textfieldUsername.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textfieldUsernameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textfieldUsernameKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textfieldUsernameKeyTyped(evt);
            }
        });
        panelLogin.add(textfieldUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 90, 250, -1));

        jLabel2.setText("Password:");
        panelLogin.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 130, 150, -1));

        buttonLogin.setText("Login");
        buttonLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLoginActionPerformed(evt);
            }
        });
        panelLogin.add(buttonLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 200, 80, 30));

        labelLinkLoginPhone.setForeground(new java.awt.Color(0, 102, 255));
        labelLinkLoginPhone.setText("<html><u>Logging in By Phone</u></html>");
        labelLinkLoginPhone.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelLinkLoginPhone.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelLinkLoginPhoneMouseClicked(evt);
            }
        });
        panelLogin.add(labelLinkLoginPhone, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 200, 120, 30));

        panelHeader.setBackground(new java.awt.Color(255, 0, 0));

        labelClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close24.png"))); // NOI18N
        labelClose.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelCloseMouseClicked(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Portal Access");

        javax.swing.GroupLayout panelHeaderLayout = new javax.swing.GroupLayout(panelHeader);
        panelHeader.setLayout(panelHeaderLayout);
        panelHeaderLayout.setHorizontalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHeaderLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 117, Short.MAX_VALUE)
                .addComponent(labelClose)
                .addGap(33, 33, 33))
        );
        panelHeaderLayout.setVerticalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelClose, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelLogin.add(panelHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 400, 50));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lock64.png"))); // NOI18N
        panelLogin.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 80, 110));

        textfieldPass.setText("admin");
        textfieldPass.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textfieldPassFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textfieldPassFocusLost(evt);
            }
        });
        textfieldPass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textfieldPassActionPerformed(evt);
            }
        });
        textfieldPass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textfieldPassKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textfieldPassKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textfieldPassKeyTyped(evt);
            }
        });
        panelLogin.add(textfieldPass, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, 250, -1));

        labelSpacing.setText("empty space");
        panelLogin.add(labelSpacing, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 230, 130, 30));

        labelLoading.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelLoading.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/loadingprel.gif"))); // NOI18N
        labelLoading.setText("Loading...");
        panelLogin.add(labelLoading, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 230, 220, 30));

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

        UIDragger.mousePressed(evt);


    }//GEN-LAST:event_panelBaseMousePressed

    private void panelBaseMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelBaseMouseDragged

        UIDragger.mouseDragged(evt);

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

    private void textfieldUsernameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textfieldUsernameFocusGained
        UIEffect.focusGained(textfieldUsername);
    }//GEN-LAST:event_textfieldUsernameFocusGained

    private void textfieldUsernameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textfieldUsernameFocusLost
        UIEffect.focusLost(textfieldUsername);
    }//GEN-LAST:event_textfieldUsernameFocusLost

    private void textfieldPassFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textfieldPassFocusGained
        UIEffect.focusGained(textfieldPass);
    }//GEN-LAST:event_textfieldPassFocusGained

    private void textfieldPassFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textfieldPassFocusLost
        UIEffect.focusLost(textfieldPass);
    }//GEN-LAST:event_textfieldPassFocusLost

    private void buttonLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLoginActionPerformed

        proceedTestLoggingIn();

    }//GEN-LAST:event_buttonLoginActionPerformed

    private void proceedTestLoggingIn() {

        showMessageLoading(true, "loading...");
        buttonLogin.setEnabled(false);

        if (!internetExist) {
            testInternet();
        } else {
            apiLogging();
        }

    }

    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);

    private void testInternet() {
        SWThreadWorker workTestInternet = new SWThreadWorker(this);
        workTestInternet.setWork(SWTKey.WORK_TEST_INTERNET);
        executorService.schedule(workTestInternet, 4, TimeUnit.SECONDS);

        labelLoading.setVisible(true);
    }

    private void apiLogging() {
        SWThreadWorker workLogging = new SWThreadWorker(this);

        workLogging.setWork(SWTKey.WORK_LOGIN);
        workLogging.addData("username", textfieldUsername.getText());
        workLogging.addData("password", textfieldPass.getText());

        executorService.schedule(workLogging, 3, TimeUnit.SECONDS);
        labelLoading.setVisible(true);
    }

    private void checkFormFilled() {

        if (!UIEffect.isEmpty(textfieldUsername) && !UIEffect.isEmpty(textfieldPass)) {
            buttonLogin.setEnabled(true);
            showErrorLoading(false, null);
            formCompleted = true;
        } else {
            buttonLogin.setEnabled(false);
            showErrorLoading(true, "Invalid username & password!");
            formCompleted = false;
        }

    }

    private void showMessageLoading(boolean b, String mess) {
        ImageIcon err = new ImageIcon(getClass().getResource("/images/loadingprel.gif"));
        labelLoading.setVisible(b);
        labelLoading.setIcon(err);
        labelLoading.setText(mess);
    }

    private void showErrorLoading(boolean b, String mess) {
        if (b) {
            // show
            ImageIcon err = new ImageIcon(getClass().getResource("/images/terminate.png"));

            labelLoading.setIcon(err);
            labelLoading.setText(mess);
        }

        labelLoading.setVisible(b);
    }


    private void textfieldPassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textfieldPassActionPerformed
        checkFormFilled();
        
        if (formCompleted) {
            proceedTestLoggingIn();
        }
    }//GEN-LAST:event_textfieldPassActionPerformed

    private void textfieldUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textfieldUsernameActionPerformed
        checkFormFilled();
        
        if (formCompleted) {
            proceedTestLoggingIn();
        }
    }//GEN-LAST:event_textfieldUsernameActionPerformed

    private void textfieldUsernameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldUsernameKeyReleased
        if (!evt.isActionKey() && evt.getKeyCode() != KeyEvent.VK_ENTER) {
            checkFormFilled();
        }
    }//GEN-LAST:event_textfieldUsernameKeyReleased

    private void textfieldPassKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldPassKeyReleased
        if (!evt.isActionKey() && evt.getKeyCode() != KeyEvent.VK_ENTER) {
            checkFormFilled();
        }
    }//GEN-LAST:event_textfieldPassKeyReleased

    private void textfieldUsernameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldUsernameKeyPressed


    }//GEN-LAST:event_textfieldUsernameKeyPressed

    private void textfieldPassKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldPassKeyPressed

    }//GEN-LAST:event_textfieldPassKeyPressed

    private void textfieldUsernameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldUsernameKeyTyped

    }//GEN-LAST:event_textfieldUsernameKeyTyped

    private void textfieldPassKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfieldPassKeyTyped


    }//GEN-LAST:event_textfieldPassKeyTyped

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
                new LoginFrame().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonLogin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel labelBarcode;
    private javax.swing.JLabel labelClose;
    private javax.swing.JLabel labelLinkLoginPhone;
    private javax.swing.JLabel labelLinkNormalLogin;
    private javax.swing.JLabel labelLoading;
    private javax.swing.JLabel labelSpacing;
    private javax.swing.JPanel panelBase;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelLogin;
    private javax.swing.JPanel panelPhoneLogin;
    private javax.swing.JPasswordField textfieldPass;
    private javax.swing.JTextField textfieldUsername;
    // End of variables declaration//GEN-END:variables

    SettingPreference configuration = new SettingPreference();

    @Override
    public void checkResponse(String resp, String callingFromURL) {

        Gson objectG = new Gson();

        System.out.println(callingFromURL + " have " + resp);
        JSONChecker jchecker = new JSONChecker();

        if (jchecker.isValid(resp)) {

            labelLoading.setVisible(false);

            // this is for testing internet availability only
            if (callingFromURL == null) {

                // continue executing
                internetExist = true;
                apiLogging();

            } else {
                // now this is the usual process of logging in

                String innerData = jchecker.getValueAsString("multi_data");
                AccessToken dataIn = objectG.fromJson(innerData, AccessToken.class);

                // update for this token
                configuration.setValue(Keys.TOKEN_API, dataIn.getToken());
                configuration.setValue(Keys.DATE_EXPIRED_TOKEN, dataIn.getExpired_date());

                if (textfieldUsername.getText().equalsIgnoreCase("admin")) {
                    AdminFrame nextFrame = new AdminFrame(this);
                    nextFrame.setVisible(true);
                } else {
                    ClientFrame nextFrame = new ClientFrame(this);
                    nextFrame.setUsername(textfieldUsername.getText());
                    nextFrame.setVisible(true);
                }

                // dont let the button leave alone
                buttonLogin.setEnabled(true);
                this.hide();

            }

        } else {
            // this is when the call is invalid

            if (callingFromURL == null) {
                // set the error icon
                internetExist = false;
                showErrorLoading(true, "please check your internet!");

                
            } else {
                // set the error icon for bad cridentials
                showErrorLoading(true, "invalid username & password!");
            }
            
            // empty value for this token
                configuration.setValue(Keys.TOKEN_API, "");
                configuration.setValue(Keys.DATE_EXPIRED_TOKEN, "");

        }

    }
}
