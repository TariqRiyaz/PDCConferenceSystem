import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private SessionService sessionService; // Service class for session-related logic

    public MainFrame() {
        // Set up the main frame (window) properties
        setTitle("Conference Management System"); // Set window title
        setSize(800, 600); // Set window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit application on window close
        setLocationRelativeTo(null); // Center the window on the screen

        // Setup main panel with CardLayout to allow switching between different views (panels)
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        // Initialize the different panels (views) for the application
        AttendeeRegistrationPanel registrationPanel = new AttendeeRegistrationPanel();
        ViewSessionsPanel viewSessionsPanel = new ViewSessionsPanel(sessionService);
        SubmitFeedbackPanel submitFeedbackPanel = new SubmitFeedbackPanel();

        // Add each panel to the main panel with a unique identifier for CardLayout
        mainPanel.add(registrationPanel, "Registration");
        mainPanel.add(viewSessionsPanel, "ViewSessions");
        mainPanel.add(submitFeedbackPanel, "SubmitFeedback");

        // Create a stylized menu panel with buttons to navigate between views
        JPanel menuPanel = createMenuPanel(cardLayout, mainPanel);

        // Add the menu panel to the left and the main panel in the center of the frame
        add(menuPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the menu panel with navigation buttons for different views.
     *
     * @param cardLayout  The layout manager for switching views in mainPanel.
     * @param mainPanel   The main panel holding different views.
     * @return            The constructed menu panel.
     */
    private JPanel createMenuPanel(CardLayout cardLayout, JPanel mainPanel) {
        // Set up menu panel with a grid layout and custom styling
        JPanel menuPanel = new JPanel(new GridLayout(4, 1, 5, 5)); // 4 rows, 1 column with spacing
        menuPanel.setBackground(new Color(240, 240, 240)); // Light grey background
        menuPanel.setBorder(BorderFactory.createTitledBorder("Menu")); // Add a titled border

        // Create buttons for navigation, each linked to a different panel in mainPanel
        JButton btnRegister = createMenuButton("Register Attendee", e -> cardLayout.show(mainPanel, "Registration"));
        JButton btnViewSessions = createMenuButton("View Sessions", e -> cardLayout.show(mainPanel, "ViewSessions"));
        JButton btnSubmitFeedback = createMenuButton("Submit Feedback", e -> cardLayout.show(mainPanel, "SubmitFeedback"));
        JButton btnExit = createMenuButton("Exit", e -> System.exit(0)); // Exit application on click

        // Add buttons to the menu panel
        menuPanel.add(btnRegister);
        menuPanel.add(btnViewSessions);
        menuPanel.add(btnSubmitFeedback);
        menuPanel.add(btnExit);

        return menuPanel; // Return the completed menu panel
    }

    /**
     * Helper method to create a stylized button for the menu.
     *
     * @param text   The text displayed on the button.
     * @param action The action performed when the button is clicked.
     * @return       The constructed button.
     */
    private JButton createMenuButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action); // Attach the action listener
        button.setBackground(new Color(70, 130, 180)); // Set button color to steel blue
        button.setForeground(Color.WHITE); // Set text color to white
        button.setFocusPainted(false); // Remove focus border for cleaner look
        return button;
    }

    public static void main(String[] args) {
        // Initialize and populate the database using a separate service class for setup
        SetupService.initializeAndPopulateDatabase();

        // Launch the main application frame in the event dispatch thread
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true); // Make the frame visible
        });
    }
}
