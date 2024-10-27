import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SubmitFeedbackPanel extends JPanel {
    private JTextField txtEmail;
    private JTextArea txtFeedback;
    private JComboBox<String> sessionComboBox;

    public SubmitFeedbackPanel() {
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
        txtEmail.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = txtEmail.getText().trim();
                loadSessionsForAttendee(email);
            }
        });

        // Submit feedback action
        btnSubmitFeedback.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = txtEmail.getText().trim();
                String selectedSession = (String) sessionComboBox.getSelectedItem();
                String feedbackText = txtFeedback.getText().trim();

                // Validate feedback input
                if (selectedSession == null || selectedSession.equals("Select your registered session")) {
                    JOptionPane.showMessageDialog(null, "Please select a session to provide feedback.");
                    return;
                }
                if (feedbackText.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Feedback cannot be empty.");
                    return;
                }

                // Submit feedback to database
                DatabaseManager.submitFeedback(email, selectedSession, feedbackText);
                JOptionPane.showMessageDialog(null, "Thank you for your feedback!");

                // Clear feedback input fields after submission
                txtEmail.setText("");
                txtFeedback.setText("");
                sessionComboBox.removeAllItems();
                sessionComboBox.addItem("Select your registered session");
            }
        });
    }

    // Method to load sessions for an attendee from the database
    private void loadSessionsForAttendee(String email) {
        List<String> sessions = DatabaseManager.getSessionsForAttendee(email);
        System.out.println(sessions.size());
        
        // Clear and populate sessionComboBox with attendee's sessions
        sessionComboBox.removeAllItems();
        if (sessions.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sessions found for this attendee. Please register first.");
            sessionComboBox.addItem("Select your registered session");
        } else {
            for (String session : sessions) {
                sessionComboBox.addItem(session);
            }
        }
    }
}
