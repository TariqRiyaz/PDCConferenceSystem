import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SubmitFeedbackPanel extends JPanel {
    private JTextField txtEmail;
    private JTextArea txtFeedback;
    private JComboBox<String> sessionComboBox;
    private FeedbackService feedbackService; // Controller for feedback management

    public SubmitFeedbackPanel() {
        feedbackService = new FeedbackService(); // Initialize the feedback service

        setLayout(new BorderLayout());

        // North panel for email input
        JPanel emailPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JLabel lblEmail = new JLabel("Enter your email:");
        txtEmail = new JTextField();

        emailPanel.add(lblEmail);
        emailPanel.add(txtEmail);
        add(emailPanel, BorderLayout.NORTH);

        // Center panel for session selection and feedback input
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Session selection
        sessionComboBox = new JComboBox<>();
        sessionComboBox.addItem("Select your registered session");
        centerPanel.add(sessionComboBox, BorderLayout.NORTH);

        // Feedback text area
        txtFeedback = new JTextArea(5, 20);
        txtFeedback.setLineWrap(true);
        txtFeedback.setWrapStyleWord(true);
        centerPanel.add(new JScrollPane(txtFeedback), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Button to submit feedback
        JButton btnSubmitFeedback = new JButton("Submit Feedback");
        add(btnSubmitFeedback, BorderLayout.SOUTH);

        // Action listener to load sessions when email is entered
        txtEmail.addActionListener(e -> {
            String email = txtEmail.getText().trim();
            loadSessionsForAttendee(email);
        });

        // Submit feedback action
        btnSubmitFeedback.addActionListener(e -> submitFeedback());
    }

    // Method to load sessions for an attendee
    private void loadSessionsForAttendee(String email) {
        List<String> sessions = feedbackService.getSessionsForAttendee(email);
        
        sessionComboBox.removeAllItems();
        if (sessions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No sessions found for this attendee. Please register first.");
            sessionComboBox.addItem("Select your registered session");
        } else {
            sessionComboBox.addItem("Select your registered session");
            for (String session : sessions) {
                sessionComboBox.addItem(session);
            }
        }
    }

    // Method to submit feedback
    private void submitFeedback() {
        String email = txtEmail.getText().trim();
        String selectedSession = (String) sessionComboBox.getSelectedItem();
        String feedbackText = txtFeedback.getText().trim();

        if (selectedSession == null || selectedSession.equals("Select your registered session")) {
            JOptionPane.showMessageDialog(this, "Please select a session to provide feedback.");
            return;
        }
        if (feedbackText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Feedback cannot be empty.");
            return;
        }

        boolean success = feedbackService.submitFeedback(email, selectedSession, feedbackText);

        if (success) {
            JOptionPane.showMessageDialog(this, "Thank you for your feedback!");
            clearInputFields();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to submit feedback. Please try again.");
        }
    }

    // Clears the input fields after feedback submission
    private void clearInputFields() {
        txtEmail.setText("");
        txtFeedback.setText("");
        sessionComboBox.removeAllItems();
        sessionComboBox.addItem("Select your registered session");
    }
}
