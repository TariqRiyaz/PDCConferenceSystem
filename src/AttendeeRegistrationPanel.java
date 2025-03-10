import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AttendeeRegistrationPanel extends JPanel {
    private JTextField txtName, txtEmail;
    private JList<String> sessionList;
    private RegistrationService registrationService; // Instance of RegistrationService
    private SessionService sessionService;
    

    public AttendeeRegistrationPanel() {
        this.registrationService = new RegistrationService();
        this.sessionService = new SessionService();
        setLayout(new BorderLayout());

        // North panel for input fields
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.add(new JLabel("Name:"));
        txtName = new JTextField();
        inputPanel.add(txtName);
        
        inputPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        inputPanel.add(txtEmail);

        // Center panel for session selection
        JPanel sessionPanel = new JPanel(new BorderLayout());
        sessionPanel.add(new JLabel("Select Sessions to Attend:"), BorderLayout.NORTH);
        
        // Fetch session titles
        List<Session> sessions = sessionService.getAllSessions();
        String[] sessionTitles = sessions.stream().map(Session::getTitle).toArray(String[]::new);
        sessionList = new JList<>(sessionTitles);
        
        // Allow toggle selection
        sessionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        sessionList.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (isSelectedIndex(index0)) {
                    removeSelectionInterval(index0, index1);
                } else {
                    addSelectionInterval(index0, index1);
                }
            }
        });
        
        sessionPanel.add(new JScrollPane(sessionList), BorderLayout.CENTER);

        // South panel for buttons
        JPanel buttonPanel = new JPanel();
        JButton btnRegister = new JButton("Register");

        buttonPanel.add(btnRegister);

        // Add panels to main layout
        add(inputPanel, BorderLayout.NORTH);
        add(sessionPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Register action listener
        btnRegister.addActionListener(e -> {
            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();
            List<String> selectedSessions = sessionList.getSelectedValuesList();

            try {
                boolean success = registrationService.registerAttendee(name, email, selectedSessions);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Attendee Registered Successfully");
                    txtName.setText("");
                    txtEmail.setText("");
                    sessionList.clearSelection();
                } else {
                    JOptionPane.showMessageDialog(this, "Registration Failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
