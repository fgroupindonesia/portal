/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */
package helper;

import beans.Attendance;
import beans.CertificateStudent;
import beans.ClassRoom;
import beans.Document;
import beans.ExamCategory;
import beans.ExamMultipleChoice;
import beans.ExamQuestion;
import beans.ExamStudentAnswer;
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

    public String getOptionCodeFromTable(JTable tab) {

        String ops = null;

        switch (tab.getRowCount()) {
            case 0:
                ops = "A";
                break;
            case 1:
                ops = "B";
                break;
            case 2:
                ops = "C";
                break;
            case 3:
                ops = "D";
                break;
        }

        return ops;

    }

    public boolean isTableEmpty(JTable data) {

        boolean y = true;

        if (data.getRowCount() > 0) {
            y = false;
        }

        return y;

    }

    public String[] collectExamQuestionOpsUsed(JTable dataTableIn, int columnPost) {

        DefaultTableModel model = (DefaultTableModel) dataTableIn.getModel();

        String collectedData[] = new String[dataTableIn.getRowCount()];

        for (int x = 0; x < dataTableIn.getRowCount(); x++) {
            Object dat = model.getValueAt(x, columnPost);
            collectedData[x] = dat.toString();

        }

        return collectedData;

    }

    public String getValueWithID(JTable el, int anID, int columnID, int columnWanted) {

        DefaultTableModel model = (DefaultTableModel) el.getModel();

        int totalRow = model.getRowCount();
        String yourValue = null;
        for (int x = 0; x < totalRow; x++) {
            // the targeted column is exist here
            Object dat = model.getValueAt(x, columnID);

            if (dat != null) {
                if (dat.toString().equalsIgnoreCase("" + anID)) {
                    yourValue = model.getValueAt(x, columnWanted).toString();
                    break;
                }
            }

        }

        return yourValue;

    }

    public String getValueWithText(JTable el, String aValue, int columnID, int columnWanted) {

        DefaultTableModel model = (DefaultTableModel) el.getModel();

        int totalRow = model.getRowCount();
        String yourValue = null;
        for (int x = 0; x < totalRow; x++) {
            // the targeted column is exist here
            Object dat = model.getValueAt(x, columnID);

            if (dat != null) {
                if (dat.toString().equalsIgnoreCase(aValue)) {
                    yourValue = model.getValueAt(x, columnWanted).toString();
                    break;
                }
            }

        }

        return yourValue;

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

    public void render(JTable el, CertificateStudent[] dataCome) {

        DefaultTableModel tableModel = (DefaultTableModel) el.getModel();

        tableModel.setRowCount(0);

        for (CertificateStudent d : dataCome) {

            String stat = null;

            if (d.getStatus() == 1) {
                stat = "released";
            } else {
                stat = "waiting";
            }

            Object[] dataBaru = new Object[]{
                false,
                d.getId(),
                d.getStudent_username(),
                d.getExam_category_title(),
                stat,
                d.getFilename(),
                d.getExam_date_created()
            };
            tableModel.addRow(dataBaru);
        }

    }

    public void render(JTable el, ExamStudentAnswer[] dataCome) {

        DefaultTableModel tableModel = (DefaultTableModel) el.getModel();

        tableModel.setRowCount(0);

        for (ExamStudentAnswer d : dataCome) {
            Object[] dataBaru = new Object[]{
                false,
                d.getId(),
                d.getStudent_username(),
                UIEffect.decodeSafe(d.getAnswer()),
                d.getScore_earned(),
                d.getStatus(),
                d.getDate_created()
            };

            tableModel.addRow(dataBaru);
        }

    }

    public void renderExamMultipleChoices(JTable el, ArrayList<ExamMultipleChoice> dataCome) {

        DefaultTableModel tableModel = (DefaultTableModel) el.getModel();

        tableModel.setRowCount(0);

        for (ExamMultipleChoice d : dataCome) {
            Object[] dataBaru = new Object[]{
                false,
                d.getOps(),
                d.isAnswer(),
                d.getTitle()
            };
            tableModel.addRow(dataBaru);
        }

    }

    public void render(JTable el, ExamQuestion[] dataCome) {

        DefaultTableModel tableModel = (DefaultTableModel) el.getModel();

        tableModel.setRowCount(0);

        String jenisQ = null;

        for (ExamQuestion d : dataCome) {

            switch (d.getJenis()) {
                case 1:
                    jenisQ = "ABCD";
                    break;
                case 2:
                    jenisQ = "Essay";
                    break;
                case 3:
                    jenisQ = "AB";
                    break;

            }

            Object[] dataBaru = new Object[]{
                false,
                d.getId(),
                UIEffect.decodeSafe(d.getQuestion()),
                jenisQ,
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

    public void render(JTable el, ClassRoom[] dataCome) {

        DefaultTableModel tableModel = (DefaultTableModel) el.getModel();

        tableModel.setRowCount(0);

        for (ClassRoom d : dataCome) {
            // 1 for yes
            // 0 for no
            boolean ex = (d.getFor_exam() == 1) ? true : false;
            
            Object[] dataBaru = new Object[]{
                false,
                d.getId(),
                d.getName(),
                d.getDescription(),
                d.getInstructor_id(),
                d.getInstructor_name(),
                d.getDate_created(),
                ex
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

    public static void renderExamSubCategory(JTable el, ArrayList<ExamSubCategory> items) {

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
