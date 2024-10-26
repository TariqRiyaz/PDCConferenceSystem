
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ViewSessionsPanel extends JPanel {
    public ViewSessionsPanel() {
        setLayout(new BorderLayout());

        // Label for the panel
        JLabel label = new JLabel("Available Sessions", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        add(label, BorderLayout.NORTH);

        // Column names for the JTable
        String[] columnNames = {"Title", "Speaker"};

        // Retrieve session data
        List<Session> sessions = Session.getAllSessions();
        Object[][] data = new Object[sessions.size()][2];
        
        // Populate table data
        for (int i = 0; i < sessions.size(); i++) {
            data[i][0] = sessions.get(i).getTitle();
            data[i][1] = sessions.get(i).getSpeaker();
        }

        // Create table with session data
        JTable sessionTable = new JTable(data, columnNames);
        sessionTable.setEnabled(false); // Read-only table
        
        // Add table to a scroll pane and to the panel
        add(new JScrollPane(sessionTable), BorderLayout.CENTER);
    }
}
