import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.*;

/**
 * Test class for DatabaseManager.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // Runs tests in alphabetical order
public class DatabaseManagerTest {

    private DatabaseManager dbManager;

    @BeforeClass
    public static void setUpClass() {
        // Initialize database and create tables once before all tests
        DatabaseManager.initializeDatabase();
    }

    @Before
    public void setUp() {
        // Initialize DatabaseManager instance and add specific test sessions
        dbManager = new DatabaseManager();
        addTestSessions();
    }

    @After
    public void tearDown() {
        // Clean up the test data after each test to maintain isolation
        clearTestData();
        dbManager = null;
    }

    // Helper method to add specific sessions for testing only
    private void addTestSessions() {
        // Add test-specific sessions with unique titles and speakers
        dbManager.addSession("Session 1", "Test Speaker 1");
        dbManager.addSession("Session 2", "Test Speaker 2");
    }

    // Helper method to clear only the data added for the tests
    private void clearTestData() {
        try (Connection conn = dbManager.connect()) {
            // Step 1: Delete rows from Attendee_Session that reference the test sessions
            String deleteAttendeeSessionSQL = "DELETE FROM Attendee_Session WHERE session_id IN (SELECT id FROM Session WHERE title = ? OR title = ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteAttendeeSessionSQL)) {
                pstmt.setString(1, "Session 1");
                pstmt.setString(2, "Session 2");
                pstmt.executeUpdate();
            }

            // Step 2: Delete the test sessions from the Session table
            dbManager.deleteSessionByTitle("Session 1");
            dbManager.deleteSessionByTitle("Session 2");

            // Step 3: Delete the test attendee from the Attendee table
            dbManager.deleteAttendeeByEmail("test@example.com");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tests the database connection
    @Test
    public void test1_Connect() throws Exception {
        System.out.println("connect");
        Connection result = dbManager.connect();
        assertNotNull("Database connection should not be null", result); // Verify connection is not null
        result.close();
    }

    // Verifies that the database is initialized properly
    @Test
    public void test2_InitializeDatabase() {
        System.out.println("initializeDatabase");
        assertTrue("Database should initialize without error", dbManager.isSessionTablePopulated()); // Check if session table has data
    }

    // Tests adding an attendee and linking them to sessions
    @Test
    public void test3_AddAttendee() {
        System.out.println("addAttendee");
        String name = "Test Attendee";
        String email = "test@example.com";
        List<String> sessionTitles = Arrays.asList("Session 1", "Session 2");

        // Add the attendee with the specified sessions
        dbManager.addAttendee(name, email, sessionTitles);

        // Verify the attendee is linked to the correct sessions
        List<String> sessions = dbManager.getSessionsForAttendee(email);
        assertEquals("Sessions should match", sessionTitles, sessions);

        // Verify the attendee was successfully added
        int attendeeId = dbManager.getAttendeeIdByEmail(email);
        assertTrue("Attendee should exist after adding", attendeeId != -1);
    }

    // Tests submitting feedback for a session and verifying it
    @Test
    public void test4_SubmitFeedback() {
        System.out.println("submitFeedback");
        String name = "Test Attendee";
        String email = "test@example.com";
        String sessionTitle = "Session 1";
        String feedback = "Great session!";

        // Ensure the attendee and session exist before submitting feedback
        dbManager.addAttendee(name, email, Arrays.asList(sessionTitle));
        int attendeeId = dbManager.getAttendeeIdByEmail(email);
        int sessionId = dbManager.findSessionIdByTitle(sessionTitle);

        assertTrue("Attendee should exist", attendeeId != -1);
        assertTrue("Session should exist", sessionId != -1);

        // Submit feedback for the specified session
        dbManager.submitFeedback(email, sessionTitle, feedback);

        // Retrieve and verify the feedback content
        String retrievedFeedback = dbManager.getFeedbackForSessionAndAttendee(email, sessionTitle);
        assertEquals("Feedback should match", feedback, retrievedFeedback);
    }

    // Tests retrieving sessions for an attendee
    @Test
    public void test5_GetSessionsForAttendee() {
        System.out.println("getSessionsForAttendee");
        String email = "test@example.com";

        // Add attendee with specified sessions to set up the test scenario
        dbManager.addAttendee("Test Attendee", email, Arrays.asList("Session 1", "Session 2"));

        // Retrieve sessions for the attendee and verify correctness
        List<String> result = dbManager.getSessionsForAttendee(email);
        assertNotNull("Result should not be null", result);
        assertEquals("Should return correct number of sessions", 2, result.size());
        assertTrue("Result should contain 'Session 1'", result.contains("Session 1"));
    }

}
