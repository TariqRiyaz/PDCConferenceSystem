import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private ConferenceSystem conferenceSystem; // Instance to handle data interactions

    public MainFrame() {
        // Set up the main frame
        setTitle("Conference Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize conference system with file path
        conferenceSystem = new ConferenceSystem("conference_data.txt");
        conferenceSystem.loadParticipants();

        // Set layout and add a card layout for different panels
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);
        
        // Panels for each functionality
        AttendeeRegistrationPanel registrationPanel = new AttendeeRegistrationPanel();
        ViewSessionsPanel viewSessionsPanel = new ViewSessionsPanel();
        SubmitFeedbackPanel submitFeedbackPanel = new SubmitFeedbackPanel(); // Add SubmitFeedbackPanel

        mainPanel.add(registrationPanel, "Registration");
        mainPanel.add(viewSessionsPanel, "ViewSessions");
        mainPanel.add(submitFeedbackPanel, "SubmitFeedback"); // Add to card layout

        // Create menu buttons
        JButton btnRegister = new JButton("Register Attendee");
        JButton btnViewSessions = new JButton("View Sessions");
        JButton btnSubmitFeedback = new JButton("Submit Feedback");
        JButton btnExit = new JButton("Exit");

        JPanel menuPanel = new JPanel(new GridLayout(4, 1));
        menuPanel.add(btnRegister);
        menuPanel.add(btnViewSessions);
        menuPanel.add(btnSubmitFeedback);
        menuPanel.add(btnExit);
        
        // Main container layout
        add(menuPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        // Action Listeners to switch panels
        btnRegister.addActionListener(e -> cardLayout.show(mainPanel, "Registration"));
        btnViewSessions.addActionListener(e -> cardLayout.show(mainPanel, "ViewSessions"));
        btnSubmitFeedback.addActionListener(e -> cardLayout.show(mainPanel, "SubmitFeedback")); // Show SubmitFeedbackPanel
        btnExit.addActionListener(e -> System.exit(0)); // Exit program
    }
    
    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        // Check if the session table is populated
        if (!DatabaseManager.isSessionTablePopulated()) {
        System.out.println("populating database");
        DatabaseManager.populateDefaultSessions(); // Populate if empty
    }
         // Display all sessions to verify population
    DatabaseManager.displayAllSessions();
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
