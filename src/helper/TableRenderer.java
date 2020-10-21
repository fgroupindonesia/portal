/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
 */
package helper;

import beans.Attendance;
import beans.Document;
import beans.Payment;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ASUS
 */
public class TableRenderer {

 
    public  void render(JTable el, Document[] dataCome) {

        DefaultTableModel tableModel = (DefaultTableModel) el.getModel();

        for (Document d : dataCome) {
            Object[] dataBaru = new Object[]{false, d.getTitle(), d.getDescription(), d.getUrl()};
            tableModel.addRow(dataBaru);
        }

    }
    
    public  void render(JTable el, Payment[] dataCome) {

        DefaultTableModel tableModel = (DefaultTableModel) el.getModel();

        RupiahGenerator rp = new RupiahGenerator();
        
        for (Payment d : dataCome) {
            Object[] dataBaru = new Object[]{false, d.getDate_created(), rp.getText(d.getAmount()), d.getMethod()};
            tableModel.addRow(dataBaru);
        }

    }
    
    public  void render(JTable el, Attendance[] dataCome) {

        DefaultTableModel tableModel = (DefaultTableModel) el.getModel();

        for (Attendance d : dataCome) {
            Object[] dataBaru = new Object[]{false, d.getClass_registered(), d.getStatus(), d.getDate_created()};
            tableModel.addRow(dataBaru);
        }

    }
}
