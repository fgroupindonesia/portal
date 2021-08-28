/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */
package helper;

import beans.Attendance;
import beans.Document;
import beans.ExamCategory;
import beans.ExamQuestion;
import beans.Payment;
import beans.RBugs;
import beans.Schedule;
import beans.ExamSubCategory;
import beans.User;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ASUS
 */
public class TableRenderer {

    public boolean isTableEmpty(JTable data) {

        boolean y = true;

        if (data.getRowCount() > 0) {
            y = false;
        }

        return y;

    }

    public String getSelectedRowValue(JTable el, int colFind) {

        String val = null;

        DefaultTableModel model = (DefaultTableModel) el.getModel();
        int activeRow = el.getSelectedRow();

        Object dat = model.getValueAt(activeRow, colFind);

        if (dat != null) {
            val = dat.toString();
        }

        return val;

    }

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
                Object dat = model.getValueAt(row, column);

                if (dat != null) {
                    val = dat.toString();
                }
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
            if (val.equalsIgnoreCase(valTarget)) {
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
                Object dat = null;
                
                    // the targeted column is exist here
                    dat = model.getValueAt(row, column);
               
                if (dat != null) {
                    String val = dat.toString();
                    data.add(val);
                }
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
    
    public void render(JTable el, ExamQuestion[] dataCome) {

        DefaultTableModel tableModel = (DefaultTableModel) el.getModel();

        tableModel.setRowCount(0);

        for (ExamQuestion d : dataCome) {
            Object[] dataBaru = new Object[]{
                false,
                d.getId(),
                UIEffect.decodeSafe(d.getQuestion()),
                d.getJenis(),
                UIEffect.decodeSafe(d.getAnswer()),
                d.getScore_point(),
                d.getPreview()
            };
            tableModel.addRow(dataBaru);
        }

    }

    public void render(JTable el, ExamSubCategory[] dataCome) {

        DefaultTableModel tableModel = (DefaultTableModel) el.getModel();

        tableModel.setRowCount(0);

        for (ExamSubCategory d : dataCome) {
            Object[] dataBaru = new Object[]{
                false,
                d.getId(),
                d.getTitle()
            };
            tableModel.addRow(dataBaru);
        }

    }

    public void render(JTable el, ExamCategory[] dataCome) {

        DefaultTableModel tableModel = (DefaultTableModel) el.getModel();

        tableModel.setRowCount(0);

        for (ExamCategory d : dataCome) {
            Object[] dataBaru = new Object[]{
                false,
                d.getId(),
                d.getTitle(),
                d.getCode()
            };
            tableModel.addRow(dataBaru);
        }

    }

    public void render(JTable el, RBugs[] dataCome) {

        DefaultTableModel tableModel = (DefaultTableModel) el.getModel();

        tableModel.setRowCount(0);

        for (RBugs d : dataCome) {
            Object[] dataBaru = new Object[]{
                false,
                d.getId(),
                d.getApp_name(),
                d.getUsername(),
                d.getIp_address(),
                d.getTitle(),
                d.getDescription(),
                d.getScreenshot(),
                d.getDate_created()
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

    public static DefaultTableModel clearData(JTable jtab) {
        DefaultTableModel model = (DefaultTableModel) jtab.getModel();
        model.setRowCount(0);

        return model;
    }

    public static void render(JTable el, ArrayList<ExamSubCategory> items) {

        DefaultTableModel tableModel = clearData(el);
        for (ExamSubCategory sb : items) {

            Object[] dataBaru = new Object[]{
                false,
                sb.getId(),
                sb.getTitle()
            };
            tableModel.addRow(dataBaru);

        }

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
