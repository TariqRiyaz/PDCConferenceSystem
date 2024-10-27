import java.util.List;

public class FeedbackService {

    // Retrieves sessions for an attendee from the database
    public List<String> getSessionsForAttendee(String email) {
        return DatabaseManager.getSessionsForAttendee(email);
    }

    // Submits feedback for a specific session
    public boolean submitFeedback(String email, String sessionTitle, String feedbackText) {
        try {
            DatabaseManager.submitFeedback(email, sessionTitle, feedbackText);
            return true;
        } catch (Exception e) {
            System.err.println("Error submitting feedback: " + e.getMessage());
            return false;
        }
    }
}
