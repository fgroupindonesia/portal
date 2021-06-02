/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */
package beans;

/**
 *
 * @author ASUS
 */
public class SubExamCategory {
    
    private int id;
    private int exam_category_id;
    private String title;

    public SubExamCategory(String title){
        this.title = title;
    }
    
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
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    
}
