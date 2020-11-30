/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
 */
package helper;

import beans.Attendance;
import beans.Document;
import beans.Payment;
import beans.Schedule;
import beans.User;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ASUS
 */
public class TableRenderer {

    public String getCheckedRowValue(JTable el, int column) {

        String val = null;
        int row = 0;

        DefaultTableModel model = (DefaultTableModel) el.getModel();

        int totalRow = model.getRowCount();
        for (int x = 0; x < totalRow; x++) {
            row = x;
            // checked is always at the 0th index
            boolean b = (Boolean) model.getValueAt(row, 0);

            if (b) {
                // the targeted column is exist here
                val = model.getValueAt(row, column).toString();

            }

        }

        return val;
    }

    public int getRowCountValue(JTable el, int column, String valTarget) {

        int counted = 0;
        int row = 0;

        DefaultTableModel model = (DefaultTableModel) el.getModel();

        int totalRow = model.getRowCount();
        for (int x = 0; x < totalRow; x++) {
            row = x;

            // the targeted column is exist here
            String val = model.getValueAt(row, column).toString();
            if(val.equalsIgnoreCase(valTarget)){
                counted++;
            }

        }

        return counted;
    }

    public ArrayList getCheckedRows(JTable el, int column) {

        ArrayList data = new ArrayList();
        int row = 0;

        DefaultTableModel model = (DefaultTableModel) el.getModel();

        int totalRow = model.getRowCount();
        for (int x = 0; x < totalRow; x++) {
            row = x;
            // checked is always at the 0th index
            boolean b = (Boolean) model.getValueAt(row, 0);

            if (b) {
                // the targeted column is exist here
                String val = model.getValueAt(row, column).toString();
                data.add(val);
            }

        }

        return data;
    }

    public void render(JTable el, User[] dataCome) {

        DefaultTableModel tableModel = (DefaultTableModel) el.getModel();

        tableModel.setRowCount(0);

        for (User d : dataCome) {
            Object[] dataBaru = new Object[]{
                false,
                d.getId(),
                d.getUsername(),
                UIEffect.decodeSafe(d.getPass()),
                UIEffect.decodeSafe(d.getEmail()),
                UIEffect.decodeSafe(d.getAddress()),
                d.getPropic(),
                d.getMobile()
            };
            tableModel.addRow(dataBaru);
        }

    }

    public void render(JTable el, Document[] dataCome) {

        // the table format is same between client & admin
        DefaultTableModel tableModel = (DefaultTableModel) el.getModel();
        tableModel.setRowCount(0);
        for (Document d : dataCome) {
            Object[] dataBaru = new Object[]{false,
                d.getId(),
                UIEffect.decodeSafe(d.getTitle()),
                UIEffect.decodeSafe(d.getDescription()),
                d.getFilename(),
                d.getUsername(),
                d.getUrl(),
                d.getDate_created()};
            tableModel.addRow(dataBaru);
        }

    }

    public void render(JTable el, Payment[] dataCome) {

        DefaultTableModel tableModel = (DefaultTableModel) el.getModel();
        tableModel.setRowCount(0);
        RupiahGenerator rp = new RupiahGenerator();

        for (Payment d : dataCome) {
            Object[] dataBaru = new Object[]{false,
                d.getId(),
                d.getUsername(),
                rp.getText(d.getAmount()),
                UIEffect.decodeSafe(d.getMethod()),
                d.getScreenshot(),
                d.getDate_created(),};
            tableModel.addRow(dataBaru);
        }

    }

    public void render(JTable el, Schedule[] dataCome) {

        DefaultTableModel tableModel = (DefaultTableModel) el.getModel();
        tableModel.setRowCount(0);

        for (Schedule d : dataCome) {
            Object[] dataBaru = new Object[]{false,
                d.getId(),
                d.getUsername(),
                d.getDay_schedule(),
                d.getTime_schedule(),
                UIEffect.decodeSafe(d.getClass_registered())
            };

            tableModel.addRow(dataBaru);
        }

    }

    // this is for UTF-8 decoder
    private static String decode(String val) {
        return UIEffect.decodeSafe(val);
    }

    public void render(JTable el, Attendance[] dataCome) {

        DefaultTableModel tableModel = (DefaultTableModel) el.getModel();
        tableModel.setRowCount(0);
        for (Attendance d : dataCome) {
            Object[] dataBaru = new Object[]{
                false,
                d.getId(),
                d.getUsername(),
                decode(d.getClass_registered()),
                d.getStatus(),
                d.getSignature(),
                d.getDate_created(),
                d.getDate_modified()
            };
            tableModel.addRow(dataBaru);
        }

    }
}
