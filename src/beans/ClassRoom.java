/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */
package beans;

/**
 *
 * @author ASUS
 */
public class ClassRoom {
    private int id;
    private int instructor_id;            
    private String instructor_name;            
    private String name;
    private String description;
    private String date_created;
    private int for_exam;

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
     * @return the instructor_id
     */
    public int getInstructor_id() {
        return instructor_id;
    }

    /**
     * @param instructor_id the instructor_id to set
     */
    public void setInstructor_id(int instructor_id) {
        this.instructor_id = instructor_id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
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
     * @return the instructor_name
     */
    public String getInstructor_name() {
        return instructor_name;
    }

    /**
     * @param instructor_name the instructor_name to set
     */
    public void setInstructor_name(String instructor_name) {
        this.instructor_name = instructor_name;
    }

    /**
     * @return the for_exam
     */
    public int getFor_exam() {
        return for_exam;
    }

    /**
     * @param for_exam the for_exam to set
     */
    public void setFor_exam(int for_exam) {
        this.for_exam = for_exam;
    }
}
