import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

//Feedback class was designed to handle user feedback functionality for sessions.
public class Feedback {
    private String feedbackText;

    //Constructs a new Feedback instance with the provided feedback text.
    public Feedback(String feedbackText) {
        this.feedbackText = feedbackText;
    }
}
