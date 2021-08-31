/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020-2021.
 */
package beans;

/**
 *
 * @author ASUS
 */
public class ExamMultipleChoice {

    private String ops;
    private String title;
    private boolean answer;

    public ExamMultipleChoice(String titleIn, String opsIn, boolean answerIn) {
        title = titleIn;
        ops = opsIn;
        answer = answerIn;
    }

    /**
     * @return the ops
     */
    public String getOps() {
        return ops;
    }

    /**
     * @param ops the ops to set
     */
    public void setOps(String ops) {
        this.ops = ops;
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

    /**
     * @return the answer
     */
    public boolean isAnswer() {
        return answer;
    }

    /**
     * @param answer the answer to set
     */
    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

}
