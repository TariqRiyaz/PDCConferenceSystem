import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DatabaseManager {
    // JDBC URL for embedded Derby database
    private static final String DB_URL = "jdbc:derby://localhost:1527/conferenceDB;create=true";

    // Method to connect to the database
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Method to add a new attendee with multiple sessions
    public static void addAttendee(String name, String email, List<String> sessionTitles) {
        try (Connection conn = connect()) {
            conn.setAutoCommit(false); // Begin transaction
            int attendeeId = -1;

            // Check if attendee already exists
            String checkAttendeeSQL = "SELECT id FROM Attendee WHERE email = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkAttendeeSQL)) {
                checkStmt.setString(1, email);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        attendeeId = rs.getInt("id"); // Attendee already exists
                    }
                }
            }

            // If attendee doesn't exist, insert them
            if (attendeeId == -1) {
                String insertAttendeeSQL = "INSERT INTO Attendee (name, email) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertAttendeeSQL,
                        Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, name);
                    pstmt.setString(2, email);
                    pstmt.executeUpdate();

                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            attendeeId = rs.getInt(1); // Get newly generated ID
                        }
                    }
                }
            }

            // Link each session to the attendee in the Attendee_Session table if not
            // already linked
            String insertAttendeeSessionSQL = "INSERT INTO Attendee_Session (attendee_id, session_id) VALUES (?, ?)";
            try (PreparedStatement sessionStmt = conn.prepareStatement(insertAttendeeSessionSQL)) {
                for (String title : sessionTitles) {
                    int sessionId = findSessionIdByTitle(conn, title);
                    System.out.println(attendeeId);
                    System.out.println(sessionId);
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

    // Helper method to check if attendee-session link already exists
    private static boolean isAttendeeSessionLinked(Connection conn, int attendeeId, int sessionId) throws SQLException {
        String checkLinkSQL = "SELECT COUNT(*) FROM Attendee_Session WHERE attendee_id = ? AND session_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(checkLinkSQL)) {
            pstmt.setInt(1, attendeeId);
            pstmt.setInt(2, sessionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true; // Link already exists
                }
            }
        }
        return false; // Link does not exist
    }

    // Helper method to find session ID by title
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
        return -1; // Return -1 if session not found
    }
       

    // Initialize database (run only once to create the tables)
    public static void initializeDatabase() {
        try (Connection conn = connect();
                Statement stmt = conn.createStatement()) {

            // Create Attendee table if it doesn't exist
            try {
                String createAttendeeTable = "CREATE TABLE Attendee (" +
                        "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                        "name VARCHAR(100), " +
                        "email VARCHAR(100) UNIQUE)";
                stmt.executeUpdate(createAttendeeTable);
            } catch (SQLException e) {
                if (!e.getSQLState().equals("X0Y32")) { // X0Y32: Table already exists
                    e.printStackTrace();
                }
            }

            // Create Session table if it doesn't exist
            try {
                String createSessionTable = "CREATE TABLE Session (" +
                        "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                        "title VARCHAR(200), " +
                        "speaker VARCHAR(100))";
                stmt.executeUpdate(createSessionTable);
            } catch (SQLException e) {
                if (!e.getSQLState().equals("X0Y32")) {
                    e.printStackTrace();
                }
            }

            // Create Attendee_Session table if it doesn't exist
            try {
                String createAttendeeSessionTable = "CREATE TABLE Attendee_Session (" +
                        "attendee_id INT, " +
                        "session_id INT, " +
                        "FOREIGN KEY (attendee_id) REFERENCES Attendee(id), " +
                        "FOREIGN KEY (session_id) REFERENCES Session(id))";
                stmt.executeUpdate(createAttendeeSessionTable);
            } catch (SQLException e) {
                if (!e.getSQLState().equals("X0Y32")) {
                    e.printStackTrace();
                }
            }

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void displayAttendeeSessions() {
        String query = "SELECT a.name, s.title FROM Attendee a " +
                "JOIN Attendee_Session ON a.id = Attendee_Session.attendee_id " +
                "JOIN Session s ON Attendee_Session.session_id = s.id";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

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

    public static void populateDefaultSessions() {
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Session (title, speaker) VALUES (?, ?)")) {

            // Check if there are any sessions in the table
            String checkQuery = "SELECT COUNT(*) AS count FROM Session";
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(checkQuery)) {

                if (rs.next() && rs.getInt("count") > 0) {
                    System.out.println("Sessions already exist, skipping population.");
                    return;
                }
            }

            // Insert default sessions
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

    public static boolean isSessionTablePopulated() {
        String query = "SELECT COUNT(*) AS count FROM Session";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                int count = rs.getInt("count");
                return count > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void displayAllSessions() {
        String query = "SELECT id, title, speaker FROM Session";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

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

}
