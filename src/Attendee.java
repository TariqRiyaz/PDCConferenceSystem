import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

// Represents an attendee who registers for conference sessions.
public class Attendee extends Person {
    private static final String TYPE = "Attendee"; // Avoid magic strings.
    
    private String email;
    private List<String> sessionNames;

    // Constructs a new Attendee with the specified name and email.
    public Attendee(String name, String email) {
        super(name); // Initializes name via Person constructor
        this.email = email; // Store email for contact
        this.sessionNames = new ArrayList<>();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public List<String> getSessionNames() {
        // Provide an unmodifiable list to prevent external modification
        return Collections.unmodifiableList(sessionNames);
    }

    // Registers a session for the attendee
    public void addSession(String sessionName) {
        sessionNames.add(sessionName);
    }

    @Override
    public String toTextFormat() {
        String sessions = String.join(";", sessionNames);
        return String.join(",", TYPE, getName(), email, sessions);
    }
}
