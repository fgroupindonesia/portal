/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020-2021.
 */
package beans;

/**
 *
 * @author ASUS
 */
public class CertificateStudent {
    
    private int id;
    private int exam_category_id;
    private int status; 
    private String student_username;
    private String exam_category_title;
    private String filename;
    private String exam_date_created;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the exam_category_id
     */
    public int getExam_category_id() {
        return exam_category_id;
    }

    /**
     * @param exam_category_id the exam_category_id to set
     */
    public void setExam_category_id(int exam_category_id) {
        this.exam_category_id = exam_category_id;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the student_username
     */
    public String getStudent_username() {
        return student_username;
    }

    /**
     * @param student_username the student_username to set
     */
    public void setStudent_username(String student_username) {
        this.student_username = student_username;
    }

    /**
     * @return the exam_category_title
     */
    public String getExam_category_title() {
        return exam_category_title;
    }

    /**
     * @param exam_category_title the exam_category_title to set
     */
    public void setExam_category_title(String exam_category_title) {
        this.exam_category_title = exam_category_title;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return the exam_date_created
     */
    public String getExam_date_created() {
        return exam_date_created;
    }

    /**
     * @param exam_date_created the exam_date_created to set
     */
    public void setExam_date_created(String exam_date_created) {
        this.exam_date_created = exam_date_created;
    }
}
