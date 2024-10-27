import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ViewSessionsPanel extends JPanel {
    private SessionService sessionService;  // Injected session service

    public ViewSessionsPanel(SessionService sessionService) {
        this.sessionService = new SessionService();  // Dependency Injection
        setLayout(new BorderLayout());

        // Label for the panel
        JLabel label = new JLabel("Available Sessions", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        add(label, BorderLayout.NORTH);

        // Create table and add to panel
        JTable sessionTable = createSessionTable();
        sessionTable.setRowHeight(25); // Set row height for readability
        sessionTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(new JScrollPane(sessionTable), BorderLayout.CENTER);
    }

    // Method to create and populate the session table
    private JTable createSessionTable() {
        String[] columnNames = {"Title", "Speaker"};
        Object[][] data = fetchSessionData();
        
        JTable table = new JTable(data, columnNames);
        table.setEnabled(false); // Read-only table
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        return table;
    }

    // Helper method to fetch and format session data for the table
    private Object[][] fetchSessionData() {
        List<Session> sessions = sessionService.getAllSessions();
        Object[][] data = new Object[sessions.size()][2];
        
        for (int i = 0; i < sessions.size(); i++) {
            data[i][0] = sessions.get(i).getTitle();
            data[i][1] = sessions.get(i).getSpeaker();
        }
        return data;
    }
}
