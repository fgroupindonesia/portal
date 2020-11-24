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
                d.getPass(),
                d.getEmail(),
                d.getAddress(),
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
                d.getTitle(),
                d.getDescription(),
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
            Object[] dataBaru = new Object[]{false, d.getDate_created(), rp.getText(d.getAmount()), d.getMethod()};
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
                d.getClass_registered()
            };
            
            tableModel.addRow(dataBaru);
        }

    }

    public void render(JTable el, Attendance[] dataCome) {

        DefaultTableModel tableModel = (DefaultTableModel) el.getModel();
        tableModel.setRowCount(0);
        for (Attendance d : dataCome) {
            Object[] dataBaru = new Object[]{false, d.getClass_registered(), d.getStatus(), d.getDate_created()};
            tableModel.addRow(dataBaru);
        }

    }
}
