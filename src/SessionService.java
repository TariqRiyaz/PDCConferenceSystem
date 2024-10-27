import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Handles data operations related to the Session class.
public class SessionService {

    // Retrieves all available sessions from the database.
    public List<Session> getAllSessions() {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT title, speaker FROM Session";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String title = rs.getString("title");
                String speaker = rs.getString("speaker");
                sessions.add(new Session(title, speaker));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving sessions: " + e.getMessage());
        }

        return sessions;
    }
}
