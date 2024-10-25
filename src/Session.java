import java.util.ArrayList;
import java.util.List;

//The Session class represents a session in the conference, including its title and speaker.
public class Session {
    // The title of the session
    private String title; 
    // The speaker of the session
    private String speaker; 

    //Constructs a new Session with the specified title and speaker.
    public Session(String title, String speaker) {
        this.title = title;
        this.speaker = speaker;
    }

    //Returns the title of the session.
    public String getTitle() {
        return title;
    }

   //Returns the name of the speaker for the session.
    public String getSpeaker() {
        return speaker;
    }

    //Generates and returns a list of all available sessions.
    public static List<Session> getAllSessions() {
        List<Session> sessions = new ArrayList<>();
        // Add predefined sessions to the list
        sessions.add(new Session("Cooking for dummies", "Jamie Oliver"));
        sessions.add(new Session("Life in the day of Mark Wahlberg", "Mark Wahlberg"));
        sessions.add(new Session("Funny life", "Trevor Noah"));
        return sessions;
    }
}
