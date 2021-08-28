/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020-2021.
 */
package beans;

/**
 *
 * @author ASUS
 */
public class ExamQuestion {
    
    private int id;
    private int jenis;
    private int score_point;
    private int exam_category_id;
    private int exam_sub_category_id;
    private String question;
    private String option_a;
    private String option_b;
    private String option_c;
    private String option_d;
    private String preview;
    private String answer;

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
     * @return the jenis
     */
    public int getJenis() {
        return jenis;
    }

    /**
     * @param jenis the jenis to set
     */
    public void setJenis(int jenis) {
        this.jenis = jenis;
    }

    /**
     * @return the score_point
     */
    public int getScore_point() {
        return score_point;
    }

    /**
     * @param score_point the score_point to set
     */
    public void setScore_point(int score_point) {
        this.score_point = score_point;
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
     * @return the exam_sub_category_id
     */
    public int getExam_sub_category_id() {
        return exam_sub_category_id;
    }

    /**
     * @param exam_sub_category_id the exam_sub_category_id to set
     */
    public void setExam_sub_category_id(int exam_sub_category_id) {
        this.exam_sub_category_id = exam_sub_category_id;
    }

    /**
     * @return the question
     */
    public String getQuestion() {
        return question;
    }

    /**
     * @param question the question to set
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * @return the option_a
     */
    public String getOption_a() {
        return option_a;
    }

    /**
     * @param option_a the option_a to set
     */
    public void setOption_a(String option_a) {
        this.option_a = option_a;
    }

    /**
     * @return the option_b
     */
    public String getOption_b() {
        return option_b;
    }

    /**
     * @param option_b the option_b to set
     */
    public void setOption_b(String option_b) {
        this.option_b = option_b;
    }

    /**
     * @return the option_c
     */
    public String getOption_c() {
        return option_c;
    }

    /**
     * @param option_c the option_c to set
     */
    public void setOption_c(String option_c) {
        this.option_c = option_c;
    }

    /**
     * @return the option_d
     */
    public String getOption_d() {
        return option_d;
    }

    /**
     * @param option_d the option_d to set
     */
    public void setOption_d(String option_d) {
        this.option_d = option_d;
    }

    /**
     * @return the preview
     */
    public String getPreview() {
        return preview;
    }

    /**
     * @param preview the preview to set
     */
    public void setPreview(String preview) {
        this.preview = preview;
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
    
}
