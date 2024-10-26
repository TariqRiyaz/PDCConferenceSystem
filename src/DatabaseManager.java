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
    
    // Method to add a new attendee
    public static void addAttendee(String name, String email, List<String> sessionTitles) {
        try (Connection conn = connect()) {
            // Insert attendee into Attendee table
            String insertAttendeeSQL = "INSERT INTO Attendee (name, email) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertAttendeeSQL, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.executeUpdate();

                // Get the generated attendee ID for use in the Attendee_Session table
                int attendeeId = -1;
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        attendeeId = rs.getInt(1);
                    }
                }

                // Insert sessions for the attendee in Attendee_Session table
                String insertAttendeeSessionSQL = "INSERT INTO Attendee_Session (attendee_id, session_id) VALUES (?, ?)";
                try (PreparedStatement sessionStmt = conn.prepareStatement(insertAttendeeSessionSQL)) {
                    for (String title : sessionTitles) {
                        int sessionId = findSessionIdByTitle(conn, title);
                        if (sessionId != -1) {  // Ensure the session exists
                            sessionStmt.setInt(1, attendeeId);
                            sessionStmt.setInt(2, sessionId);
                            sessionStmt.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Helper method to find session ID by title
    private static int findSessionIdByTitle(Connection conn, String title) throws SQLException {
        String query = "SELECT id FROM Session WHERE title = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, title);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
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
            if (!e.getSQLState().equals("X0Y32")) {  // X0Y32: Table already exists
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
    
     public static void displayAllAttendees() {
        String query = "SELECT id, name, email FROM Attendee";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("Attendee Table:");
            System.out.println("ID | Name | Email");
            System.out.println("----------------------------");
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");

                System.out.println(id + " | " + name + " | " + email);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

