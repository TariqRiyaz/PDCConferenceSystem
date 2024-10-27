import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    // JDBC URL for embedded Derby database
    private static final String DB_URL = "jdbc:derby:conferenceDB;create=true";

    // Method to establish a connection to the database
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Method to add a new attendee and link them to multiple sessions
    public static void addAttendee(String name, String email, List<String> sessionTitles) {
        try (Connection conn = connect()) {
            conn.setAutoCommit(false); // Begin transaction to ensure atomicity
            int attendeeId = -1;

            // Check if the attendee already exists based on email
            String checkAttendeeSQL = "SELECT id FROM Attendee WHERE email = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkAttendeeSQL)) {
                checkStmt.setString(1, email);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        attendeeId = rs.getInt("id"); // Get existing attendee ID
                    }
                }
            }

            // If the attendee doesn't exist, insert them into the database
            if (attendeeId == -1) {
                String insertAttendeeSQL = "INSERT INTO Attendee (name, email) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertAttendeeSQL, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, name);
                    pstmt.setString(2, email);
                    pstmt.executeUpdate();

                    // Retrieve the generated attendee ID
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            attendeeId = rs.getInt(1);
                        }
                    }
                }
            }

            // Link each session title to the attendee in the Attendee_Session table
            String insertAttendeeSessionSQL = "INSERT INTO Attendee_Session (attendee_id, session_id) VALUES (?, ?)";
            try (PreparedStatement sessionStmt = conn.prepareStatement(insertAttendeeSessionSQL)) {
                for (String title : sessionTitles) {
                    int sessionId = findSessionIdByTitle(conn, title);
                    // Only link if the session exists and is not already linked
                    if (sessionId != -1 && !isAttendeeSessionLinked(conn, attendeeId, sessionId)) {
                        sessionStmt.setInt(1, attendeeId);
                        sessionStmt.setInt(2, sessionId);
                        sessionStmt.executeUpdate();
                    }
                }
            }

            conn.commit(); // Commit transaction

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to check if a session link already exists for an attendee
    private static boolean isAttendeeSessionLinked(Connection conn, int attendeeId, int sessionId) throws SQLException {
        String checkLinkSQL = "SELECT COUNT(*) FROM Attendee_Session WHERE attendee_id = ? AND session_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(checkLinkSQL)) {
            pstmt.setInt(1, attendeeId);
            pstmt.setInt(2, sessionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0; // Returns true if link exists
            }
        }
    }

    // Helper method to find a session ID by its title (case-insensitive)
    private static int findSessionIdByTitle(Connection conn, String title) throws SQLException {
        String query = "SELECT ID FROM APP.\"SESSION\" WHERE UPPER(TITLE) = UPPER(?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, title);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID");
                }
            }
        }
        return -1; // Return -1 if session is not found
    }

    // Initialize database tables (runs only once to create the tables if they do not exist)
    public static void initializeDatabase() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            // Create Attendee table with unique email constraint
            try {
                String createAttendeeTable = "CREATE TABLE Attendee (" +
                        "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                        "name VARCHAR(100), " +
                        "email VARCHAR(100) UNIQUE)";
                stmt.executeUpdate(createAttendeeTable);
            } catch (SQLException e) {
                if (!e.getSQLState().equals("X0Y32")) { // Ignore table exists error
                    e.printStackTrace();
                }
            }

            // Create Session table with title and speaker columns
            try {
                String createSessionTable = "CREATE TABLE Session (" +
                        "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                        "title VARCHAR(200), " +
                        "speaker VARCHAR(100))";
                stmt.executeUpdate(createSessionTable);
            } catch (SQLException e) {
                if (!e.getSQLState().equals("X0Y32")) { // Ignore table exists error
                    e.printStackTrace();
                }
            }

            // Create Attendee_Session table to link attendees to sessions with feedback
            try {
                String createAttendeeSessionTable = "CREATE TABLE Attendee_Session (" +
                        "attendee_id INT, " +
                        "session_id INT, " +
                        "feedback VARCHAR(500), " +
                        "FOREIGN KEY (attendee_id) REFERENCES Attendee(id), " +
                        "FOREIGN KEY (session_id) REFERENCES Session(id))";
                stmt.executeUpdate(createAttendeeSessionTable);
            } catch (SQLException e) {
                if (!e.getSQLState().equals("X0Y32")) { // Ignore table exists error
                    e.printStackTrace();
                }
            }

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Display all attendees and their associated sessions
    public static void displayAttendeeSessions() {
        String query = "SELECT a.name, s.title FROM Attendee a " +
                "JOIN Attendee_Session ON a.id = Attendee_Session.attendee_id " +
                "JOIN Session s ON Attendee_Session.session_id = s.id";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            System.out.println("Attendees and their Sessions:");
            while (rs.next()) {
                String attendeeName = rs.getString("name");
                String sessionTitle = rs.getString("title");
                System.out.println("Attendee: " + attendeeName + ", Session: " + sessionTitle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Populate the database with default sessions if no sessions exist
    public static void populateDefaultSessions() {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Session (title, speaker) VALUES (?, ?)")) {

            // Check if the Session table already has data
            String checkQuery = "SELECT COUNT(*) AS count FROM Session";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(checkQuery)) {

                if (rs.next() && rs.getInt("count") > 0) {
                    System.out.println("Sessions already exist, skipping population.");
                    return;
                }
            }

            // Insert default session records
            String[][] sessions = {
                    { "Cooking for Dummies", "Jamie Oliver" },
                    { "Life in the Day of Mark Wahlberg", "Mark Wahlberg" },
                    { "Funny Life", "Trevor Noah" }
            };

            for (String[] session : sessions) {
                pstmt.setString(1, session[0]);
                pstmt.setString(2, session[1]);
                pstmt.executeUpdate();
            }

            System.out.println("Default sessions added successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Check if any sessions exist in the database
    public static boolean isSessionTablePopulated() {
        String query = "SELECT COUNT(*) AS count FROM Session";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Display all sessions in the Session table
    public static void displayAllSessions() {
        String query = "SELECT id, title, speaker FROM Session";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            System.out.println("Sessions in the Database:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String speaker = rs.getString("speaker");
                System.out.println("ID: " + id + ", Title: " + title + ", Speaker: " + speaker);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Submit feedback for a specific session by attendee's email
    public static void submitFeedback(String email, String sessionTitle, String feedback) {
        try (Connection conn = connect()) {
            int attendeeId = getAttendeeIdByEmail(conn, email);
            int sessionId = findSessionIdByTitle(conn, sessionTitle);

            if (attendeeId != -1 && sessionId != -1) {
                String updateFeedbackSQL = "UPDATE Attendee_Session SET feedback = ? WHERE attendee_id = ? AND session_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateFeedbackSQL)) {
                    pstmt.setString(1, feedback);
                    pstmt.setInt(2, attendeeId);
                    pstmt.setInt(3, sessionId);
                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Feedback submitted successfully.");
                    } else {
                        System.out.println("No matching attendee-session record found.");
                    }
                }
            } else {
                System.out.println("Attendee or session not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get a list of session titles for a given attendee based on their email
    public static List<String> getSessionsForAttendee(String email) {
        List<String> sessions = new ArrayList<>();

        String query = "SELECT s.title FROM Attendee a " +
                "JOIN Attendee_Session att_sess ON a.id = att_sess.attendee_id " +
                "JOIN Session s ON att_sess.session_id = s.id " +
                "WHERE a.email = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sessions.add(rs.getString("title"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sessions;
    }

    // Method to add a new session to the Session table
    public static int addSession(String title, String speaker) {
        String insertSessionSQL = "INSERT INTO Session (title, speaker) VALUES (?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(insertSessionSQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, title);
            pstmt.setString(2, speaker);
            pstmt.executeUpdate();

            // Retrieve the generated session ID
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if the session could not be added
    }

    // Get feedback for a specific session by attendee's email and session title
    public String getFeedbackForSessionAndAttendee(String email, String sessionTitle) {
        String feedback = null;

        try (Connection conn = connect()) {
            int attendeeId = getAttendeeIdByEmail(conn, email);
            int sessionId = findSessionIdByTitle(conn, sessionTitle);

            if (attendeeId != -1 && sessionId != -1) {
                String query = "SELECT feedback FROM Attendee_Session WHERE attendee_id = ? AND session_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, attendeeId);
                    stmt.setInt(2, sessionId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        feedback = rs.getString("feedback");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return feedback;
    }

    // Delete a session by title (for test cleanup)
    public void deleteSessionByTitle(String title) {
        String query = "DELETE FROM Session WHERE title = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, title);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete an attendee by email (for test cleanup)
    public void deleteAttendeeByEmail(String email) {
        String query = "DELETE FROM Attendee WHERE email = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get attendee ID by email
    public static int getAttendeeIdByEmail(String email) {
        try (Connection conn = connect()) {
            return getAttendeeIdByEmail(conn, email);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Find session ID by title
    public static int findSessionIdByTitle(String title) {
        try (Connection conn = connect()) {
            return findSessionIdByTitle(conn, title);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Check if a session link exists for an attendee
    public static boolean isAttendeeSessionLinked(int attendeeId, int sessionId) {
        try (Connection conn = connect()) {
            return isAttendeeSessionLinked(conn, attendeeId, sessionId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to retrieve attendee ID by email within a transaction
    private static int getAttendeeIdByEmail(Connection conn, String email) throws SQLException {
        String query = "SELECT id FROM Attendee WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1;
    }
}
