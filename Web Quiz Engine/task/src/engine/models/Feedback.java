package engine.models;

public class Feedback {
    private boolean success;
    private final String feedback;

    public Feedback(boolean success) {
        this.success = success;
        feedback = this.success ?
                "Congratulations, you're right!" :
                "Wrong answer! Please, try again.";
    }

    public boolean getSuccess() {
        return success;
    }

    public String getFeedback() {
        return feedback;
    }
}
