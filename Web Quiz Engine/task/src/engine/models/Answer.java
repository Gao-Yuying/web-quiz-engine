package engine.models;

public class Answer {
    private Integer[] answer;

    public void setAnswer(Integer[] answer) {
        this.answer = answer.clone();
    }

    public Integer[] getAnswer() {
        return answer;
    }
}
