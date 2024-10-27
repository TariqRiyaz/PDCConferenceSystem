import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
public class MainFrame extends JFrame {
    private SessionService sessionService;

    public MainFrame() {
        setTitle("Conference Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Setup main panel with CardLayout for different views
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        AttendeeRegistrationPanel registrationPanel = new AttendeeRegistrationPanel();
        ViewSessionsPanel viewSessionsPanel = new ViewSessionsPanel(sessionService);
        SubmitFeedbackPanel submitFeedbackPanel = new SubmitFeedbackPanel();

        mainPanel.add(registrationPanel, "Registration");
        mainPanel.add(viewSessionsPanel, "ViewSessions");
        mainPanel.add(submitFeedbackPanel, "SubmitFeedback");

        // Create a stylized menu panel
        JPanel menuPanel = createMenuPanel(cardLayout, mainPanel);

        add(menuPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createMenuPanel(CardLayout cardLayout, JPanel mainPanel) {
        JPanel menuPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        menuPanel.setBackground(new Color(240, 240, 240));
        menuPanel.setBorder(BorderFactory.createTitledBorder("Menu"));

        JButton btnRegister = createMenuButton("Register Attendee", e -> cardLayout.show(mainPanel, "Registration"));
        JButton btnViewSessions = createMenuButton("View Sessions", e -> cardLayout.show(mainPanel, "ViewSessions"));
        JButton btnSubmitFeedback = createMenuButton("Submit Feedback", e -> cardLayout.show(mainPanel, "SubmitFeedback"));
        JButton btnExit = createMenuButton("Exit", e -> System.exit(0));

        menuPanel.add(btnRegister);
        menuPanel.add(btnViewSessions);
        menuPanel.add(btnSubmitFeedback);
        menuPanel.add(btnExit);

        return menuPanel;
    }

    private JButton createMenuButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    public static void main(String[] args) {
        SetupService.initializeAndPopulateDatabase(); // Move setup logic here for separation of concerns

        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
