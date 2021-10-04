/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020-2021.
 */
package beans;

/**
 *
 * @author ASUS
 */
public class ExamStudentAnswer {

    private int id;
    private int exam_qa_id;
    private int score_earned;
    private String student_username;
    private String answer;
    private String status;
    private String date_created;
    private String fileupload;

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
     * @return the exam_qa_id
     */
    public int getExam_qa_id() {
        return exam_qa_id;
    }

    /**
     * @param exam_qa_id the exam_qa_id to set
     */
    public void setExam_qa_id(int exam_qa_id) {
        this.exam_qa_id = exam_qa_id;
    }

    /**
     * @return the score_earned
     */
    public int getScore_earned() {
        return score_earned;
    }

    /**
     * @param score_earned the score_earned to set
     */
    public void setScore_earned(int score_earned) {
        this.score_earned = score_earned;
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
     * @return the answer
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * @param answer the answer to set
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the date_created
     */
    public String getDate_created() {
        return date_created;
    }

    /**
     * @param date_created the date_created to set
     */
    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    /**
     * @return the fileupload
     */
    public String getFileupload() {
        return fileupload;
    }

    /**
     * @param fileupload the fileupload to set
     */
    public void setFileupload(String fileupload) {
        this.fileupload = fileupload;
    }
    
}
