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

    //Saves the feedback text to the specified feedback file.
    public void saveToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(feedbackText);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
