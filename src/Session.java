import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// The Session class represents a session in the conference, including its title and speaker.
public class Session {
    // The title of the session
    private String title; 
    // The speaker of the session
    private String speaker; 

    // Constructs a new Session with the specified title and speaker.
    public Session(String title, String speaker) {
        this.title = title;
        this.speaker = speaker;
    }

    // Returns the title of the session.
    public String getTitle() {
        return title;
    }

    // Returns the name of the speaker for the session.
    public String getSpeaker() {
        return speaker;
    }

    // Generates and returns a list of all available sessions from the database.
    public static List<Session> getAllSessions() {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT title, speaker FROM Session";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            // Loop through the result set and add each session to the list
            while (rs.next()) {
                String title = rs.getString("title");
                String speaker = rs.getString("speaker");
                sessions.add(new Session(title, speaker));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sessions;
    }
}
