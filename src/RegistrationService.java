import java.util.List;

public class RegistrationService {

    // Method to handle attendee registration
    public boolean registerAttendee(String name, String email, List<String> sessionTitles) {
        if (name == null || name.isEmpty() || email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Name and email must not be empty.");
        }

        if (!email.matches("^\\S+@\\S+\\.\\S+$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        try {
            // Use DatabaseManager to register the attendee
            DatabaseManager.addAttendee(name, email, sessionTitles);
            return true; // Success
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Failure
        }
    }

    // Optional additional service methods for attendee management
    public List<String> getSessionsForAttendee(String email) {
        return DatabaseManager.getSessionsForAttendee(email);
    }
}
